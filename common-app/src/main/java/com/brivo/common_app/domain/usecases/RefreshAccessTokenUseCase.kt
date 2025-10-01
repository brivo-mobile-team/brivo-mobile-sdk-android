package com.brivo.common_app.domain.usecases

import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.onair.model.BrivoAuthenticateResponse
import javax.inject.Inject

class RefreshAccessTokenUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {
    suspend fun execute(
        accessToken: String,
        refreshToken: String
    ): DomainState<BrivoAuthenticateResponse> {
        return when (val result =
            brivoSdkMobileRepository.refreshAccessTokenWithToken(accessToken, refreshToken)) {
            is DomainState.Failed -> {
                DomainState.Failed(result.error, result.errorCode)
            }

            is DomainState.Success -> {
                DomainState.Success(result.data)
            }
        }
    }
}
