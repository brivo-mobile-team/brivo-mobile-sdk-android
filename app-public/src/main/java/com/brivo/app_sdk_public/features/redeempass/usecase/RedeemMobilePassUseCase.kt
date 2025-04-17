package com.brivo.app_sdk_public.features.redeempass.usecase

import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.sdk.onair.model.BrivoOnairPass
import javax.inject.Inject

class RedeemMobilePassUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl,
) {

    suspend fun execute(email: String, token: String): DomainState<BrivoOnairPass?> =
        when (val result = brivoSdkMobileRepository.redeemMobilePass(email, token)) {
            is DomainState.Failed -> {
                DomainState.Failed(result.error)
            }

            is DomainState.Success -> {
                brivoSdkMobileRepository.refreshAllegionCredentials()
                DomainState.Success(result.data)
            }
        }
}


