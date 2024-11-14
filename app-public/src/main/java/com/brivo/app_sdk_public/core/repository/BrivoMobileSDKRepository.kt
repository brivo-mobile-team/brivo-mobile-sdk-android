package com.brivo.app_sdk_public.core.repository

import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.sdk.enums.ServerRegion
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.onair.model.BrivoOnairPass
import kotlinx.coroutines.flow.Flow

interface BrivoMobileSDKRepository {

    fun init(serverRegion: ServerRegion): DomainState<Unit>

    fun initLocalAuth(
        title: String,
        message: String,
        negativeButtonText: String,
        description: String
    ): DomainState<Unit>

    fun getVersion(): DomainState<String>

    suspend fun redeemMobilePass(email: String, token: String): DomainState<BrivoOnairPass?>

    suspend fun retrieveSDKLocallyStoredPasses(): DomainState<Map<String, BrivoOnairPass>?>

    suspend fun refreshPass(
        refreshToken: String,
        accessToken: String?
    ): DomainState<BrivoOnairPass?>

    suspend fun refreshAllegionCredentials(passes: List<BrivoOnairPass>)

    suspend fun refreshAllegionCredential(pass: BrivoOnairPass)

    fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        activity: FragmentActivity
    ): Flow<BrivoResult>

    fun unlockNearestBLEAccessPoint(
        activity: FragmentActivity,
    ): Flow<BrivoResult>
}