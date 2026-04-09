package com.brivo.common_app.features.thermostat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brivo.common_app.R
import com.brivo.common_app.features.thermostat.model.FanOption
import com.brivo.common_app.features.thermostat.model.FanUIState
import com.brivo.common_app.features.thermostat.model.ThermostatMode
import com.brivo.common_app.features.thermostat.model.ThermostatUiState

@Composable
internal fun ThermostatBottomActionBar(
    ts: ThermostatUiState,
    onModeClick: () -> Unit,
    onFanClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsRow(
            label        = stringResource(R.string.thermostat_mode),
            currentValue = ts.mode.displayName(),
            onClick      = onModeClick
        )
        if (ts.fanUIState is FanUIState.Available) {
            SettingsRow(
                label        = stringResource(R.string.thermostat_fan),
                currentValue = ts.fanUIState.option.displayName(),
                onClick      = onFanClick
            )
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    currentValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = currentValue,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThermostatMode.displayName(): String = stringResource(
    when (this) {
        ThermostatMode.OFF  -> R.string.thermostat_mode_off
        ThermostatMode.HEAT -> R.string.thermostat_mode_heat
        ThermostatMode.COOL -> R.string.thermostat_mode_cool
        ThermostatMode.AUTO -> R.string.thermostat_mode_auto
    }
)

@Composable
private fun FanOption.displayName(): String = stringResource(
    when (this) {
        FanOption.AUTO       -> R.string.thermostat_fan_auto
        FanOption.ON         -> R.string.thermostat_fan_on
        FanOption.CIRCULATE  -> R.string.thermostat_fan_circulate
    }
)
