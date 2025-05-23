package com.brivo.app_sdk_public.features.unlockdoor.presentation

import android.Manifest
import android.os.Build
import android.os.CancellationSignal
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.app_sdk_public.App
import com.brivo.app_sdk_public.features.unlockdoor.model.DoorState
import com.brivo.app_sdk_public.features.unlockdoor.model.UnlockDoorUIEvent
import com.brivo.app_sdk_public.features.unlockdoor.navigation.UnlockDoorArgs
import com.brivo.app_sdk_public.features.unlockdoor.usecase.InitializeBrivoSDKLocalAuthUseCase
import com.brivo.app_sdk_public.features.unlockdoor.usecase.UnlockDoorUseCase
import com.brivo.app_sdk_public.features.unlockdoor.usecase.UnlockNearestBLEAccessPointUseCase
import com.brivo.sdk.ble.core.BrivoLog
import com.brivo.sdk.enums.AccessPointCommunicationState
import com.brivo.sdk.model.BrivoResult
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
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
    private val unlockDoorUseCase: UnlockDoorUseCase
) : ViewModel() {

    private val unlockDoorArgs: UnlockDoorArgs = UnlockDoorArgs(
        savedStateHandle
    )

    private val _state = MutableStateFlow(
        UnlockDoorViewState(
            accessPointName = unlockDoorArgs.accessPointName,
            isMagicButton = unlockDoorArgs.passId == ""
        )
    )
    val state: StateFlow<UnlockDoorViewState> = _state

    private var cancellationSignal = CancellationSignal()

    fun onEvent(event: UnlockDoorUIEvent) {
        when (event) {
            is UnlockDoorUIEvent.CheckPermissions -> {
                checkBackgroundPermissions()
                checkBluetoothPermissions()
            }

            is UnlockDoorUIEvent.ResetBackgroundPermissionDialog -> {
                updateShowLocationPermissionRationaleDialog(false)
            }

            is UnlockDoorUIEvent.ResetBluetoothPermissionDialog -> {
                updateShowBluetoothPermissionRationaleDialog(false)
            }

            is UnlockDoorUIEvent.InitLocalAuth -> {
                initBrivoSDKLocalAuth(
                    event.title,
                    event.message,
                    event.negativeButtonText,
                    event.description
                )
            }

            is UnlockDoorUIEvent.UnlockDoor -> {
                if (unlockDoorArgs.passId.isEmpty()) {
                    unlockDoorWithMagicButton(activity = event.activity)
                } else {
                    unlockDoor(activity = event.activity)
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

    private fun checkBackgroundPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            Manifest.permission.ACCESS_FINE_LOCATION
        }
        Dexter
            .withContext(App.instance.applicationContext)
            .withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {}
                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    updateShowLocationPermissionRationaleDialog(true)
                }
            }).check()
    }

    private fun checkBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        Dexter.withContext(App.instance.applicationContext)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {}
                override fun onPermissionRationaleShouldBeShown(
                    request: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    updateShowBluetoothPermissionRationaleDialog(true)
                }
            }).check()
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
                    onUnlockSuccess(result)
                }

                AccessPointCommunicationState.FAILED -> {
                    onUnlockFailed(result)
                }

                AccessPointCommunicationState.SHOULD_CONTINUE -> {
                    BrivoLog.i("should continue")
                }

                AccessPointCommunicationState.SCANNING -> BrivoLog.i("scanning")
                AccessPointCommunicationState.AUTHENTICATE -> BrivoLog.i("authenticate")
                AccessPointCommunicationState.CONNECTING -> BrivoLog.i("connecting")
                AccessPointCommunicationState.COMMUNICATING -> BrivoLog.i("communicating")
                AccessPointCommunicationState.ON_CLOSEST_READER -> {
                    // TODO
                }
            }
        }
    }

    private suspend fun onUnlockSuccess(result: BrivoResult) {
        BrivoLog.i("onUnlockSuccess: ${result.communicationState.name}")
        updateDoorState(DoorState.UNLOCKED)
        updateShowSnackbar(true)
        cancellationSignal.cancel()
        delay(5000)
        updateDoorState(DoorState.LOCKED)
        updateShowSnackbar(false)
    }

    private suspend fun onUnlockFailed(result: BrivoResult) {
        BrivoLog.i("onUnlockFailed: ${result.error?.message}")
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

    private fun updateShowLocationPermissionRationaleDialog(showLocationPermissionRationaleDialog: Boolean) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showLocationPermissionRationaleDialog = showLocationPermissionRationaleDialog
                )
            }
        }
    }

    private fun updateShowBluetoothPermissionRationaleDialog(showBluetoothPermissionRationaleDialog: Boolean) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showBluetoothPermissionRationaleDialog = showBluetoothPermissionRationaleDialog
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

    data class UnlockDoorViewState(
        val isMagicButton: Boolean = false,
        val showSnackbar: Boolean = false,
        val doorState: DoorState = DoorState.LOCKED,
        val accessPointName: String = "",
        val alertMessage: String = "",
        val showLocationPermissionRationaleDialog: Boolean = false,
        val showBluetoothPermissionRationaleDialog: Boolean = false
    )
}
