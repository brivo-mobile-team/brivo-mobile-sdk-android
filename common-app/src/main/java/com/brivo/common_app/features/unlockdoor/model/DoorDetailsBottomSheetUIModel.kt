package com.brivo.common_app.features.unlockdoor.model

import com.brivo.sdk.enums.DoorType
import com.brivo.sdk.onair.model.BrivoBluetoothReader

data class DoorDetailsBottomSheetUIModel(
    val siteId: String,
    val siteName: String,
    val doorType: DoorType,
    val isTwoFactorEnabled: Boolean,
    val bluetoothReader: BrivoBluetoothReader,
    val controlLockSerialNumber: String,
    val dormakabaMobilePassEnabled: Boolean,
)
