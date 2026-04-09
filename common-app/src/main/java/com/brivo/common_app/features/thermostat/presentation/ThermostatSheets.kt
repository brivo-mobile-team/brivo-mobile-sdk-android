package com.brivo.common_app.features.thermostat.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brivo.common_app.features.thermostat.model.FanOption
import com.brivo.common_app.features.thermostat.model.ThermostatActionState
import com.brivo.common_app.features.thermostat.model.ThermostatMode
import com.brivo.common_app.features.thermostat.model.ThermostatUIEvent

@Composable
internal fun ThermostatModeSheet(
    selected: ThermostatMode,
    allowedModes: List<ThermostatMode>,
    actionState: ThermostatActionState,
    onEvent: (ThermostatUIEvent) -> Unit,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        ThermostatBaseSheet(title = "Mode", onClose = onClose) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allowedModes.forEach { mode ->
                    val isLoading = actionState is ThermostatActionState.Loading &&
                            actionState.targetMode == mode
                    val isBusy = actionState is ThermostatActionState.Loading
                    SheetOptionCell(
                        label = mode.name,
                        selected = selected == mode,
                        isLoading = isLoading,
                        enabled = !isBusy && selected != mode,
                        onClick = { onEvent(ThermostatUIEvent.UpdateMode(mode)) }
                    )
                }
            }
        }

        SheetErrorBanner(
            visible = actionState is ThermostatActionState.Failed,
            message = "Failed to update mode. Try again.",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
internal fun ThermostatFanSheet(
    selected: FanOption,
    actionState: ThermostatActionState,
    onEvent: (ThermostatUIEvent) -> Unit,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        ThermostatBaseSheet(title = "Fan", onClose = onClose) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FanOption.entries.forEach { option ->
                    val isLoading = actionState is ThermostatActionState.Loading &&
                            actionState.targetFan == option
                    val isBusy = actionState is ThermostatActionState.Loading
                    SheetOptionCell(
                        label = option.name,
                        selected = selected == option,
                        isLoading = isLoading,
                        enabled = !isBusy && selected != option,
                        onClick = { onEvent(ThermostatUIEvent.UpdateFan(option)) }
                    )
                }
            }
        }

        SheetErrorBanner(
            visible = actionState is ThermostatActionState.Failed,
            message = "Failed to update fan. Try again.",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
        )
    }
}

