package com.brivo.app_sdk_public.core.usecase

import com.brivo.app_sdk_public.BrivoSampleConstants
import com.brivo.app_sdk_public.core.repository.BrivoMobileSDKRepositoryImpl
import com.brivo.sdk.enums.ServerRegion
import javax.inject.Inject

class InitializeBrivoSDKUseCase @Inject constructor(
    private val brivoSdkMobileRepository: BrivoMobileSDKRepositoryImpl
) {

    fun execute(serverRegion: ServerRegion) {
        when (serverRegion) {
            ServerRegion.UNITED_STATES -> {
                brivoSdkMobileRepository.init(
                    serverRegion = serverRegion,
                    clientId = BrivoSampleConstants.CLIENT_ID,
                    clientSecret = BrivoSampleConstants.CLIENT_SECRET,
                    authUrl = BrivoSampleConstants.AUTH_URL,
                    apiUrl = BrivoSampleConstants.API_URL
                )
            }

            ServerRegion.EUROPE -> {
                brivoSdkMobileRepository.init(
                    serverRegion = serverRegion,
                    clientId = BrivoSampleConstants.CLIENT_ID_EU,
                    clientSecret = BrivoSampleConstants.CLIENT_SECRET_EU,
                    authUrl = BrivoSampleConstants.AUTH_URL_EU,
                    apiUrl = BrivoSampleConstants.API_URL_EU
                )
            }
        }
    }
}
