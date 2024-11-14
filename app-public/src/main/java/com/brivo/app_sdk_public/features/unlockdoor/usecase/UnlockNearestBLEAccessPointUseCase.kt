package com.brivo.app_sdk_public.features.unlockdoor.usecase

import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.sdk.model.BrivoResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnlockNearestBLEAccessPointUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(
        activity: FragmentActivity
    ): Flow<BrivoResult> =
        brivoSdkMobileRepository.unlockNearestBLEAccessPoint(
            activity = activity
        )
}