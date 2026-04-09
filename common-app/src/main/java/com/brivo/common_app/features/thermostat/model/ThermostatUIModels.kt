package com.brivo.common_app.features.thermostat.model

// ── Mode / Unit enums ─────────────────────────────────────────────────────
enum class ThermostatMode { OFF, HEAT, COOL, AUTO }

enum class ThermostatUnit { CELSIUS, FAHRENHEIT }

// ── Fan ───────────────────────────────────────────────────────────────────
enum class FanOption {
    AUTO, ON, CIRCULATE;

    companion object {
        fun parse(value: String?): FanOption? =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
}

sealed interface FanUIState {
    data object NotSupported : FanUIState
    data class Available(val option: FanOption) : FanUIState
}

// ── Action state (mode/fan API call in flight) ────────────────────────────
sealed interface ThermostatActionState {
    data object Idle : ThermostatActionState
    data class Loading(
        val targetMode: ThermostatMode? = null,
        val targetFan: FanOption? = null
    ) : ThermostatActionState
    data class Failed(val message: String) : ThermostatActionState
}

// ── Loading state (initial data fetch) ───────────────────────────────────
sealed interface ThermostatLoadingState {
    data object Loading : ThermostatLoadingState
    data object Success : ThermostatLoadingState
    data class Failed(val message: String) : ThermostatLoadingState
}

// ── Primary UI state ──────────────────────────────────────────────────────
data class ThermostatUiState(
    val deviceName: String = "",
    val currentTemp: Float = 0f,
    val low: Float = 0f,
    val high: Float = 0f,
    val minTemp: Float = 0f,
    val maxTemp: Float = 0f,
    val allowedModes: List<ThermostatMode> = emptyList(),
    val mode: ThermostatMode = ThermostatMode.AUTO,
    val unit: ThermostatUnit = ThermostatUnit.FAHRENHEIT,
    val modeActionState: ThermostatActionState = ThermostatActionState.Idle,
    val fanActionState: ThermostatActionState = ThermostatActionState.Idle,
    val fanUIState: FanUIState = FanUIState.NotSupported
)

// ── UI events ─────────────────────────────────────────────────────────────
sealed class ThermostatUIEvent {
    data class UpdateLowTemp(val value: Float) : ThermostatUIEvent()
    data class UpdateHighTemp(val value: Float) : ThermostatUIEvent()
    data class UpdateMode(val mode: ThermostatMode) : ThermostatUIEvent()
    data class UpdateFan(val fan: FanOption) : ThermostatUIEvent()
    data object ResetModeActionState : ThermostatUIEvent()
    data object ResetFanActionState : ThermostatUIEvent()
}

// ── UI side-effects ───────────────────────────────────────────────────────
sealed class ThermostatUiEffect {
    data object CloseModeSheet : ThermostatUiEffect()
    data object CloseFanSheet : ThermostatUiEffect()
}

// ── Helper enum for range control buttons ────────────────────────────────
enum class RangeEdit { LOW, HIGH }

// ── Composable view state (owned by ThermostatViewModel in each app) ──────
data class ThermostatViewState(
    val loadingState: ThermostatLoadingState = ThermostatLoadingState.Loading,
    val thermostatState: ThermostatUiState = ThermostatUiState(),
    val shouldShowTemperatureError: Boolean = false
)
