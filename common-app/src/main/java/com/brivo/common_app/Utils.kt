package com.brivo.common_app

import android.os.Build
import com.brivo.common_app.domain.permissions.BluetoothPermissionRequest
import com.brivo.common_app.domain.permissions.LocationPermissionRequest

suspend fun checkAndAskPermissions(
    hasTrustedNetwork: Boolean,
    locationPermissionRequest: LocationPermissionRequest,
    bluetoothPermissionRequest: BluetoothPermissionRequest
): Boolean {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R || hasTrustedNetwork) {
        locationPermissionRequest.requestFineAndCoarseLocation()
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        bluetoothPermissionRequest.requestBluetoothPermissions()
    } else true
}
