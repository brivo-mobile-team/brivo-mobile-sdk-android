package com.brivo.app_sdk_sample.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object AppUtils {

    fun navigateToPermissionSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}