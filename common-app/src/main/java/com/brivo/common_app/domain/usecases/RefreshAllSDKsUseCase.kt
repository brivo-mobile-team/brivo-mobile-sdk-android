package com.brivo.common_app.domain.usecases

import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.model.BrivoSDKApiState
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class RefreshAllSDKsUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {

    suspend fun execute(passes: List<BrivoOnairPass>): BrivoSDKApiState<Unit> =
        brivoSdkMobileRepository.refreshAllSDKs(passes)
}
