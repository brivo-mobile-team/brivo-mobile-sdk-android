package com.brivo.app_sdk_public

import android.app.Application
import com.brivo.app_sdk_public.core.usecase.InitializeBrivoSDKUseCase
import com.brivo.sdk.BrivoSharedPreferences
import com.brivo.sdk.enums.ServerRegion
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    @Inject
    lateinit var initializeBrivoSDKUseCase: InitializeBrivoSDKUseCase

    @Inject
    lateinit var appScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        instance = this
        BrivoSharedPreferences.Builder()
            .setContext(applicationContext)
            .build()
        initializeBrivoMobileSDK()
    }

    private fun initializeBrivoMobileSDK() {
        appScope.launch {
            initializeBrivoSDKUseCase.execute(serverRegion = ServerRegion.UNITED_STATES)
        }
    }
}
