package com.brivo.common_app.repository

import androidx.fragment.app.FragmentActivity
import com.brivo.common_app.model.DomainState
import com.brivo.sdk.enums.ServerRegion
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.model.BrivoSDKApiState
import com.brivo.sdk.onair.model.BrivoAuthenticateResponse
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.model.BrivoTokens
import kotlinx.coroutines.flow.Flow

interface BrivoMobileSDKRepository {

    fun init(
        serverRegion: ServerRegion,
        clientId: String,
        clientSecret: String,
        authUrl: String,
        apiUrl: String,
        ): DomainState<Unit>

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

    suspend fun refreshAccessTokenWithToken(
        accessToken: String,
        refreshToken: String
    ): DomainState<BrivoAuthenticateResponse>

    suspend fun refreshAllegionCredentials(): DomainState<Unit>

    suspend fun refreshOrigoCredentials(pass: BrivoOnairPass): DomainState<Unit>

    suspend fun refreshDormakabaCredentials(passes: List<BrivoOnairPass>): DomainState<Unit>

    fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        activity: FragmentActivity
    ): Flow<BrivoResult>

    fun unlockNearestBLEAccessPoint(
        activity: FragmentActivity
    ): Flow<BrivoResult>

    suspend fun getWalletEligibilityStatus(
        tokens: BrivoTokens,
        forced: Boolean
    ): BrivoSDKApiState<WalletEligibilityStatus>
}


