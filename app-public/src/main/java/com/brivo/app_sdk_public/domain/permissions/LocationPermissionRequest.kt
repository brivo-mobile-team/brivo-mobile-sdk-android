package com.brivo.app_sdk_public.domain.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.R
import com.brivo.app_sdk_public.domain.models.PermissionState
import com.brivo.sdk.BrivoSharedPreferences
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationPermissionRequest(private val activity: FragmentActivity) {

    private val requestPermission = RequestPermissions(activity)

    private val defaultRequiredPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val backgroundPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION

    fun isLocationPermissionGranted(checkBackgroundLocation: Boolean): Boolean {
        val requiredPermissions =
            if (checkBackgroundLocation) arrayOf(
                *defaultRequiredPermissions,
                backgroundPermission
            ) else defaultRequiredPermissions
        val permissionStatus = requiredPermissions.map { permissionString ->
            ContextCompat.checkSelfPermission(
                activity,
                permissionString
            ) == PackageManager.PERMISSION_GRANTED
        }
        return permissionStatus.all { it }
    }

    private fun checkLocationPermissions(): PermissionState {
        val wasPermissionAskedBefore =
            BrivoSharedPreferences.getBoolean(WAS_LOCATION_PERMISSION_ASKED, false)
        val defaultPermissionsStates = defaultRequiredPermissions.map { permissionString ->
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    permissionString
                ) == PackageManager.PERMISSION_GRANTED -> {
                    PermissionState.GRANTED
                }

                shouldShowRequestPermissionRationale(
                    activity,
                    permissionString
                ) -> {
                    PermissionState.SHOW_RATIONALE
                }

                else -> {
                    PermissionState.DENIED
                }
            }
        }
        if (defaultPermissionsStates.any { it == PermissionState.SHOW_RATIONALE }) {
            return PermissionState.SHOW_RATIONALE
        }
        if (defaultPermissionsStates.any { it == PermissionState.DENIED }) {
            return if (wasPermissionAskedBefore)
                PermissionState.DENIED_PERMANENTLY
            else PermissionState.DENIED
        }
        return PermissionState.GRANTED
    }

    suspend fun requestFineAndCoarseLocation(): Boolean {
        when (checkLocationPermissions()) {
            PermissionState.GRANTED -> {
                return true
            }

            PermissionState.SHOW_RATIONALE -> {
                BrivoSharedPreferences.putBoolean(WAS_LOCATION_PERMISSION_ASKED, true)
                val userResponse = createLocationRationaleDialog()
                if (!userResponse)
                    return false
                val permissionsState =
                    requestPermission.requestMultiplePermissions(defaultRequiredPermissions)

                return !permissionsState.values.any { !it }
            }

            PermissionState.DENIED_PERMANENTLY -> {
                createLocationPermanentlyDeniedDialog(false)
                return false
            }

            PermissionState.DENIED -> {
                val permissionState =
                    requestPermission.requestMultiplePermissions(defaultRequiredPermissions)

                BrivoSharedPreferences.putBoolean(WAS_LOCATION_PERMISSION_ASKED, true)
                return !permissionState.values.any { !it }
            }
        }
    }

    private suspend fun createLocationRationaleDialog(
        shouldShowCancelButton: Boolean = false
    ) = suspendCancellableCoroutine { continuation ->
        val description =
            activity.getString(R.string.precise_location_permisison_request_message)
        val title = activity.getString(R.string.precise_location_permission_request_title)
        val alert = AlertDialog.Builder(activity).setTitle(title)
            .setMessage(description).setCancelable(shouldShowCancelButton)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                continuation.resume(true)
            }
            .setOnCancelListener {
                continuation.resume(false)
            }
        if (shouldShowCancelButton) {
            alert.setNegativeButton(android.R.string.cancel) { _, _ ->
                continuation.resume(false)
            }
        }
        alert.show()
    }


    private suspend fun createLocationPermanentlyDeniedDialog(isBackgroundPermission: Boolean) =
        suspendCoroutine { continuation ->
            val description =
                if (isBackgroundPermission)
                    activity.getString(R.string.widget_background_location_permission_request_message)
                else activity.getString(R.string.precise_location_permisison_request_message)
            val title = if (isBackgroundPermission)
                activity.getString(R.string.widget_background_location_permission_request_title)
            else activity.getString(R.string.precise_location_permission_request_title)
            val alert = AlertDialog.Builder(activity).setTitle(title)
                .setMessage(description).setCancelable(true)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermission.navigateToPermissionSettings(activity)
                    continuation.resume(false)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    continuation.resume(false)
                }
                .setOnCancelListener {
                    continuation.resume(false)
                }
            alert.show()

        }

    companion object {
        const val WAS_LOCATION_PERMISSION_ASKED: String = "WAS_LOCATION_PERMISSION_ASKED"

    }

}
