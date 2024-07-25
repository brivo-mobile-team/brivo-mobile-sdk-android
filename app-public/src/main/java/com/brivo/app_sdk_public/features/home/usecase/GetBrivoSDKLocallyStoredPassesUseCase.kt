package com.brivo.app_sdk_public.features.home.usecase

import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class GetBrivoSDKLocallyStoredPassesUseCase  @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    suspend fun execute() : DomainState<LinkedHashMap<String, BrivoOnairPass>?> {
        return brivoSdkMobileRepository.retrieveSDKLocallyStoredPasses()
    }
}