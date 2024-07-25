package com.brivo.app_sdk_public.features.unlockdoor.usecase

import com.brivo.sdk.model.BrivoError
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class GetBLEErrorsUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(): DomainState<BrivoError> =
        brivoSdkMobileRepository.getBleAuthenticationTimedOutError()
}