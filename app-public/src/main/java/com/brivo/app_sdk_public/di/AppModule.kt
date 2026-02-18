package com.brivo.app_sdk_public.di

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.common_app.repository.BrivoMobileSDKRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBrivoMobileSDKRepositoryImpl(): BrivoMobileSDKRepositoryImpl =
        BrivoMobileSDKRepositoryImpl()

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = ProcessLifecycleOwner.get().lifecycleScope
}

@Module
@InstallIn(SingletonComponent::class)
interface AppBindingModule {

    @Binds
    @Singleton
    fun bindBrivoMobileSdkRepository(brivoMobileSDKRepositoryImpl: BrivoMobileSDKRepositoryImpl): BrivoMobileSDKRepository

}
