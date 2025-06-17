package com.brivo.app_sdk_public.features.unlockdoor.model

import androidx.fragment.app.FragmentActivity

sealed class UnlockDoorUIEvent {
    data object DoorUnlocked : UnlockDoorUIEvent()

    data object DoorLocked : UnlockDoorUIEvent()

    data object CancelDoorUnlock : UnlockDoorUIEvent()

    data class UnlockDoor(val activity: FragmentActivity) : UnlockDoorUIEvent()

    data class UpdateAlertMessage(val message: String) : UnlockDoorUIEvent()

    data class InitLocalAuth(
        val title: String,
        val message: String,
        val negativeButtonText: String,
        val description: String
    ) : UnlockDoorUIEvent()
}
