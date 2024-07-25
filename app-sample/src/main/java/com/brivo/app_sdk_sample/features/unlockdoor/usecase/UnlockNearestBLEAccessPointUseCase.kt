package com.brivo.app_sdk_sample.features.unlockdoor.usecase

import android.os.CancellationSignal
import com.brivo.app_sdk_sample.core.model.DomainState
import com.brivo.app_sdk_sample.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.app_sdk_sample.features.unlockdoor.model.UnlockDoorListener
import javax.inject.Inject

class UnlockNearestBLEAccessPointUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener
    ): DomainState<Unit> =
        brivoSdkMobileRepository.unlockNearestBLEAccessPoint(
            cancellationSignal = cancellationSignal,
            listener = listener
        )
}