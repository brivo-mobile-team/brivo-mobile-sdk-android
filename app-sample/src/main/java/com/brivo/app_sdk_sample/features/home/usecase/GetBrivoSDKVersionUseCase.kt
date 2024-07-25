package com.brivo.app_sdk_sample.features.home.usecase

import com.brivo.sdk.BrivoSDK
import javax.inject.Inject

class GetBrivoSDKVersionUseCase @Inject constructor() {

    fun execute() = BrivoSDK.getInstance().version
}