package com.brivo.app_sdk_public.features.redeempass.usecase

import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import javax.inject.Inject

class RedeemMobilePassUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    suspend fun execute(email: String, token: String): DomainState<BrivoOnairPass?> =
        brivoSdkMobileRepository.redeemMobilePass(email, token)
}