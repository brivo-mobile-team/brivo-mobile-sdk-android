package com.brivo.common_app.features.unlockdoor.usecase

import com.brivo.common_app.repository.BrivoMobileSDKRepository
import javax.inject.Inject

class InitializeBrivoSDKLocalAuthUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
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
