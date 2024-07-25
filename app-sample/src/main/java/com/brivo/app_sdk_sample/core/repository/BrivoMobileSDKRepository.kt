package com.brivo.app_sdk_sample.core.repository

import android.os.CancellationSignal
import com.brivo.sdk.model.BrivoError
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.app_sdk_sample.core.model.DomainState
import com.brivo.app_sdk_sample.features.unlockdoor.model.UnlockDoorListener

interface BrivoMobileSDKRepository {

    fun init(isEURegion: Boolean): DomainState<Unit>

    fun initLocalAuth(title: String, message: String, negativeButtonText: String, description: String): DomainState<Unit>

    fun getVersion(): DomainState<String>

    suspend fun redeemMobilePass(email: String, token: String): DomainState<BrivoOnairPass?>

    suspend fun retrieveSDKLocallyStoredPasses(): DomainState<LinkedHashMap<String, BrivoOnairPass>?>

    suspend fun refreshPass(refreshToken: String, accessToken: String?): DomainState<Unit>

    fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener
    ): DomainState<Unit>

    fun unlockNearestBLEAccessPoint(
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener
    ): DomainState<Unit>

    fun getBleAuthenticationTimedOutError(): DomainState<BrivoError>
}