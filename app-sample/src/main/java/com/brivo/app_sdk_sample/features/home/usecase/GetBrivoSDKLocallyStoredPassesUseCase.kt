package com.brivo.app_sdk_sample.features.home.usecase

import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.app_sdk_sample.core.model.DomainState
import com.brivo.app_sdk_sample.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class GetBrivoSDKLocallyStoredPassesUseCase  @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    suspend fun execute() : DomainState<LinkedHashMap<String, BrivoOnairPass>?> {
        return brivoSdkMobileRepository.retrieveSDKLocallyStoredPasses()
    }
}