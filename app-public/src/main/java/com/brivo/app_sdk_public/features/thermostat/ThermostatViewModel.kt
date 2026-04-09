package com.brivo.app_sdk_public.features.thermostat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.common_app.features.thermostat.model.FanOption
import com.brivo.common_app.features.thermostat.model.FanUIState
import com.brivo.common_app.features.thermostat.model.ThermostatActionState
import com.brivo.common_app.features.thermostat.model.ThermostatLoadingState
import com.brivo.common_app.features.thermostat.model.ThermostatMode
import com.brivo.common_app.features.thermostat.model.ThermostatUIEvent
import com.brivo.common_app.features.thermostat.model.ThermostatUiEffect
import com.brivo.common_app.features.thermostat.model.ThermostatUiState
import com.brivo.common_app.features.thermostat.model.ThermostatUnit
import com.brivo.common_app.features.thermostat.model.ThermostatViewState
import com.brivo.common_app.features.thermostat.usecase.GetResideoThermostatUseCase
import com.brivo.common_app.features.thermostat.usecase.SetResideoThermostatSettingsUseCase
import com.brivo.common_app.model.DomainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

private val SNACKBAR_DELAY = 2.seconds

const val ThermostatIdArg = "thermostatId"

@HiltViewModel
class ThermostatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getResideoThermostatUseCase: GetResideoThermostatUseCase,
    private val setResideoThermostatUseCase: SetResideoThermostatSettingsUseCase
) : ViewModel() {

    private val thermostatId: String = checkNotNull(savedStateHandle[ThermostatIdArg])

    private val _state = MutableStateFlow(ThermostatViewState())
    val state: StateFlow<ThermostatViewState> = _state

    private val _uiEffect = MutableSharedFlow<ThermostatUiEffect>()
    val uiEffect: SharedFlow<ThermostatUiEffect> = _uiEffect.asSharedFlow()

    private var updateSettingsJob: Job? = null

    init {
        loadThermostatData()
    }

    fun onEvent(event: ThermostatUIEvent) {
        when (event) {
            is ThermostatUIEvent.UpdateLowTemp -> {
                updateLowTemp(event.value)
                triggerSettingsUpdate()
            }
            is ThermostatUIEvent.UpdateHighTemp -> {
                updateHighTemp(event.value)
                triggerSettingsUpdate()
            }
            is ThermostatUIEvent.UpdateMode        -> updateMode(event.mode)
            is ThermostatUIEvent.UpdateFan         -> updateFan(event.fan)
            ThermostatUIEvent.ResetModeActionState -> resetModeActionState()
            ThermostatUIEvent.ResetFanActionState  -> resetFanActionState()
        }
    }

    // ── Initial load ──────────────────────────────────────────────────────

    private fun loadThermostatData() {
        viewModelScope.launch {
            _state.update { it.copy(loadingState = ThermostatLoadingState.Loading) }

            when (val result = getResideoThermostatUseCase.execute(thermostatId)) {
                is DomainState.Failed -> {
                    _state.update {
                        it.copy(loadingState = ThermostatLoadingState.Failed(result.error))
                    }
                }
                is DomainState.Success -> {
                    val response = result.data
                    val settings = response.deviceSettings
                    val deviceState = response.deviceState

                    val mode = ThermostatMode.entries.find {
                        it.name.equals(settings.mode.value, ignoreCase = true)
                    } ?: ThermostatMode.AUTO

                    val unit = ThermostatUnit.entries.find {
                        it.name.equals(settings.units.value, ignoreCase = true)
                    } ?: ThermostatUnit.FAHRENHEIT

                    val heatMin = settings.heat.minValue.toFloat()
                    val heatMax = settings.heat.maxValue.toFloat()
                    val coolMin = settings.cool.minValue.toFloat()
                    val coolMax = settings.cool.maxValue.toFloat()

                    val (minTemp, maxTemp) = resolveMinMax(mode, coolMin, coolMax, heatMin, heatMax)

                    val allowedModes = settings.mode.allowedModes.mapNotNull { modeStr ->
                        ThermostatMode.entries.find { it.name.equals(modeStr, ignoreCase = true) }
                    }

                    val fanUIState = FanOption.parse(settings.fan?.value)
                        ?.let { FanUIState.Available(it) }
                        ?: FanUIState.NotSupported

                    _state.update {
                        it.copy(
                            loadingState    = ThermostatLoadingState.Success,
                            thermostatState = ThermostatUiState(
                                deviceName   = response.deviceData.name,
                                currentTemp  = deviceState.temperature.toFloat(),
                                low          = settings.heat.value.toFloat(),
                                high         = settings.cool.value.toFloat(),
                                minTemp      = minTemp,
                                maxTemp      = maxTemp,
                                allowedModes = allowedModes,
                                mode         = mode,
                                unit         = unit,
                                fanUIState   = fanUIState
                            )
                        )
                    }
                }
            }
        }
    }

    // ── Temperature setpoint updates (debounced 2 s) ──────────────────────

    private fun triggerSettingsUpdate() {
        updateSettingsJob?.cancel()
        updateSettingsJob = viewModelScope.launch {
            delay(2.seconds)
            pushThermostatSettings()
        }
    }

    private fun pushThermostatSettings() {
        viewModelScope.launch {
            _state.update { it.copy(shouldShowTemperatureError = false) }

            withContext(NonCancellable) {
                val ts = _state.value.thermostatState
                val fanMode = (ts.fanUIState as? FanUIState.Available)?.option?.name

                val result = setResideoThermostatUseCase.execute(
                    thermostatId  = thermostatId,
                    mode          = ts.mode.name,
                    fanMode       = fanMode,
                    heatSetpoint  = ts.low,
                    coolSetpoint  = ts.high
                )

                when (result) {
                    is DomainState.Failed -> {
                        _state.update { it.copy(shouldShowTemperatureError = true) }
                        delay(SNACKBAR_DELAY)
                        _state.update { it.copy(shouldShowTemperatureError = false) }
                    }
                    is DomainState.Success -> Unit
                }
            }
        }
    }

    // ── Mode update ───────────────────────────────────────────────────────

    private fun updateMode(mode: ThermostatMode) {
        viewModelScope.launch {
            if (_state.value.thermostatState.mode == mode) return@launch

            _state.update {
                it.copy(
                    thermostatState = it.thermostatState.copy(
                        modeActionState = ThermostatActionState.Loading(targetMode = mode)
                    )
                )
            }

            val ts = _state.value.thermostatState
            val result = setResideoThermostatUseCase.execute(
                thermostatId = thermostatId,
                mode         = mode.name,
                fanMode      = (ts.fanUIState as? FanUIState.Available)?.option?.name,
                heatSetpoint = ts.low,
                coolSetpoint = ts.high
            )

            when (result) {
                is DomainState.Failed -> {
                    _state.update {
                        it.copy(
                            thermostatState = it.thermostatState.copy(
                                modeActionState = ThermostatActionState.Failed(result.error)
                            )
                        )
                    }
                    delay(SNACKBAR_DELAY)
                    resetModeActionState()
                }
                is DomainState.Success -> {
                    val (minTemp, maxTemp) = run {
                        val s = _state.value.thermostatState
                        resolveMinMaxFromState(mode, s)
                    }
                    _state.update {
                        it.copy(
                            thermostatState = it.thermostatState.copy(
                                mode            = mode,
                                minTemp         = minTemp,
                                maxTemp         = maxTemp,
                                modeActionState = ThermostatActionState.Idle
                            )
                        )
                    }
                    _uiEffect.emit(ThermostatUiEffect.CloseModeSheet)
                }
            }
        }
    }

    // ── Fan update ────────────────────────────────────────────────────────

    private fun updateFan(fan: FanOption) {
        viewModelScope.launch {
            val currentFanState = _state.value.thermostatState.fanUIState
            if (currentFanState !is FanUIState.Available) return@launch
            if (currentFanState.option == fan) return@launch

            _state.update {
                it.copy(
                    thermostatState = it.thermostatState.copy(
                        fanActionState = ThermostatActionState.Loading(targetFan = fan)
                    )
                )
            }

            val ts = _state.value.thermostatState
            val result = setResideoThermostatUseCase.execute(
                thermostatId = thermostatId,
                mode         = ts.mode.name,
                fanMode      = fan.name,
                heatSetpoint = ts.low,
                coolSetpoint = ts.high
            )

            when (result) {
                is DomainState.Failed -> {
                    _state.update {
                        it.copy(
                            thermostatState = it.thermostatState.copy(
                                fanActionState = ThermostatActionState.Failed(result.error)
                            )
                        )
                    }
                    delay(SNACKBAR_DELAY)
                    resetFanActionState()
                }
                is DomainState.Success -> {
                    _state.update {
                        it.copy(
                            thermostatState = it.thermostatState.copy(
                                fanUIState     = currentFanState.copy(option = fan),
                                fanActionState = ThermostatActionState.Idle
                            )
                        )
                    }
                    _uiEffect.emit(ThermostatUiEffect.CloseFanSheet)
                }
            }
        }
    }

    // ── Low / High state updates ──────────────────────────────────────────

    private fun updateLowTemp(value: Float) {
        _state.update { it.copy(thermostatState = it.thermostatState.copy(low = value)) }
    }

    private fun updateHighTemp(value: Float) {
        _state.update { it.copy(thermostatState = it.thermostatState.copy(high = value)) }
    }

    // ── Reset helpers ─────────────────────────────────────────────────────

    private fun resetModeActionState() {
        _state.update {
            it.copy(thermostatState = it.thermostatState.copy(modeActionState = ThermostatActionState.Idle))
        }
    }

    private fun resetFanActionState() {
        _state.update {
            it.copy(thermostatState = it.thermostatState.copy(fanActionState = ThermostatActionState.Idle))
        }
    }

    // ── Min/Max helpers ───────────────────────────────────────────────────

    private fun resolveMinMax(
        mode: ThermostatMode,
        coolMin: Float, coolMax: Float,
        heatMin: Float, heatMax: Float
    ): Pair<Float, Float> = when (mode) {
        ThermostatMode.HEAT -> heatMin to heatMax
        ThermostatMode.COOL -> coolMin to coolMax
        ThermostatMode.AUTO -> min(coolMin, heatMin) to maxOf(coolMax, heatMax)
        ThermostatMode.OFF  -> coolMin to heatMax
    }

    private fun resolveMinMaxFromState(mode: ThermostatMode, ts: ThermostatUiState) =
        resolveMinMax(mode, ts.minTemp, ts.maxTemp, ts.minTemp, ts.maxTemp)
}
