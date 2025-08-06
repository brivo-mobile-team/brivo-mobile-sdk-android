package com.brivo.common_app.features.home.usecase

import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import javax.inject.Inject

class RefreshAllegionCredentialsUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {
    suspend fun execute(): DomainState<Unit> {
        return brivoSdkMobileRepository.refreshAllegionCredentials()
    }
}
