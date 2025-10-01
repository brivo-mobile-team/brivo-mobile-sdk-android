package com.brivo.app_sdk_public.features.unlockdoor

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.common_app.R
import com.brivo.common_app.features.unlockdoor.model.UnlockDoorUIEvent
import com.brivo.common_app.features.unlockdoor.presentation.UnlockDoorContent
import com.brivo.sdk.enums.DoorType
import com.brivo.sdk.onair.model.BrivoBluetoothReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockDoorScreen(
    onBackPressed: () -> Unit,
    onCheckPermissions: suspend (hasTrustedNetwork: Boolean) -> Boolean,
    viewModel: UnlockDoorViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        textAlign = TextAlign.Center,
                        text = if (state.accessPointName.isNotEmpty())
                            stringResource(id = R.string.unlock_door_title, state.accessPointName)
                        else stringResource(id = R.string.unlock_door_with_magic_button_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.updateShouldShowBottomSheet(true)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Access Point Details"
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            UnlockDoorContent(
                doorState = state.doorState,
                showSnackbar = state.showSnackbar,
                hasTrustedNetwork = state.hasTrustedNetwork,
                accessPointName = state.accessPointName,
                onEvent = viewModel::onEvent,
                onCheckPermissions = onCheckPermissions
            )

            Spacer(Modifier.height(24.dp))
            DoorDetailsBottomSheet(
                sheetState = sheetState,
                siteId = state.doorDetailsBottomSheetUIModel.siteId,
                siteName = state.doorDetailsBottomSheetUIModel.siteName,
                doorType = state.doorDetailsBottomSheetUIModel.doorType,
                isTwoFactorEnabled = state.doorDetailsBottomSheetUIModel.isTwoFactorEnabled,
                bluetoothReader = state.doorDetailsBottomSheetUIModel.bluetoothReader,
                controlLockSerialNumber = state.doorDetailsBottomSheetUIModel.controlLockSerialNumber,
                shouldShowBotomSheet = state.showBottomSheet,
                onDismissRequest = {
                    viewModel.updateShouldShowBottomSheet(false)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoorDetailsBottomSheet(
    sheetState: SheetState,
    shouldShowBotomSheet: Boolean,
    siteId: String,
    siteName: String,
    doorType: DoorType,
    isTwoFactorEnabled: Boolean,
    bluetoothReader: BrivoBluetoothReader,
    controlLockSerialNumber: String,
    onDismissRequest: () -> Unit
) {
    if(shouldShowBotomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Door Details",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Site ID: $siteId}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Site Name: $siteName",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Door Type: $doorType",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Two Factor Enabled: $isTwoFactorEnabled",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Bluetooth Reader: $bluetoothReader",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Control Lock Serial Number: $controlLockSerialNumber",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

