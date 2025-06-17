package com.brivo.app_sdk_public

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.brivo.app_sdk_public.navigation.MainNavigation
import com.brivo.app_sdk_public.ui.theme.AppTheme
import androidx.fragment.app.FragmentActivity
import com.brivo.app_sdk_public.domain.permissions.BluetoothPermissionRequest
import com.brivo.app_sdk_public.domain.permissions.LocationPermissionRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    val locationPermissionRequest = LocationPermissionRequest(this)
    val bluetoothPermissionRequest = BluetoothPermissionRequest(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BrivoSDKComposeSampleApp()
            }
        }
    }
}

@Composable
fun BrivoSDKComposeSampleApp() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainNavigation()
        }
    }
}
