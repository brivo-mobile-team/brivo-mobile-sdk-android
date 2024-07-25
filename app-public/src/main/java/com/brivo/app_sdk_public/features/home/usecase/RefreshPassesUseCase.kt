package com.brivo.app_sdk_public.features.home.usecase

import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class RefreshPassesUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    suspend fun execute(refreshToken: String, accessToken: String?): DomainState<Unit> =
        brivoSdkMobileRepository.refreshPass(refreshToken, accessToken)
}