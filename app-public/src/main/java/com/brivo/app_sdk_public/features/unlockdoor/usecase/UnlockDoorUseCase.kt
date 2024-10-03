package com.brivo.app_sdk_public.features.unlockdoor.usecase

import android.os.CancellationSignal
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.app_sdk_public.features.unlockdoor.model.UnlockDoorListener
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject

class UnlockDoorUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(
        passId: String,
        accessPointId: String,
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener,
        activity: FragmentActivity
    ): DomainState<Unit> =
        brivoSdkMobileRepository.unlockAccessPoint(
            passId = passId,
            accessPointId = accessPointId,
            cancellationSignal = cancellationSignal,
            listener = listener,
            activity = activity
        )
}