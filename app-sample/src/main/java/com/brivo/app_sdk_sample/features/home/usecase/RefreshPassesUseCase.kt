package com.brivo.app_sdk_sample.features.home.usecase

import com.brivo.app_sdk_sample.core.model.DomainState
import com.brivo.app_sdk_sample.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class RefreshPassesUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    suspend fun execute(refreshToken: String, accessToken: String?): DomainState<Unit> =
        brivoSdkMobileRepository.refreshPass(refreshToken, accessToken)
}