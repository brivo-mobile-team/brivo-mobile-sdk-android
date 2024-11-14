package com.brivo.app_sdk_public.core.usecase

import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.sdk.enums.ServerRegion
import javax.inject.Inject

class InitializeBrivoSDKUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(serverRegion: ServerRegion) = brivoSdkMobileRepository.init(serverRegion)
}