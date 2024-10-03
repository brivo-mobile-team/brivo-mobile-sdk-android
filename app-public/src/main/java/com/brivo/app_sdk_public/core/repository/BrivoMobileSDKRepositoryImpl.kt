package com.brivo.app_sdk_public.core.repository

import android.annotation.SuppressLint
import android.os.CancellationSignal
import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.App
import com.brivo.app_sdk_public.BrivoSampleConstants
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.utils.BrivoSdkActivityDelegateImpl
import com.brivo.app_sdk_public.features.unlockdoor.model.UnlockDoorListener
import com.brivo.sdk.BrivoSDK
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.access.BrivoSDKAccess
import com.brivo.sdk.ble.allegion.BrivoSDKBLEAllegion
import com.brivo.sdk.interfaces.IOnCommunicateWithAccessPointListener
import com.brivo.sdk.localauthentication.BrivoSDKLocalAuthentication
import com.brivo.sdk.model.BrivoConfiguration
import com.brivo.sdk.model.BrivoError
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.onair.interfaces.IOnRedeemPassListener
import com.brivo.sdk.onair.interfaces.IOnRetrieveSDKLocallyStoredPassesListener
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.model.BrivoTokens
import com.brivo.sdk.onair.repository.BrivoSDKOnair
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("RestrictedApi")
class BrivoMobileSDKRepositoryImpl @Inject constructor(
    private val brivoSdkActivityDelegate: BrivoSdkActivityDelegateImpl
) : BrivoMobileSDKRepository {

    override fun init(isEURegion: Boolean): DomainState<Unit> {
        try {
            BrivoSDK.getInstance().init(
                context = App.instance.applicationContext,
                brivoConfiguration = BrivoConfiguration(
                    clientId = if (isEURegion) BrivoSampleConstants.CLIENT_ID_EU else BrivoSampleConstants.CLIENT_ID,
                    clientSecret = if (isEURegion) BrivoSampleConstants.CLIENT_SECRET_EU else BrivoSampleConstants.CLIENT_SECRET,
                    authUrl = if (isEURegion) BrivoSampleConstants.AUTH_URL_EU else BrivoSampleConstants.AUTH_URL,
                    apiUrl = if (isEURegion) BrivoSampleConstants.API_URL_EU else BrivoSampleConstants.API_URL,
                    useEURegion = isEURegion,
                    useSDKStorage = true,
                    shouldVerifyDoor = false
                )
            )
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message!!)
        }
        try {
            BrivoSDKBLEAllegion.getInstance().init()
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message ?: "Failed to initialize BrivoSDKBLEAllegion")
        }

        return DomainState.Success(Unit)
    }

    override suspend fun refreshAllegionCredentials(passes: List<BrivoOnairPass>) {
        passes.forEach {
            BrivoSDKBLEAllegion.getInstance().refreshCredentials(it)
        }
    }

    override suspend fun refreshAllegionCredential(pass: BrivoOnairPass) {
        BrivoSDKBLEAllegion.getInstance().refreshCredentials(pass)
    }

    override fun initLocalAuth(
        title: String,
        message: String,
        negativeButtonText: String,
        description: String
    ): DomainState<Unit> {
        try {
            BrivoSDKLocalAuthentication.getInstance().init(
                App.instance.applicationContext,
                title,
                message,
                negativeButtonText,
                description
            )
        } catch (e: BrivoSDKInitializationException) {
            return DomainState.Failed(e.message!!)
        }

        return DomainState.Success(Unit)
    }

    override fun getVersion(): DomainState<String> {
        return try {
            DomainState.Success(BrivoSDK.getInstance().version)
        } catch (e: BrivoSDKInitializationException) {
            DomainState.Failed(e.message!!)
        }
    }

    override suspend fun redeemMobilePass(
        email: String,
        token: String
    ): DomainState<BrivoOnairPass?> {
        return suspendCoroutine { cont ->
            try {
                BrivoSDKOnair.instance?.redeemPass(email, token, object : IOnRedeemPassListener {
                    override fun onSuccess(pass: BrivoOnairPass?) {
                        cont.resume(DomainState.Success(pass))
                    }

                    override fun onFailed(error: BrivoError) {
                        cont.resume(DomainState.Failed(error.message))
                    }
                })
            } catch (e: BrivoSDKInitializationException) {
                cont.resume(DomainState.Failed(e.message!!))
            }
        }
    }

    override suspend fun retrieveSDKLocallyStoredPasses(): DomainState<LinkedHashMap<String, BrivoOnairPass>?> {
        return suspendCoroutine { cont ->
            try {
                BrivoSDKOnair.instance?.retrieveSDKLocallyStoredPasses(object :
                    IOnRetrieveSDKLocallyStoredPassesListener {
                    override fun onSuccess(passes: LinkedHashMap<String, BrivoOnairPass>?) {
                        cont.resume(DomainState.Success(passes))
                    }

                    override fun onFailed(error: BrivoError) {
                        cont.resume(DomainState.Failed(error.message))
                    }
                })
            } catch (e: BrivoSDKInitializationException) {
                cont.resume(DomainState.Failed(e.message!!))
            }
        }
    }

    override suspend fun refreshPass(
        refreshToken: String,
        accessToken: String?
    ): DomainState<Unit> {
        return suspendCoroutine { cont ->
            try {
                BrivoSDKOnair.instance?.refreshPass(BrivoTokens(accessToken, refreshToken),
                    object : IOnRedeemPassListener {
                        override fun onSuccess(pass: BrivoOnairPass?) {
                            cont.resume(DomainState.Success(Unit))
                        }

                        override fun onFailed(error: BrivoError) {
                            cont.resume(DomainState.Failed(error.message))
                        }
                    }
                )
            } catch (e: BrivoSDKInitializationException) {
                cont.resume(DomainState.Failed(e.message!!))
            }
        }
    }

    override fun unlockAccessPoint(
        passId: String,
        accessPointId: String,
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener,
        activity: FragmentActivity
    ): DomainState<Unit> {
        return try {
            BrivoSDKAccess.getInstance().unlockAccessPoint(
                passId = passId,
                accessPointId = accessPointId,
                cancellationSignal = cancellationSignal,
                object : IOnCommunicateWithAccessPointListener {
                    override fun onResult(result: BrivoResult) {
                        listener.onUnlockDoorEvent(result)
                    }
                },
                activity = activity
            )
            DomainState.Success(Unit)
        } catch (e: Exception) {
            DomainState.Failed(e.message!!)
        }
    }

    override fun unlockNearestBLEAccessPoint(
        cancellationSignal: CancellationSignal,
        listener: UnlockDoorListener,
        activity: FragmentActivity
    ): DomainState<Unit> {
        return try {
            BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(
                cancellationSignal = cancellationSignal,
                object : IOnCommunicateWithAccessPointListener {
                    override fun onResult(result: BrivoResult) {
                        listener.onUnlockDoorEvent(result)
                    }
                },
                activity = activity
            )
            DomainState.Success(Unit)
        } catch (e: Exception) {
            DomainState.Failed(e.message!!)
        }
    }
}