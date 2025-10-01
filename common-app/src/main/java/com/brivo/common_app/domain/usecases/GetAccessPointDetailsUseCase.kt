package com.brivo.common_app.domain.usecases

import com.brivo.common_app.model.DomainState
import com.brivo.sdk.onair.model.BrivoAccessPoint
import javax.inject.Inject

class GetAccessPointDetailsUseCase @Inject constructor(
    private val getBrivoSDKLocallyStoredPassesUseCase: GetBrivoSDKLocallyStoredPassesUseCase
) {
    suspend fun execute(accessPointId: String): DomainState<BrivoAccessPoint> {
        return when (val result = getBrivoSDKLocallyStoredPassesUseCase.execute()) {
            is DomainState.Success -> {
                val accessPoint = result.data?.values?.flatMap { pass ->
                    pass.sites.flatMap { site ->
                        site.accessPoints
                    }
                }?.firstOrNull { it.id == accessPointId }

                if (accessPoint != null) {
                    DomainState.Success(accessPoint)
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
