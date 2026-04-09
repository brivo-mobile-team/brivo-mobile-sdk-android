package com.brivo.common_app.features.thermostat.presentation

import com.brivo.common_app.features.thermostat.model.RangeEdit
import com.brivo.common_app.features.thermostat.model.ThermostatMode
import com.brivo.common_app.features.thermostat.model.ThermostatUIEvent
import kotlin.math.roundToInt

internal class ThermostatRangeController(
    private val minTemp: Float,
    private val maxTemp: Float,
    private val isCelsius: Boolean,
    private val getLow: () -> Float,
    private val getHigh: () -> Float,
    private val onEvent: (ThermostatUIEvent) -> Unit
) {

    private fun snap(value: Float): Float {
        val step = if (isCelsius) 0.5f else 1.0f
        return (value / step).roundToInt() * step
    }

    fun applyLow(value: Float, mode: ThermostatMode) {
        val snapped = snap(value).coerceIn(minTemp, maxTemp)
        val final = if (mode == ThermostatMode.AUTO) snapped.coerceAtMost(getHigh()) else snapped
        onEvent(ThermostatUIEvent.UpdateLowTemp(final))
    }

    fun applyHigh(value: Float, mode: ThermostatMode) {
        val snapped = snap(value).coerceIn(minTemp, maxTemp)
        val final = if (mode == ThermostatMode.AUTO) snapped.coerceAtLeast(getLow()) else snapped
        onEvent(ThermostatUIEvent.UpdateHighTemp(final))
    }

    fun step(mode: ThermostatMode, rangeEdit: RangeEdit, deltaSteps: Int) {
        val stepSize = if (isCelsius) 0.5f else 1.0f
        val change = deltaSteps * stepSize
        when (mode) {
            ThermostatMode.AUTO ->
                if (rangeEdit == RangeEdit.LOW) applyLow(getLow() + change, mode)
                else applyHigh(getHigh() + change, mode)

            ThermostatMode.HEAT -> applyLow(getLow() + change, mode)
            ThermostatMode.COOL -> applyHigh(getHigh() + change, mode)
            ThermostatMode.OFF -> Unit
        }
    }
}
