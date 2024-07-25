package com.brivo.app_sdk_sample.features.unlockdoor.usecase

import com.brivo.app_sdk_sample.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class InitializeBrivoSDKLocalAuthUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(
        title: String,
        message: String,
        negativeButtonText: String,
        description: String
    ) = brivoSdkMobileRepository.initLocalAuth(
        title = title,
        message = message,
        negativeButtonText = negativeButtonText,
        description = description
    )
}