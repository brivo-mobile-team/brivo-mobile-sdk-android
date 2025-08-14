package com.brivo.common_app.domain.usecases

import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class RefreshPassesUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {

    suspend fun execute(refreshToken: String, accessToken: String?): DomainState<BrivoOnairPass?> =
        brivoSdkMobileRepository.refreshPass(refreshToken, accessToken)
}
