package com.brivo.common_app.features.thermostat.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brivo.common_app.features.thermostat.model.FanUIState
import com.brivo.common_app.features.thermostat.model.ThermostatLoadingState
import com.brivo.common_app.features.thermostat.model.ThermostatUIEvent
import com.brivo.common_app.features.thermostat.model.ThermostatUiEffect
import com.brivo.common_app.features.thermostat.model.ThermostatUnit
import com.brivo.common_app.features.thermostat.model.ThermostatViewState
import com.brivo.common_app.ui.theme.Gray950
import kotlinx.coroutines.flow.SharedFlow

// ── Sheet enum ────────────────────────────────────────────────────────────────
private enum class ActiveSheet { NONE, MODE, FAN }

// ─────────────────────────────────────────────────────────────────────────────
// Entry point — stateless, wired to ViewModel by each app's ThermostatScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ThermostatContent(
    modifier: Modifier = Modifier,
    state: ThermostatViewState,
    uiEffect: SharedFlow<ThermostatUiEffect>,
    onBackPressed: () -> Unit,
    onEvent: (ThermostatUIEvent) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        when (val loading = state.loadingState) {
            ThermostatLoadingState.Loading -> ThermostatLoadingView()
            is ThermostatLoadingState.Failed -> ThermostatErrorView(loading.message)
            ThermostatLoadingState.Success -> ThermostatLoaded(
                state = state,
                uiEffect = uiEffect,
                onBackPressed = onBackPressed,
                onEvent = onEvent
            )
        }
    }
}

// ── Loaded: owns sheet state ──────────────────────────────────────────────────

@Composable
private fun ThermostatLoaded(
    state: ThermostatViewState,
    uiEffect: SharedFlow<ThermostatUiEffect>,
    onBackPressed: () -> Unit,
    onEvent: (ThermostatUIEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var activeSheet by remember { mutableStateOf(ActiveSheet.NONE) }

    val ts = state.thermostatState
    val latestTs by rememberUpdatedState(ts)

    val controller = remember(ts.minTemp, ts.maxTemp, ts.unit) {
        ThermostatRangeController(
            minTemp = ts.minTemp,
            maxTemp = ts.maxTemp,
            isCelsius = ts.unit == ThermostatUnit.CELSIUS,
            getLow = { latestTs.low },
            getHigh = { latestTs.high },
            onEvent = onEvent
        )
    }

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                ThermostatUiEffect.CloseModeSheet,
                ThermostatUiEffect.CloseFanSheet -> activeSheet = ActiveSheet.NONE
            }
        }
    }

    LaunchedEffect(activeSheet) {
        if (activeSheet != ActiveSheet.NONE) sheetState.show() else sheetState.hide()
    }

    LaunchedEffect(sheetState) {
        var wasVisible = false
        snapshotFlow { sheetState.isVisible }.collect { isVisible ->
            if (wasVisible && !isVisible && activeSheet != ActiveSheet.NONE) {
                activeSheet = ActiveSheet.NONE
                onEvent(ThermostatUIEvent.ResetModeActionState)
                onEvent(ThermostatUIEvent.ResetFanActionState)
            }
            wasVisible = isVisible
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetBackgroundColor = Gray950,
        scrimColor = Color.Black.copy(alpha = 0.50f),
        sheetElevation = 0.dp,
        sheetContent = {
            when (activeSheet) {
                ActiveSheet.MODE -> ThermostatModeSheet(
                    selected = ts.mode,
                    allowedModes = ts.allowedModes,
                    actionState = ts.modeActionState,
                    onEvent = onEvent,
                    onClose = { activeSheet = ActiveSheet.NONE }
                )

                ActiveSheet.FAN -> {
                    val fanState = ts.fanUIState
                    if (fanState is FanUIState.Available) {
                        ThermostatFanSheet(
                            selected = fanState.option,
                            actionState = ts.fanActionState,
                            onEvent = onEvent,
                            onClose = { activeSheet = ActiveSheet.NONE }
                        )
                    } else {
                        Spacer(Modifier.height(1.dp))
                    }
                }

                ActiveSheet.NONE -> Spacer(Modifier.height(1.dp))
            }
        }
    ) {
        ThermostatInnerContent(
            ts = ts,
            shouldShowTemperatureError = state.shouldShowTemperatureError,
            controller = controller,
            onBackPressed = onBackPressed,
            onOpenModeSheet = { activeSheet = ActiveSheet.MODE },
            onOpenFanSheet = { activeSheet = ActiveSheet.FAN }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Loading / Error screens
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ThermostatLoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ThermostatErrorView(message: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

