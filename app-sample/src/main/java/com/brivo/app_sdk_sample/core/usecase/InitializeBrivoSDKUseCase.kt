package com.brivo.app_sdk_sample.core.usecase

import com.brivo.app_sdk_sample.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class InitializeBrivoSDKUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(isEURegion: Boolean = false) = brivoSdkMobileRepository.init(isEURegion)
}