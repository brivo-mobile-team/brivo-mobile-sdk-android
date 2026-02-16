package com.brivo.common_app.features.unlockdoor.usecase

import androidx.fragment.app.FragmentActivity
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.enums.UnlockStrategy
import com.brivo.sdk.model.BrivoResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnlockDoorUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {

    fun execute(
        passId: String,
        accessPointId: String,
        activity: FragmentActivity,
        unlockStrategy: UnlockStrategy? = null
    ): Flow<BrivoResult> = brivoSdkMobileRepository.unlockAccessPoint(
        passId = passId,
        accessPointId = accessPointId,
        activity = activity,
        unlockStrategy = unlockStrategy
    )
}
