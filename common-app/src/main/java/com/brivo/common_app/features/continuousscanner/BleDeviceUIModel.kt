package com.brivo.common_app.features.continuousscanner

data class BleDeviceUIModel(
    val name: String,
    val readerUUID: String,
    val rssi: Int
)
