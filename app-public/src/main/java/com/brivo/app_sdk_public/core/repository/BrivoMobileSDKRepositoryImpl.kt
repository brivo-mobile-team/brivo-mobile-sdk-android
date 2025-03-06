package com.brivo.app_sdk_public.core.repository

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.App
import com.brivo.app_sdk_public.BrivoSampleConstants
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.utils.BrivoSdkActivityDelegateImpl
import com.brivo.sdk.BrivoLog
import com.brivo.sdk.BrivoSDK
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.access.BrivoSDKAccess
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
    private val brivoSdkActivityDelegate: BrivoSdkActivityDelegateImpl
) : BrivoMobileSDKRepository {

    override fun init(serverRegion: ServerRegion): DomainState<Unit> {
        val (clientId, clientSecret) = when (serverRegion) {
            ServerRegion.UNITED_STATES -> BrivoSampleConstants.CLIENT_ID to BrivoSampleConstants.CLIENT_SECRET
            ServerRegion.EUROPE -> BrivoSampleConstants.CLIENT_ID_EU to BrivoSampleConstants.CLIENT_SECRET_EU
        }
        val (authUrl, apiUrl) = when (serverRegion) {
            ServerRegion.UNITED_STATES -> BrivoSampleConstants.AUTH_URL to BrivoSampleConstants.API_URL
            ServerRegion.EUROPE -> BrivoSampleConstants.AUTH_URL_EU to BrivoSampleConstants.API_URL_EU
        }
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
            //TODO uncomment if using allegion SDK
//            val result = BrivoSDKBLEAllegion.init()
//            if(result is BrivoSDKApiState.Failed) {
//                BrivoLog.e("Failed to initialize BrivoSDKBLEAllegion: ${result.brivoError.message}")
//            }
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message ?: "Failed to initialize BrivoSDKBLEAllegion")
        }

        return DomainState.Success(Unit)
    }

    override suspend fun refreshAllegionCredentials(passes: List<BrivoOnairPass>) {
        //TODO uncomment if using allegion SDK
        passes.forEach {
//            BrivoSDKBLEAllegion.refreshCredentials(it)
        }
    }

    override suspend fun refreshAllegionCredential(pass: BrivoOnairPass) {
        //TODO uncomment if using allegion SDK
//        BrivoSDKBLEAllegion.refreshCredentials(pass)
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