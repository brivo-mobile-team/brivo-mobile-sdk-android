package com.demo.sample.kotlin

import android.app.Application
import android.content.Context
import com.brivo.sdk.BrivoLog
import com.brivo.sdk.BrivoSDK
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.activitydelegate.BrivoSdkActivityDelegate
import com.brivo.sdk.model.BrivoConfiguration
import com.brivo.sdk.model.BrivoError
import com.brivo.sdk.smarthome.model.BrivoSDKSmartHomeConfiguration
import com.brivo.sdk.smarthome.repository.BrivoSDKSmartHome

class BrivoApplication : Application(), BrivoSdkActivityDelegate {

    companion object {
        lateinit var instance: BrivoApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeBrivoMobileSDK()
    }

    fun initializeBrivoMobileSDK() {
        try {
            val sharedPreferences =
                getSharedPreferences(BrivoSampleConstants.CONFIG_KEY, Context.MODE_PRIVATE)
            val storedClientId = sharedPreferences.getString(
                BrivoSampleConstants.CLIENT_ID_KEY,
                BrivoSampleConstants.CLIENT_ID
            )
            val storedClientSecret = sharedPreferences.getString(
                BrivoSampleConstants.CLIENT_SECRET_KEY,
                BrivoSampleConstants.CLIENT_SECRET
            )

            BrivoSDK.getInstance().init(
                applicationContext, BrivoConfiguration(
                    clientId = storedClientId ?: BrivoSampleConstants.CLIENT_ID,
                    clientSecret = storedClientSecret ?: BrivoSampleConstants.CLIENT_SECRET,
                    authUrl = BrivoSampleConstants.AUTH_URL,
                    apiUrl = BrivoSampleConstants.API_URL,
                    useEURegion = false,
                    useSDKStorage = true,
                    shouldVerifyDoor = false
                )
            )
        } catch (e: BrivoSDKInitializationException) {
            e.printStackTrace()
        }

        try {
            BrivoSDKSmartHome.instance?.init(
                brivoSmartHomeConfiguration = BrivoSDKSmartHomeConfiguration(
                    apiUrl = BrivoSampleConstants.SMART_HOME_API_URL,
                    useSDKStorage = true
                ), activityDelegate = this
            )
        } catch (e: BrivoSDKInitializationException) {
            e.printStackTrace()
        }
    }

    override fun log(message: String) {
        BrivoLog.i(message)
    }

    override fun error(error: BrivoError, throwable: Throwable) {
        BrivoLog.e(error.message)
    }
}
