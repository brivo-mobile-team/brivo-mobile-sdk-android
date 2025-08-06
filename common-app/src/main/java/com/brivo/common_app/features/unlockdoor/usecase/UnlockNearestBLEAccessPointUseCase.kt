package com.brivo.common_app.features.unlockdoor.usecase

import androidx.fragment.app.FragmentActivity
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.model.BrivoResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnlockNearestBLEAccessPointUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository
) {

    fun execute(
        activity: FragmentActivity,
    ): Flow<BrivoResult> =
        brivoSdkMobileRepository.unlockNearestBLEAccessPoint(
            activity = activity
        )
}
