package com.brivo.app_sdk_public.features.unlockdoor.model

sealed class UnlockDoorUIEvent {

    data object CheckPermissions : UnlockDoorUIEvent()

    data object ResetBackgroundPermissionDialog : UnlockDoorUIEvent()

    data object ResetBluetoothPermissionDialog : UnlockDoorUIEvent()

    data object UnlockDoor : UnlockDoorUIEvent()

    data object DoorUnlocked : UnlockDoorUIEvent()

    data object DoorLocked : UnlockDoorUIEvent()

    data object CancelDoorUnlock : UnlockDoorUIEvent()

    data class UpdateAlertMessage(val message: String) : UnlockDoorUIEvent()

    data class InitLocalAuth(
        val title: String,
        val message: String,
        val negativeButtonText: String,
        val description: String
    ) : UnlockDoorUIEvent()
}