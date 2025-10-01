package com.brivo.common_app.domain.usecases

import com.brivo.common_app.model.DomainState
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class GetPassFromAccessPointIdUseCase @Inject constructor(
    private val getBrivoSDKLocallyStoredPassesUseCase: GetBrivoSDKLocallyStoredPassesUseCase
) {
    suspend fun execute(accessPointId: String): DomainState<BrivoOnairPass?> {
        return when (val result = getBrivoSDKLocallyStoredPassesUseCase.execute()) {
            is DomainState.Success -> {
                val pass = result.data?.values?.firstOrNull { pass ->
                    pass.sites.any { site ->
                        site.accessPoints.any { it.id == accessPointId }
                    }
                }

                if (pass != null) {
                    DomainState.Success(pass)
                } else {
                    DomainState.Failed("Access Point Id not found among the passes", -1)
                }
            }

            is DomainState.Failed -> {
                DomainState.Failed("Failed to get passes stored locally", -1)
            }
        }
    }
}
