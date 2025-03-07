package com.brivo.app_sdk_public

import android.app.Application
import com.brivo.app_sdk_public.core.usecase.InitializeBrivoSDKUseCase
import com.brivo.sdk.BrivoLog
import com.brivo.sdk.activitydelegate.BrivoSdkActivityDelegate
import com.brivo.sdk.enums.ServerRegion
import com.brivo.sdk.model.BrivoError
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), BrivoSdkActivityDelegate {

    companion object {
        lateinit var instance: App
            private set
    }

    @Inject
    lateinit var initializeBrivoSDKUseCase: InitializeBrivoSDKUseCase

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeBrivoMobileSDK()
    }

    private fun initializeBrivoMobileSDK() {
        initializeBrivoSDKUseCase.execute(serverRegion = ServerRegion.UNITED_STATES)
    }

    override fun log(message: String) {
        BrivoLog.i(message)
    }

    override fun error(error: BrivoError, throwable: Throwable) {
        BrivoLog.e(error.message)
    }
}