package com.brivo.app_sdk.core

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk.App
import com.brivo.app_sdk.BrivoSampleConstants
import com.brivo.app_sdk.core.data.SampleAppDataStore
import com.brivo.common_app.model.DomainState
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import com.brivo.sdk.BrivoSDK
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.access.BrivoSDKAccess
import com.brivo.sdk.access.RefreshMode
import com.brivo.sdk.enums.ServerRegion
import com.brivo.sdk.enums.UnlockStrategy
import com.brivo.sdk.hidorigo.BrivoSDKHIDOrigo
import com.brivo.sdk.localauthentication.BrivoSDKLocalAuthentication
import com.brivo.sdk.model.AccessPointPath
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.model.BrivoSDKApiState
import com.brivo.sdk.model.configuration.BrivoConfiguration
import com.brivo.sdk.onair.model.BrivoAuthenticateResponse
import com.brivo.sdk.onair.model.BrivoListReaderCommandResponse
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.model.BrivoTokens
import com.brivo.sdk.onair.repository.BrivoSDKOnair
import com.brivo.sdk.smarthome.model.BrivoSDKSmartHomeConfiguration
import com.brivo.sdk.smarthome.repository.BrivoSDKSmartHome
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("RestrictedApi")
@Singleton
class BrivoMobileSDKRepositoryImpl @Inject constructor(
    private val sampleAppDataStore: SampleAppDataStore
) : BrivoMobileSDKRepository, ReaderCommandsRepository {

    override suspend fun init(
        serverRegion: ServerRegion,
        clientId: String,
        clientSecret: String,
        authUrl: String,
        apiUrl: String,
    ): DomainState<Unit> {
        try {
            val environment = sampleAppDataStore.getEnvironment()

            val brivoConfiguration = BrivoConfiguration.Builder(
                clientId = clientId,
                clientSecret = clientSecret,
                useSDKStorage = true
            ).authUrl(url = authUrl)
                .apiUrl(url = apiUrl)
                .allegionModule()
                .hidorigoModule(
                    config = BrivoSampleConstants.getHIDOrigoConfiguration(environment)
                )
                .dormakabaModule(
                    config = BrivoSampleConstants.getDormakabaConfig(environment)
                )
                .build()
            BrivoSDK.init(
                context = App.instance.applicationContext,
                brivoConfiguration = brivoConfiguration,
            )
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message ?: "Failed to initialize Brivo SDK")
        }

        try {
            BrivoSDKSmartHome.instance?.init(
                brivoSmartHomeConfiguration = BrivoSDKSmartHomeConfiguration(
                    apiUrl = BrivoSampleConstants.SMART_HOME_API_URL,
                    useSDKStorage = true
                )
            )
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message ?: "Failed to initialize Brivo Smart Home SDK")
        }

        return DomainState.Success(Unit)
    }

    override suspend fun refreshAllSDKs(passes: List<BrivoOnairPass>) {
        BrivoSDKAccess.refreshCredentials(passes, refreshMode = RefreshMode.FALLBACK_TO_LOCAL)
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

        return when (val result = BrivoSDKOnair.instance.redeemPass(email, token)) {
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
                DomainState.Failed(
                    error = request.brivoError.message ?: "Failed to refresh pass",
                    errorCode = request.brivoError.code
                )
            }
        }

    }

    override suspend fun refreshAccessTokenWithToken(
        accessToken: String,
        refreshToken: String
    ): DomainState<BrivoAuthenticateResponse> {
        return when (val refreshResult =
            BrivoSDKOnair.instance.refreshAccessTokenWithToken(accessToken, refreshToken)) {
            is BrivoSDKApiState.Failed -> {
                DomainState.Failed(
                    error = refreshResult.brivoError.message ?: "Failed to refresh pass",
                    errorCode = refreshResult.brivoError.code
                )
            }

            is BrivoSDKApiState.Success -> {
                DomainState.Success(refreshResult.data)
            }
        }
    }

    override fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        activity: FragmentActivity,
        unlockStrategy: UnlockStrategy?
    ): Flow<BrivoResult> {
        return BrivoSDKAccess.unlockAccessPoint(
            passId = passId,
            accessPointId = accessPointId,
            activity = activity,
            unlockStrategy = unlockStrategy
        )
    }

    override fun unlockNearestBLEAccessPoint(
        activity: FragmentActivity
    ): Flow<BrivoResult> {
        return BrivoSDKAccess.unlockNearestBLEAccessPoint(
            activity = activity,
        )
    }

    override suspend fun getWalletEligibilityStatus(
        tokens: BrivoTokens,
        forced: Boolean
    ): BrivoSDKApiState<com.brivo.common_app.repository.WalletEligibilityStatus> {
        return when (val result = BrivoSDKHIDOrigo.getWalletEligibilityStatus(
            tokens,
            forced
        )) {
            is BrivoSDKApiState.Failed -> {
                BrivoSDKApiState.Failed(result.brivoError)
            }

            is BrivoSDKApiState.Success -> {
                BrivoSDKApiState.Success(
                    com.brivo.common_app.repository.WalletEligibilityStatus(
                        result.data.hasPurchasedNFC,
                        result.data.hasCurrentCredential
                    )
                )
            }
        }
    }

    override suspend fun getReaderCommands(
        tokens: BrivoTokens,
        accessPointIds: Set<String>
    ): BrivoSDKApiState<BrivoListReaderCommandResponse> {
        return when (val result =
            BrivoSDKOnair.instance.getReaderCommands(tokens, accessPointIds)) {
            is BrivoSDKApiState.Failed -> {
                BrivoSDKApiState.Failed(result.brivoError)
            }

            is BrivoSDKApiState.Success -> {
                BrivoSDKApiState.Success(result.data)
            }
        }
    }

    override suspend fun engageReaderCommand(
        tokens: BrivoTokens,
        accessPointPath: AccessPointPath,
    ): BrivoSDKApiState<Unit> {
        return when (val result =
            BrivoSDKOnair.instance.engageReaderCommand(tokens, accessPointPath, "1")) {
            is BrivoSDKApiState.Failed -> {
                BrivoSDKApiState.Failed(result.brivoError)
            }

            is BrivoSDKApiState.Success -> {
                BrivoSDKApiState.Success(Unit)
            }
        }
    }
}
