package com.brivo.app_sdk_sample.features.unlockdoor.usecase

import android.os.CancellationSignal
import com.brivo.app_sdk_sample.core.model.DomainState
import com.brivo.app_sdk_sample.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.app_sdk_sample.features.unlockdoor.model.UnlockDoorListener
import javax.inject.Inject

class UnlockDoorUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(
        passId: String,
        accessPointId: String,
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener
    ): DomainState<Unit> =
        brivoSdkMobileRepository.unlockAccessPoint(
            passId = passId,
            accessPointId = accessPointId,
            cancellationSignal = cancellationSignal,
            listener = listener
        )
}