package com.brivo.app_sdk_public.features.home.usecase

import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class GetBrivoSDKLocallyStoredPassesUseCase  @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    suspend fun execute() : DomainState<Map<String, BrivoOnairPass>?> {
        return brivoSdkMobileRepository.retrieveSDKLocallyStoredPasses()
    }
}