package com.brivo.common_app.features.devices.model

import com.brivo.sdk.onair.model.BrivoThermostat

data class ResideoThermostatUIModel(
    val id: String,
    val name: String,
    val currentTemperature: String
)

fun BrivoThermostat.toResideoThermostatUIModel() = ResideoThermostatUIModel(
    id = id.toString(),
    name = name,
    currentTemperature = temperature.toString()
)
