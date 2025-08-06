package com.brivo.app_sdk_public.di

import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBrivoMobileSDKRepositoryImpl(): BrivoMobileSDKRepositoryImpl =
        BrivoMobileSDKRepositoryImpl()
}

@Module
@InstallIn(SingletonComponent::class)
interface AppBindingModule {

    @Binds
    @Singleton
    fun bindBrivoMobileSdkRepository(brivoMobileSDKRepositoryImpl: BrivoMobileSDKRepositoryImpl): BrivoMobileSDKRepository

}
