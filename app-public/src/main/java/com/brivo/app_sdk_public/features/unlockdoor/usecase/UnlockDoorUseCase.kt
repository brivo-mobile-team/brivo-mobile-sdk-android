package com.brivo.app_sdk_public.features.unlockdoor.usecase

import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.sdk.model.BrivoResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnlockDoorUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(
        passId: String,
        accessPointId: String,
        activity: FragmentActivity
    ): Flow<BrivoResult> = brivoSdkMobileRepository.unlockAccessPoint(
        passId = passId,
        accessPointId = accessPointId,
        activity = activity
    )
}