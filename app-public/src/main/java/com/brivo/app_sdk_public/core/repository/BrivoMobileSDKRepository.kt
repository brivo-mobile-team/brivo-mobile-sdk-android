package com.brivo.app_sdk_public.core.repository

import android.os.CancellationSignal
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.features.unlockdoor.model.UnlockDoorListener
import androidx.fragment.app.FragmentActivity
import com.brivo.sdk.onair.model.BrivoOnairPass

interface BrivoMobileSDKRepository {

    fun init(isEURegion: Boolean): DomainState<Unit>

    fun initLocalAuth(title: String, message: String, negativeButtonText: String, description: String): DomainState<Unit>

    fun getVersion(): DomainState<String>

    suspend fun redeemMobilePass(email: String, token: String): DomainState<BrivoOnairPass?>

    suspend fun retrieveSDKLocallyStoredPasses(): DomainState<LinkedHashMap<String, BrivoOnairPass>?>

    suspend fun refreshPass(refreshToken: String, accessToken: String?): DomainState<Unit>

    suspend fun refreshAllegionCredentials(passes:List<BrivoOnairPass>)

    suspend fun refreshAllegionCredential(pass: BrivoOnairPass)

    fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener,
        activity: FragmentActivity
    ): DomainState<Unit>

    fun unlockNearestBLEAccessPoint(
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener,
        activity: FragmentActivity
    ): DomainState<Unit>

}