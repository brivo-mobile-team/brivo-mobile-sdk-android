package com.brivo.common_app.features.thermostat.usecase

import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.onair.model.resideo.ResideoThermostatResponse
import javax.inject.Inject

class GetResideoThermostatUseCase @Inject constructor(
    private val repository: BrivoMobileSDKRepository
) {
    suspend fun execute(thermostatId: String): DomainState<ResideoThermostatResponse> =
        repository.getResideoThermostat(thermostatId)
}
