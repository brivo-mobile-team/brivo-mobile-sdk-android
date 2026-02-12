package com.brivo.common_app.features.unlockdoor.model

import com.brivo.sdk.enums.DoorType

data class DoorDetailsBottomSheetUIModel(
    val accessPointId: String = "",
    val doorType: DoorType = DoorType.UNKNOWN,
    val doorModel: String = "",
    val lockId: String = "",
    val readerId: String = "",
    val twoFactorStatus: Boolean = false,
    val minimumPanelRssi: String = "",
)
