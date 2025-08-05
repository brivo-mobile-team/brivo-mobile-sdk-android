package com.brivo.common_app.domain.usecases

import com.brivo.sdk.BrivoSDK
import javax.inject.Inject

class GetBrivoSDKVersionUseCase @Inject constructor() {

    fun execute() = BrivoSDK.version
}
