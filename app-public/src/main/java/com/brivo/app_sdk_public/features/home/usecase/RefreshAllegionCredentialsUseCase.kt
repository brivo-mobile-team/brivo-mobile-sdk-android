package com.brivo.app_sdk_public.features.home.usecase

import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepository
import javax.inject.Inject

class RefreshAllegionCredentialsUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {
    suspend fun execute(): DomainState<Unit> {
        return brivoSdkMobileRepository.refreshAllegionCredentials()
    }
}
