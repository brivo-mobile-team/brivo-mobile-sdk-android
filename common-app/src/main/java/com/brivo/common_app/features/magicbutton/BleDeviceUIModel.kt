package com.brivo.common_app.features.magicbutton

data class BleDeviceUIModel(
    val name: String,
    val readerUUID: String,
    val rssi: Int
)
