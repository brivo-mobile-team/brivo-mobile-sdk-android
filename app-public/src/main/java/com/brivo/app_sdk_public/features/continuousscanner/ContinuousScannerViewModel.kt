package com.brivo.app_sdk_public.features.continuousscanner

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.common_app.R
import com.brivo.common_app.domain.usecases.GetBrivoSDKLocallyStoredPassesUseCase
import com.brivo.common_app.domain.usecases.GetPassFromAccessPointIdUseCase
import com.brivo.common_app.features.continuousscanner.BleDeviceUIModel
import com.brivo.common_app.features.continuousscanner.ContinousScanForNearbyDevicesUseCase
import com.brivo.common_app.features.continuousscanner.ContinuousScannerUIState
import com.brivo.common_app.features.continuousscanner.ContinuousScannerViewState
import com.brivo.common_app.features.continuousscanner.UnlockStatus
import com.brivo.common_app.features.unlockdoor.usecase.UnlockDoorUseCase
import com.brivo.common_app.model.DomainState
import com.brivo.sdk.access.continuousscanning.ContinuousScanningErrors
import com.brivo.sdk.enums.AccessPointCommunicationState
import com.brivo.sdk.enums.DoorType
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.scanningcache.NearbyDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ContinuousScannerViewModel @Inject constructor(
    private val getBrivoSDKLocallyStoredPassesUseCase: GetBrivoSDKLocallyStoredPassesUseCase,
    private val continousScanForNearbyDevicesUseCase: ContinousScanForNearbyDevicesUseCase,
    private val getPassFromAccessPointIdUseCase: GetPassFromAccessPointIdUseCase,
    private val unlockDoorUseCase: UnlockDoorUseCase
) : ViewModel() {

    private val discoveredNearbyDevices = MutableStateFlow<List<NearbyDevice>>(listOf())
    private var isScanningStarted = false

    private val _scanningErrors = MutableSharedFlow<String>(extraBufferCapacity = 64)
    private val _scanningErrorMessage = MutableStateFlow<String?>(null)

    val discoveredDevices = discoveredNearbyDevices.map { nearbyDevices ->
        nearbyDevices.map { nearbyDevice ->
            BleDeviceUIModel(
                getDeviceNameFromReaderUUID(nearbyDevice.readerUUID.value),
                nearbyDevice.readerUUID.value,
                nearbyDevice.rssiValue
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    private val _unlockingState = MutableStateFlow<UnlockStatus>(UnlockStatus.Idle)

    private val _continuousScannerUIState = MutableStateFlow(ContinuousScannerUIState.SCANNING)

    private val nearestDeviceInfo = discoveredNearbyDevices
        .debounce(100)
        .map { devices ->
            if (devices.isEmpty()) {
                NearestDeviceInfo("", DoorType.UNKNOWN)
            } else {
                val nearest = devices.first()
                NearestDeviceInfo(
                    name = getDeviceNameFromReaderUUID(nearest.readerUUID.value),
                    doorType = nearest.doorType
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, NearestDeviceInfo("", DoorType.UNKNOWN))

    val continuousScannerState = combine(
        nearestDeviceInfo,
        _unlockingState,
        _continuousScannerUIState,
        _scanningErrorMessage
    ) { deviceInfo, unlockState, continuousScannerUIState, errorMessage ->
        ContinuousScannerViewState(
            uiState = continuousScannerUIState,
            nearestDeviceName = deviceInfo.name,
            nearestDeviceDoorType = deviceInfo.doorType,
            isButtonEnabled = unlockState == UnlockStatus.Idle && deviceInfo.name.isNotEmpty(),
            unlockStatus = unlockState,
            scanningErrorMessage = errorMessage
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, ContinuousScannerViewState())

    init {
        viewModelScope.launch {
            val pendingErrors = mutableListOf<String>()
            var debounceJob: Job? = null

            _scanningErrors.collect { error ->
                pendingErrors.add(error)
                debounceJob?.cancel()
                debounceJob = launch {
                    delay(200.milliseconds)
                    _scanningErrorMessage.update { pendingErrors.joinToString("\n") }
                    pendingErrors.clear()
                    delay(5.seconds)
                    _scanningErrorMessage.update { null }
                }
            }
        }
    }

    private data class NearestDeviceInfo(val name: String, val doorType: DoorType)

    fun startScanning() {
        if (isScanningStarted) return
        isScanningStarted = true

        viewModelScope.launch {
            continousScanForNearbyDevicesUseCase.execute(loadPasses()).collect { scanningResults ->
                scanningResults.onScanResults {
                    discoveredNearbyDevices.emit(it)
                    _continuousScannerUIState.update { ContinuousScannerUIState.SCANNING }
                }.onScanState {
                    _continuousScannerUIState.update { ContinuousScannerUIState.BLE_OFF }
                }.onError {
                    val message = when (it) {
                        is ContinuousScanningErrors.FailureForInternalScanner ->
                            "${it.scanType.name}: ${it.error.message}"
                        is ContinuousScanningErrors.FailedToStartScan ->
                            "Failed to start scan: ${it.error.message}"
                    }
                    _scanningErrors.tryEmit(message)
                }
            }
        }
    }

    fun dismissScanningError() {
        _scanningErrorMessage.update { null }
    }

    fun unlockNearestBLEAccessPoint(activity: FragmentActivity) {
        viewModelScope.launch {
            val nearestDevice = discoveredNearbyDevices.value.firstOrNull()

            if (nearestDevice == null) {
                showTransientThenIdle(UnlockStatus.Error(R.string.continuous_scanner_failed_to_unlock))
                return@launch
            }

            val pass = when (val result = getPassFromAccessPointIdUseCase.execute(nearestDevice.accessPointId)) {
                is DomainState.Success -> result.data
                is DomainState.Failed -> null
            }

            if (pass == null) {
                showTransientThenIdle(UnlockStatus.Error(R.string.continuous_scanner_failed_to_unlock))
                return@launch
            }

            _unlockingState.update { UnlockStatus.InProgress }
            unlockDoorUseCase.execute(
                passId = pass.pass,
                accessPointId = nearestDevice.accessPointId,
                activity = activity
            ).collect { result ->
                // Map SDK state to UI events/state
                when (result.communicationState) {
                    AccessPointCommunicationState.SUCCESS ->
                        showTransientThenIdle(UnlockStatus.Success)

                    AccessPointCommunicationState.FAILED ->
                        showTransientThenIdle(UnlockStatus.Error(R.string.continuous_scanner_failed_to_unlock))

                    else -> {
                        // SCANNING, CONNECTING, COMMUNICATING - keep InProgress state
                    }
                }
            }
        }
    }

    private suspend fun showTransientThenIdle(status: UnlockStatus) {
        _unlockingState.update { status }
        delay(2.seconds)
        _unlockingState.update { UnlockStatus.Idle }
    }

    private var passes: List<BrivoOnairPass>? = null
    private suspend fun getDeviceNameFromReaderUUID(readerUUID: String): String {
        val cached = passes ?: loadPasses().also { passes = it }
        return getAccessPointNameByReaderUUID(cached, readerUUID) ?: "NULL"
    }

    private suspend fun loadPasses(): List<BrivoOnairPass> =
        when (val result = getBrivoSDKLocallyStoredPassesUseCase.execute()) {
            is DomainState.Success -> result.data?.values?.toList().orEmpty()
            is DomainState.Failed -> emptyList()
        }

    private fun getAccessPointNameByReaderUUID(
        passes: List<BrivoOnairPass>,
        readerUUID: String
    ): String? = passes
        .asSequence()
        .flatMap { it.sites }
        .flatMap { it.accessPoints }
        .firstOrNull { accessPoint ->
            if (accessPoint.doorType == DoorType.ALLEGION_BLE || accessPoint.doorType == DoorType.ALLEGION) {
                accessPoint.controlLockSerialNumber.contains(readerUUID, ignoreCase = true)
            } else {
                accessPoint.bluetoothReader.readerUid.contains(readerUUID, ignoreCase = true)
            }
        }?.name
}
