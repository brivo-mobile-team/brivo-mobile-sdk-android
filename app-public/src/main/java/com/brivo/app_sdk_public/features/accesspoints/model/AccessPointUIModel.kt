package com.brivo.app_sdk_public.features.accesspoints.model

import com.brivo.sdk.enums.DoorType
import com.brivo.sdk.onair.model.BrivoAccessPoint

data class AccessPointUIModel(
    val id: String,
    val accessPointName: String,
    val doorType: DoorType
)

fun BrivoAccessPoint.toAccessPointUIModel() =
    AccessPointUIModel(
        id = this.id,
        accessPointName = this.name,
        doorType = this.doorType
    )
