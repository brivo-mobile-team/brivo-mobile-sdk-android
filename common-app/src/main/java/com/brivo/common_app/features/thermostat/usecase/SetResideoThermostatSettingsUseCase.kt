package com.brivo.common_app.features.thermostat.usecase

import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import javax.inject.Inject

class SetResideoThermostatSettingsUseCase @Inject constructor(
    private val repository: BrivoMobileSDKRepository
) {
    suspend fun execute(
        thermostatId: String,
        mode: String,
        fanMode: String?,
        heatSetpoint: Float,
        coolSetpoint: Float
    ): DomainState<Unit> = repository.setResideoThermostatSettings(
        thermostatId  = thermostatId,
        mode          = mode,
        fanMode       = fanMode,
        heatSetpoint  = heatSetpoint,
        coolSetpoint  = coolSetpoint
    )
}
