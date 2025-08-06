package com.brivo.common_app.features.redeempass.usecase

import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class RedeemMobilePassUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository,
) {

    suspend fun execute(email: String, token: String): DomainState<BrivoOnairPass?> =
        when (val result = brivoSdkMobileRepository.redeemMobilePass(email, token)) {
            is DomainState.Failed -> {
                DomainState.Failed(result.error)
            }

            is DomainState.Success -> {
                result.data?.let { brivoSdkMobileRepository.refreshAllegionCredentials() }
                DomainState.Success(result.data)
            }
        }
}


