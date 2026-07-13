package com.brivo.common_app.repository

import androidx.fragment.app.FragmentActivity
import com.brivo.common_app.model.DomainState
import com.brivo.sdk.access.continuousscanning.ContinuousScanningResults
import com.brivo.sdk.enums.ServerRegion
import com.brivo.sdk.enums.UnlockStrategy
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.model.BrivoSDKApiState
import com.brivo.sdk.onair.model.BrivoAuthenticateResponse
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.model.BrivoTokens
import com.brivo.sdk.onair.model.resideo.ResideoThermostatResponse
import kotlinx.coroutines.flow.Flow

interface BrivoMobileSDKRepository {

    suspend fun init(
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

    fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        activity: FragmentActivity,
        unlockStrategy: UnlockStrategy?
    ): Flow<BrivoResult>

    fun unlockNearestBLEAccessPoint(
        activity: FragmentActivity
    ): Flow<BrivoResult>

    suspend fun getWalletEligibilityStatus(
        tokens: BrivoTokens,
        forced: Boolean
    ): BrivoSDKApiState<WalletEligibilityStatus>

    fun startScanForNearbyDevices(
        passes: List<BrivoOnairPass>
    ) : Flow<ContinuousScanningResults>

    suspend fun refreshAllSDKs(passes: List<BrivoOnairPass>): BrivoSDKApiState<Unit>

    suspend fun getResideoThermostat(thermostatId: String): DomainState<ResideoThermostatResponse>

    suspend fun setResideoThermostatSettings(
        thermostatId: String,
        mode: String,
        fanMode: String?,
        heatSetpoint: Float,
        coolSetpoint: Float
    ): DomainState<Unit>
}


