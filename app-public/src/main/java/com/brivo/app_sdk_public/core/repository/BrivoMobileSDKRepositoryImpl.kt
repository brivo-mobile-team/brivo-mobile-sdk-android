package com.brivo.app_sdk_public.core.repository

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.App
import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.common_app.repository.WalletEligibilityStatus
import com.brivo.sdk.BrivoSDK
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.access.BrivoSDKAccess
import com.brivo.sdk.ble.allegion.BrivoSDKBLEAllegion
import com.brivo.sdk.enums.ServerRegion
import com.brivo.sdk.localauthentication.BrivoSDKLocalAuthentication
import com.brivo.sdk.model.BrivoConfiguration
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.model.BrivoSDKApiState
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.model.BrivoTokens
import com.brivo.sdk.onair.repository.BrivoSDKOnair
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class BrivoMobileSDKRepositoryImpl @Inject constructor(
) : BrivoMobileSDKRepository {

    override fun init(
        serverRegion: ServerRegion,
        clientId: String,
        clientSecret: String,
        authUrl: String,
        apiUrl: String
    ): DomainState<Unit> {
        try {
            BrivoSDK.init(
                context = App.instance.applicationContext,
                brivoConfiguration = BrivoConfiguration(
                    clientId = clientId,
                    clientSecret = clientSecret,
                    authUrl = authUrl,
                    apiUrl = apiUrl,
                    serverRegion = serverRegion,
                    useSDKStorage = true,
                )
            )
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message!!)
        }
        try {
            val result = BrivoSDKBLEAllegion.init()
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message ?: "Failed to initialize BrivoSDKBLEAllegion")
        }

        return DomainState.Success(Unit)
    }

    override suspend fun refreshOrigoCredentials(pass: BrivoOnairPass): DomainState<Unit> {
        error("Not implemented/Not used")
    }

    override suspend fun getWalletEligibilityStatus(
        tokens: BrivoTokens,
        forced: Boolean
    ): BrivoSDKApiState<WalletEligibilityStatus> {
        error("Not implemented/Not used")
    }

    override suspend fun refreshAllegionCredentials(): DomainState<Unit> {
        val passes = retrieveSDKLocallyStoredPasses()
        if (passes is DomainState.Success) {
            val refreshAllegionResult = passes.data?.values?.toList()?.let {
                BrivoSDKBLEAllegion.refreshCredentials(it)
            }

            return when (refreshAllegionResult) {
                is BrivoSDKApiState.Failed -> {
                    DomainState.Failed(refreshAllegionResult.brivoError.toString())
                }

                else -> {
                    DomainState.Success(Unit)
                }
            }
        } else {
            return DomainState.Failed("Failed to get passes")
        }
    }


    override fun initLocalAuth(
        title: String,
        message: String,
        negativeButtonText: String,
        description: String
    ): DomainState<Unit> {
        try {
            BrivoSDKLocalAuthentication.init(
                App.instance.applicationContext,
                title,
                message,
                negativeButtonText,
                description
            )
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(
                e.message ?: "Failed to initialize Brivo Local Authentication SDK"
            )
        }

        return DomainState.Success(Unit)
    }

    override fun getVersion(): DomainState<String> {
        return try {
            DomainState.Success(BrivoSDK.version)
        } catch (e: BrivoSDKInitializationException) {
            DomainState.Failed(e.message ?: "Failed to get Brivo SDK version")
        }
    }

    override suspend fun redeemMobilePass(
        email: String,
        token: String
    ): DomainState<BrivoOnairPass?> {

        val result = BrivoSDKOnair.instance.redeemPass(email, token)
        return when (result) {
            is BrivoSDKApiState.Success -> {
                DomainState.Success(result.data)
            }

            is BrivoSDKApiState.Failed -> {
                DomainState.Failed(result.brivoError.message ?: "Failed to redeem pass")
            }
        }
    }

    override suspend fun retrieveSDKLocallyStoredPasses(): DomainState<Map<String, BrivoOnairPass>?> {
        try {
            val passes =
                BrivoSDKOnair.instance.retrieveSDKLocallyStoredPasses()
            return when (passes) {
                is BrivoSDKApiState.Success -> {
                    DomainState.Success(passes.data)
                }

                is BrivoSDKApiState.Failed -> {
                    DomainState.Failed(
                        passes.brivoError.message ?: "Failed to retrieve locally stored passes"
                    )
                }
            }
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(
                e.message ?: "Failed to retrieve locally stored passes"
            )
        }

    }

    override suspend fun refreshPass(
        refreshToken: String,
        accessToken: String?
    ): DomainState<BrivoOnairPass?> {
        return when (val request =
            BrivoSDKOnair.instance.refreshPass(BrivoTokens(accessToken, refreshToken))) {
            is BrivoSDKApiState.Success -> {
                DomainState.Success(request.data)
            }

            is BrivoSDKApiState.Failed -> {
                DomainState.Failed(request.brivoError.message ?: "Failed to refresh pass")
            }
        }

    }

    override fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        activity: FragmentActivity
    ): Flow<BrivoResult> {
        return BrivoSDKAccess.unlockAccessPoint(
            passId = passId,
            accessPointId = accessPointId,
            activity = activity
        )
    }

    override fun unlockNearestBLEAccessPoint(
        activity: FragmentActivity,
    ): Flow<BrivoResult> {
        return BrivoSDKAccess.unlockNearestBLEAccessPoint(
            activity = activity,
        )
    }
}
