package com.brivo.common_app.features.accesspoints.model

import com.brivo.sdk.enums.DoorType
import com.brivo.sdk.onair.model.BrivoAccessPoint
import com.brivo.sdk.onair.model.ReaderType

data class AccessPointUIModel(
    val id: String,
    val accessPointName: String,
    val doorType: DoorType
)

fun BrivoAccessPoint.toAccessPointUIModel() =
    AccessPointUIModel(
        id = this.id,
        accessPointName = this.name ?: "",
        doorType = if (this.readerType == ReaderType.HID_ORIGO) {
            DoorType.HID_ORIGO
        } else {
            this.doorType
        }
    )
