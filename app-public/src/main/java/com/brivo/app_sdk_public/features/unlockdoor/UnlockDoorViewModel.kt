package com.brivo.app_sdk_public.features.unlockdoor

import android.os.CancellationSignal
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.app_sdk_public.features.unlockdoor.navigation.UnlockDoorArgs
import com.brivo.common_app.features.unlockdoor.model.DoorState
import com.brivo.common_app.features.unlockdoor.model.UnlockDoorUIEvent
import com.brivo.common_app.features.unlockdoor.usecase.InitializeBrivoSDKLocalAuthUseCase
import com.brivo.common_app.features.unlockdoor.usecase.UnlockDoorUseCase
import com.brivo.common_app.features.unlockdoor.usecase.UnlockNearestBLEAccessPointUseCase
import com.brivo.sdk.enums.AccessPointCommunicationState
import com.brivo.sdk.model.BrivoResult
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
            isMagicButton = unlockDoorArgs.passId == "",
            hasTrustedNetwork = unlockDoorArgs.hasTrustedNetwork
        )
    )
    val state: StateFlow<UnlockDoorViewState> = _state

    private var cancellationSignal = CancellationSignal()

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
}

data class UnlockDoorViewState(
    val isMagicButton: Boolean = false,
    val showSnackbar: Boolean = false,
    val doorState: DoorState = DoorState.LOCKED,
    val accessPointName: String = "",
    val alertMessage: String = "",
    val hasTrustedNetwork: Boolean = false
)
