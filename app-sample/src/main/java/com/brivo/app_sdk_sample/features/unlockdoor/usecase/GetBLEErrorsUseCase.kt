package com.brivo.app_sdk_sample.features.unlockdoor.usecase

import com.brivo.sdk.model.BrivoError
import com.brivo.app_sdk_sample.core.model.DomainState
import com.brivo.app_sdk_sample.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class GetBLEErrorsUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(): DomainState<BrivoError> =
        brivoSdkMobileRepository.getBleAuthenticationTimedOutError()
}