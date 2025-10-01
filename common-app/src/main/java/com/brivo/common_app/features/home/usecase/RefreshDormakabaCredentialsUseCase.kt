package com.brivo.common_app.features.home.usecase

import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class RefreshDormakabaCredentialsUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {
    suspend fun execute(passes: List<BrivoOnairPass>): DomainState<Unit> {
        return brivoSdkMobileRepository.refreshDormakabaCredentials(passes)
    }
}
