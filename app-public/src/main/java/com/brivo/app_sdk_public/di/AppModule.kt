package com.brivo.app_sdk_public.di

import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepository
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
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
    fun provideBrivoMobileSDK(
    ): BrivoMobileSDKRepository {
        return BrivoMobileSDKRepositoryImpl()
    }
}
