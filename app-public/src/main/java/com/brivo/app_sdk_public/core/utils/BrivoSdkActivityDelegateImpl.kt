package com.brivo.app_sdk_public.core.utils

import com.brivo.sdk.BrivoLog
import com.brivo.sdk.activitydelegate.BrivoSdkActivityDelegate
import com.brivo.sdk.model.BrivoError
import javax.inject.Inject

class BrivoSdkActivityDelegateImpl @Inject constructor() : BrivoSdkActivityDelegate {
    override fun error(error: BrivoError, throwable: Throwable) {
        BrivoLog.e(error.message)
    }

    override fun log(message: String) {
        BrivoLog.d(message)
    }
}