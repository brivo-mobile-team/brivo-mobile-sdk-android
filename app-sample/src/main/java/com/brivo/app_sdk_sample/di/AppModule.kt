package com.brivo.app_sdk_sample.di

import com.brivo.sdk.activitydelegate.BrivoSdkActivityDelegate
import com.brivo.app_sdk_sample.core.utils.BrivoSdkActivityDelegateImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideBrivoSdkActivityDelegate(): BrivoSdkActivityDelegate = BrivoSdkActivityDelegateImpl()
}