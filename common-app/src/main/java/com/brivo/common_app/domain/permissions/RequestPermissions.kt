package com.brivo.common_app.domain.permissions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

class RequestPermissions(activity: ComponentActivity) {
    private var requestPermissionContinuation: CancellableContinuation<Boolean>? = null
    private var requestMultiplePermissionsContinuation: CancellableContinuation<Map<String, Boolean>>? = null

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            requestPermissionContinuation?.resumeWith(Result.success(isGranted))
        }

    private val requestMultiplePermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            requestMultiplePermissionsContinuation?.resumeWith(Result.success(permissions))
        }

    suspend fun requestPermission(permission: String) =
        suspendCancellableCoroutine<Boolean> { continuation ->
            requestPermissionContinuation = continuation
            requestPermissionLauncher.launch(permission)
            continuation.invokeOnCancellation {
                requestPermissionContinuation = null
            }
        }

    suspend fun requestMultiplePermissions(permissions: Array<String>) = suspendCancellableCoroutine { continuation ->
        requestMultiplePermissionsContinuation = continuation
        requestMultiplePermissionLauncher.launch(permissions)
        continuation.invokeOnCancellation {
            requestMultiplePermissionsContinuation =  null
        }
    }
    fun navigateToPermissionSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        startActivity(context,intent, null)
    }


}
