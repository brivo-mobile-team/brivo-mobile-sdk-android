package com.brivo.app_sdk_public

import android.app.Application
import com.brivo.app_sdk_public.core.usecase.InitializeBrivoSDKUseCase
import com.brivo.sdk.enums.ServerRegion
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

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
}
