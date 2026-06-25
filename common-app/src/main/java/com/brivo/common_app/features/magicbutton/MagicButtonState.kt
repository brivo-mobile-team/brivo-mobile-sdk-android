package com.brivo.common_app.features.magicbutton

import androidx.annotation.StringRes
import com.brivo.sdk.enums.DoorType

/**
 * Represents the persistent state of the Magic Button
 */
enum class MagicButtonUIState {
    SCANNING,
    BLE_OFF
}

/**
 * One-time events that happened during unlock
 * These should be consumed once and trigger temporary UI feedback
 */
sealed class UnlockStatus {
    data object Idle : UnlockStatus()

    data object InProgress : UnlockStatus()

    data object Success : UnlockStatus()

    data class Error(@StringRes val messageResId: Int) : UnlockStatus()
}

data class MagicButtonViewState(
    val uiState: MagicButtonUIState = MagicButtonUIState.SCANNING,
    val nearestDeviceName: String = "",
    val nearestDeviceDoorType: DoorType = DoorType.UNKNOWN,
    val isButtonEnabled: Boolean = false,
    val unlockStatus: UnlockStatus = UnlockStatus.Idle,
    val scanningErrorMessage: String? = null
)
