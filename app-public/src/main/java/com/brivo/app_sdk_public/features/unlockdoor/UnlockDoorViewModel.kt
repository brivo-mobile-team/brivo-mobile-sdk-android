package com.brivo.app_sdk_public.features.unlockdoor

import android.os.CancellationSignal
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.app_sdk_public.features.unlockdoor.navigation.UnlockDoorArgs
import com.brivo.common_app.domain.usecases.GetAccessPointDetailsUseCase
import com.brivo.common_app.features.unlockdoor.model.DoorDetailsBottomSheetUIModel
import com.brivo.common_app.features.unlockdoor.model.DoorState
import com.brivo.common_app.features.unlockdoor.model.UnlockDoorUIEvent
import com.brivo.common_app.features.unlockdoor.usecase.InitializeBrivoSDKLocalAuthUseCase
import com.brivo.common_app.features.unlockdoor.usecase.UnlockDoorUseCase
import com.brivo.common_app.features.unlockdoor.usecase.UnlockNearestBLEAccessPointUseCase
import com.brivo.common_app.model.DomainState
import com.brivo.sdk.enums.AccessPointCommunicationState
import com.brivo.sdk.enums.DoorType
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.onair.model.BrivoBluetoothReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnlockDoorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val initializeBrivoSDKLocalAuthUseCase: InitializeBrivoSDKLocalAuthUseCase,
    private val unlockNearestBLEAccessPointUseCase: UnlockNearestBLEAccessPointUseCase,
    private val unlockDoorUseCase: UnlockDoorUseCase,
    private val getAccessPointDetailsUseCase: GetAccessPointDetailsUseCase
) : ViewModel() {

    private val unlockDoorArgs: UnlockDoorArgs = UnlockDoorArgs(
        savedStateHandle
    )

    private val _state = MutableStateFlow(
        UnlockDoorViewState(
            accessPointName = unlockDoorArgs.accessPointName,
            accessPointType = DoorType.valueOf(unlockDoorArgs.accessPointType.ifEmpty {
                DoorType.UNKNOWN.name
            }),
            isMagicButton = unlockDoorArgs.passId == "",
            hasTrustedNetwork = unlockDoorArgs.hasTrustedNetwork
        )
    )
    val state: StateFlow<UnlockDoorViewState> = _state

    private var cancellationSignal = CancellationSignal()

    init {
        viewModelScope.launch {
            updateDoorDetailsBootmSheet()
        }
    }

    fun onEvent(event: UnlockDoorUIEvent) {

        when (event) {
            is UnlockDoorUIEvent.InitLocalAuth -> {
                initBrivoSDKLocalAuth(
                    event.title,
                    event.message,
                    event.negativeButtonText,
                    event.description
                )
            }

            is UnlockDoorUIEvent.UnlockDoor -> {
                viewModelScope.launch {
                    if (unlockDoorArgs.passId.isEmpty()) {
                        unlockDoorWithMagicButton(activity = event.activity)
                    } else {
                        unlockDoor(activity = event.activity)
                    }
                }
            }

            is UnlockDoorUIEvent.DoorUnlocked -> {
                updateDoorState(DoorState.UNLOCKED)
            }

            is UnlockDoorUIEvent.DoorLocked -> {
                updateDoorState(DoorState.LOCKED)
            }

            is UnlockDoorUIEvent.CancelDoorUnlock -> {
                cancellationSignal.cancel()
            }

            is UnlockDoorUIEvent.UpdateAlertMessage -> {
                updateAlertMessage(alertMessage = event.message)
            }

            is UnlockDoorUIEvent.DismissDormakabaTooltip -> {
                dismissDormakabaUnlockTooltip()
            }
        }
    }

    private suspend fun updateDoorDetailsBootmSheet() {
        when (val result = getAccessPointDetailsUseCase.execute(unlockDoorArgs.accessPointId)) {
            is DomainState.Failed -> {
                updateAlertMessage(result.error)
            }
            is DomainState.Success -> {
                val accessPoint = result.data
                _state.update {
                    it.copy(
                        doorDetailsBottomSheetUIModel = DoorDetailsBottomSheetUIModel(
                            siteId = accessPoint.id,
                            siteName = accessPoint.siteName,
                            doorType = accessPoint.doorType,
                            bluetoothReader = accessPoint.bluetoothReader,
                            controlLockSerialNumber = accessPoint.controlLockSerialNumber,
                            isTwoFactorEnabled = accessPoint.isTwoFactorEnabled,
                            dormakabaMobilePassEnabled = false
                        )
                    )
                }
            }
        }
    }

    fun updateShouldShowBottomSheet(newValue: Boolean) {
        _state.update {
            it.copy(
                showBottomSheet = newValue
            )
        }
    }

    private fun initBrivoSDKLocalAuth(
        title: String,
        message: String,
        negativeButtonText: String,
        description: String
    ) {
        viewModelScope.launch {
            initializeBrivoSDKLocalAuthUseCase.execute(
                title = title,
                message = message,
                negativeButtonText = negativeButtonText,
                description = description
            )

        }
    }

    private fun unlockDoorWithMagicButton(activity: FragmentActivity) {
        viewModelScope.launch {
            updateDoorState(DoorState.UNLOCKING)
            unlockNearestBLEAccessPointUseCase.execute(
                activity = activity
            ).collect {
                processUnlockDoorEvent(it)
            }
        }

    }

    private fun unlockDoor(activity: FragmentActivity) {
        viewModelScope.launch {
            checkShowDormakabaUnlockTooltip(doorType = _state.value.accessPointType)
            updateDoorState(DoorState.UNLOCKING)
            unlockDoorUseCase.execute(
                passId = unlockDoorArgs.passId,
                accessPointId = unlockDoorArgs.accessPointId,
                activity = activity

            ).collect {
                processUnlockDoorEvent(it)
            }
        }
    }

    private fun processUnlockDoorEvent(result: BrivoResult) {
        viewModelScope.launch {
            when (result.communicationState) {
                AccessPointCommunicationState.SUCCESS -> {
                    Log.d("Unlock result", "Success")
                    onUnlockSuccess()
                }

                AccessPointCommunicationState.FAILED -> {
                    Log.d("Unlock result", "Failed")
                    onUnlockFailed()
                }

                AccessPointCommunicationState.SHOULD_CONTINUE,
                AccessPointCommunicationState.SCANNING, AccessPointCommunicationState.AUTHENTICATE,
                AccessPointCommunicationState.CONNECTING, AccessPointCommunicationState.COMMUNICATING,
                AccessPointCommunicationState.ON_CLOSEST_READER -> {
                    Log.d("Unlock door state", result.communicationState.name)
                }

                AccessPointCommunicationState.SCANNING_COOLDOWN -> {
                    Log.d("Unlock door state", "Scanning cooldown")
                    updateAlertMessage(
                        alertMessage = "Scanning too frequently. Please wait ${result.scanCooldownDurationInSeconds} seconds before trying again."
                    )
                    onUnlockFailed()
                }
            }
        }
    }

    private suspend fun onUnlockSuccess() {
        updateDoorState(DoorState.UNLOCKED)
        updateShowSnackbar(true)
        cancellationSignal.cancel()
        delay(5000)
        updateDoorState(DoorState.LOCKED)
        updateShowSnackbar(false)
    }

    private suspend fun onUnlockFailed() {
        updateDoorState(DoorState.LOCKED)
        updateShowSnackbar(true)
        cancellationSignal.cancel()
        delay(5000)
        updateShowSnackbar(false)
    }

    private fun updateDoorState(state: DoorState) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    doorState = state
                )
            }
        }
    }

    private fun updateAlertMessage(alertMessage: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    alertMessage = alertMessage
                )
            }
        }
    }

    private fun updateShowSnackbar(showSnackbar: Boolean) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showSnackbar = showSnackbar
                )
            }
        }
    }

    private fun checkShowDormakabaUnlockTooltip(doorType: DoorType) {
//        if (doorType == DoorType.DORMAKABA) { // todo uncomment if dormakaba is supported on public SDK
//            viewModelScope.launch {
//                _state.update {
//                    it.copy(
//                        showDormakabaUnlockTooltip = true
//                    )
//                }
//            }
//        }
    }

    private fun dismissDormakabaUnlockTooltip() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showDormakabaUnlockTooltip = false
                )
            }
        }
    }
}

data class UnlockDoorViewState(
    val isMagicButton: Boolean = false,
    val showSnackbar: Boolean = false,
    val doorState: DoorState = DoorState.LOCKED,
    val accessPointName: String = "",
    val accessPointType: DoorType = DoorType.UNKNOWN,
    val alertMessage: String = "",
    val hasTrustedNetwork: Boolean = false,
    val showDormakabaUnlockTooltip: Boolean = false,
    val showBottomSheet: Boolean = false,
    val doorDetailsBottomSheetUIModel: DoorDetailsBottomSheetUIModel = DoorDetailsBottomSheetUIModel(
        siteId = "",
        siteName = "",
        doorType = DoorType.UNKNOWN,
        isTwoFactorEnabled = false,
        bluetoothReader = BrivoBluetoothReader(),
        controlLockSerialNumber = "",
        dormakabaMobilePassEnabled = false
    )
)
