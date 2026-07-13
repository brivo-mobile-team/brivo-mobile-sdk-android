package com.brivo.common_app.features.continuousscanner

import androidx.annotation.StringRes
import com.brivo.sdk.enums.DoorType

/**
 * Represents the persistent state of the Continuous Scanner
 */
enum class ContinuousScannerUIState {
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

data class ContinuousScannerViewState(
    val uiState: ContinuousScannerUIState = ContinuousScannerUIState.SCANNING,
    val nearestDeviceName: String = "",
    val nearestDeviceDoorType: DoorType = DoorType.UNKNOWN,
    val isButtonEnabled: Boolean = false,
    val unlockStatus: UnlockStatus = UnlockStatus.Idle,
    val scanningErrorMessage: String? = null
)
