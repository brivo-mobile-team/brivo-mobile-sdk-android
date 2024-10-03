package com.brivo.app_sdk_public.di

import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepository
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.app_sdk_public.core.utils.BrivoSdkActivityDelegateImpl
import com.brivo.sdk.activitydelegate.BrivoSdkActivityDelegate
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

    @Provides
    @Singleton
    fun provideBrivoMobileSDK(
        brivoSdkActivityDelegate: BrivoSdkActivityDelegateImpl
    ): BrivoMobileSDKRepository{
        return BrivoMobileSDKRepositoryImpl(brivoSdkActivityDelegate)
    }
}