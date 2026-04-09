package com.brivo.common_app.features.thermostat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brivo.common_app.R
import com.brivo.common_app.features.thermostat.model.RangeEdit
import com.brivo.common_app.features.thermostat.model.ThermostatMode
import com.brivo.common_app.features.thermostat.model.ThermostatUiState
import com.brivo.common_app.features.thermostat.model.ThermostatUnit

@Composable
internal fun ThermostatRangeControlPanel(
    ts: ThermostatUiState,
    controller: ThermostatRangeController
) {
    val isCelsius = ts.unit == ThermostatUnit.CELSIUS
    val step = if (isCelsius) 0.5f else 1.0f
    val minGap = 1.0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when (ts.mode) {
            ThermostatMode.HEAT -> {
                TempControlRow(
                    label = stringResource(R.string.thermostat_heat_to),
                    value = ts.low,
                    isCelsius = isCelsius,
                    minusEnabled = (ts.low - step) >= ts.minTemp,
                    plusEnabled = (ts.low + step) <= ts.maxTemp,
                    onMinus = { controller.step(ts.mode, RangeEdit.LOW, -1) },
                    onPlus = { controller.step(ts.mode, RangeEdit.LOW, +1) }
                )
            }

            ThermostatMode.COOL -> {
                TempControlRow(
                    label = stringResource(R.string.thermostat_cool_to),
                    value = ts.high,
                    isCelsius = isCelsius,
                    minusEnabled = (ts.high - step) >= ts.minTemp,
                    plusEnabled = (ts.high + step) <= ts.maxTemp,
                    onMinus = { controller.step(ts.mode, RangeEdit.HIGH, -1) },
                    onPlus = { controller.step(ts.mode, RangeEdit.HIGH, +1) }
                )
            }

            ThermostatMode.AUTO -> {
                TempControlRow(
                    label = stringResource(R.string.thermostat_low),
                    value = ts.low,
                    isCelsius = isCelsius,
                    minusEnabled = (ts.low - step) >= ts.minTemp,
                    plusEnabled = (ts.low + step) <= (ts.high - minGap),
                    onMinus = { controller.step(ts.mode, RangeEdit.LOW, -1) },
                    onPlus = { controller.step(ts.mode, RangeEdit.LOW, +1) }
                )
                TempControlRow(
                    label = stringResource(R.string.thermostat_high),
                    value = ts.high,
                    isCelsius = isCelsius,
                    minusEnabled = (ts.high - step) >= (ts.low + minGap),
                    plusEnabled = (ts.high + step) <= ts.maxTemp,
                    onMinus = { controller.step(ts.mode, RangeEdit.HIGH, -1) },
                    onPlus = { controller.step(ts.mode, RangeEdit.HIGH, +1) }
                )
            }

            ThermostatMode.OFF -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, MaterialTheme.shapes.small)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.thermostat_off_message),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TempControlRow(
    label: String,
    value: Float,
    isCelsius: Boolean,
    minusEnabled: Boolean,
    plusEnabled: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    val formatted = if (isCelsius) "%.1f".format(value) else "%.0f".format(value)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepButton(label = "−", enabled = minusEnabled, onClick = onMinus)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatted,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        StepButton(label = "+", enabled = plusEnabled, onClick = onPlus)
    }
}

@Composable
private fun StepButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    val contentColor = if (enabled)
        MaterialTheme.colorScheme.onSurface
    else
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val bgColor = if (enabled)
        MaterialTheme.colorScheme.surface
    else
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = contentColor, fontSize = 22.sp, fontWeight = FontWeight.Light)
    }
}
