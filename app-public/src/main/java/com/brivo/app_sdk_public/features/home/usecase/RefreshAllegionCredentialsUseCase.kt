package com.brivo.app_sdk_public.features.home.usecase

import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepository
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class RefreshAllegionCredentialsUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {
    suspend fun execute(passes: List<BrivoOnairPass>){
        brivoSdkMobileRepository.refreshAllegionCredentials(passes)
    }
}