package com.brivo.common_app.features.magicbutton

import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.access.continuousscanning.ContinuousScanningResults
import com.brivo.sdk.onair.model.BrivoOnairPass
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContinousScanForNearbyDevicesUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepository

) {
    fun execute(passes: List<BrivoOnairPass>): Flow<ContinuousScanningResults>{
        return brivoSdkMobileRepository.startScanForNearbyDevices(passes)
    }
}
