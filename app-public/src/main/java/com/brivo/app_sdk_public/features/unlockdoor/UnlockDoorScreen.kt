package com.brivo.app_sdk_public.features.unlockdoor

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.common_app.R
import com.brivo.common_app.features.unlockdoor.model.UnlockDoorUIEvent
import com.brivo.common_app.features.unlockdoor.presentation.UnlockDoorContent

@Composable
fun UnlockDoorScreen(
    onBackPressed: () -> Unit,
    onCheckPermissions: suspend (hasTrustedNetwork: Boolean) -> Boolean,
    viewModel: UnlockDoorViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(
            UnlockDoorUIEvent.InitLocalAuth(
                title = context.resources.getString(R.string.unlock_door_two_factor_dialog_title),
                message = context.resources.getString(R.string.unlock_door_two_factor_dialog_message),
                negativeButtonText = context.resources.getString(R.string.unlock_door_two_factor_dialog_cancel),
                description = ""
            )
        )
    }

    if (state.alertMessage.isNotEmpty()) {
        Toast.makeText(context, state.alertMessage, Toast.LENGTH_SHORT).show()
    }

    UnlockDoorContent(
        doorState = state.doorState,
        accessPointName = state.accessPointName,
        showSnackbar = state.showSnackbar,
        hasTrustedNetwork = state.hasTrustedNetwork,
        onEvent = viewModel::onEvent,
        onCheckPermissions
    )
}
