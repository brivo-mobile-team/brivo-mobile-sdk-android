package com.brivo.app_sdk_public.core.usecase

import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class InitializeBrivoSDKUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(isEURegion: Boolean = false) = brivoSdkMobileRepository.init(isEURegion)
}