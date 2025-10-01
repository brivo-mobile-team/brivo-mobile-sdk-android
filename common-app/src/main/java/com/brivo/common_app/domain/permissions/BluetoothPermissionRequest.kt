package com.brivo.common_app.domain.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.brivo.common_app.R
import com.brivo.common_app.domain.models.PermissionState
import com.brivo.sdk.BrivoSharedPreferences
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BluetoothPermissionRequest(private val activity: FragmentActivity) {

    private val requestPermission = RequestPermissions(activity)
    private val requiredPermissions: Array<String>
        get() {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    )
                }

                else -> arrayOf(Manifest.permission.BLUETOOTH)
            }
        }

    fun isBluetoothPermissionGranted(): Boolean {
        val permissionsStatus = requiredPermissions.map { permissionString ->
            ContextCompat.checkSelfPermission(
                activity,
                permissionString
            ) == PackageManager.PERMISSION_GRANTED
        }
        return permissionsStatus.all { it }
    }

    private fun checkBluetoothPermissions(): PermissionState {
        val wasPermissionAskedBefore =
            BrivoSharedPreferences.getBoolean(wasBluetoothPermissionAsked, false)

        val permissionsStates = requiredPermissions.map { permissionString ->
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
        if (permissionsStates.any { it == PermissionState.SHOW_RATIONALE }) {
            return PermissionState.SHOW_RATIONALE
        }
        if (permissionsStates.any { it == PermissionState.DENIED }) {
            return if (wasPermissionAskedBefore)
                PermissionState.DENIED_PERMANENTLY
            else PermissionState.DENIED
        }
        return PermissionState.GRANTED
    }

    suspend fun requestBluetoothPermissions(): Boolean {
        when (checkBluetoothPermissions()) {
            PermissionState.GRANTED -> {
                return true
            }
            PermissionState.SHOW_RATIONALE -> {
                BrivoSharedPreferences.putBoolean(wasBluetoothPermissionAsked, true)
                val userResponse = createBluetoothPermissionRationaleDialog()
                if (!userResponse) {
                    return false
                }
                val requestResult =
                    requestPermission.requestMultiplePermissions(requiredPermissions)
                if (requestResult.values.any { !it }) {
                    return false
                }
                return true
            }

            PermissionState.DENIED_PERMANENTLY -> {
                createBluetoothPermanentlyDeniedDialog()
                return false
            }

            PermissionState.DENIED -> {
                val requestResult =
                    requestPermission.requestMultiplePermissions(requiredPermissions)
                BrivoSharedPreferences.putBoolean(wasBluetoothPermissionAsked, true)
                if (requestResult.values.any { !it }) {
                    return false
                }
                return true
            }
        }
    }

    private suspend fun createBluetoothPermissionRationaleDialog(
        shouldShowCancelButton: Boolean = false
    ) = suspendCancellableCoroutine { continuation ->
        val description =
            activity.getString(R.string.bluetooth_permission_required_to_open_ble_doors)
        val title = activity.getString(R.string.bluetooth_permission_required)
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

    private suspend fun createBluetoothPermanentlyDeniedDialog() =
        suspendCoroutine { continuation ->
            val description: String
            val title: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                description =
                    activity.getString(R.string.bluetooth_nearby_permission_required_to_open_ble_doors)
                title = activity.getString(R.string.bluetooth_nearby_permission_required)
            } else {
                description =
                    activity.getString(R.string.bluetooth_permission_required_to_open_ble_doors)
                title = activity.getString(R.string.bluetooth_permission_required)
            }
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
        private const val wasBluetoothPermissionAsked: String = "WAS_BLUETOOTH_PERMISSION_ASKED"
    }

}
