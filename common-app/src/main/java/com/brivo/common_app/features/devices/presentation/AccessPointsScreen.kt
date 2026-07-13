package com.brivo.common_app.features.devices.presentation

import AlertMessageDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.brivo.common_app.R
import com.brivo.common_app.features.devices.model.AccessPointUIModel
import com.brivo.common_app.features.devices.model.AccessPointsUIEvent
import com.brivo.common_app.features.devices.model.AccessPointsViewState
import com.brivo.common_app.features.devices.model.ResideoThermostatUIModel
import com.brivo.common_app.features.devices.model.SiteDetailsBottomSheetUIModel
import com.brivo.sdk.enums.DoorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessPointsContent(
    state: AccessPointsViewState,
    onAccessPointPressed: (String, String, String) -> Unit,
    onThermostatPressed: (String) -> Unit,
    onEvent: (AccessPointsUIEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            if (state.siteName.isNotEmpty()) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(id = R.string.access_points, state.siteName),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(AccessPointsUIEvent.ShouldShowBottomSheet(true)) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Site details"
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.alertMessage.isNotEmpty()) {
                    AlertMessageDialog(
                        message = state.alertMessage,
                        onDialogClosed = { onEvent(AccessPointsUIEvent.UpdateAlertMessage("")) }
                    )
                }
                SiteDetailsBottomSheet(
                    sheetState = sheetState,
                    shouldShowBottomSheet = state.shouldShowBottomSheet,
                    siteDetailsBottomSheetUIModel = state.siteDetailsBottomSheetUIModel,
                    onDismissRequest = {
                        onEvent(AccessPointsUIEvent.ShouldShowBottomSheet(false))
                    }
                )
                DevicesList(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    loading = state.loading,
                    accessPoints = state.accessPoints,
                    thermostats = state.thermostats,
                    onAccessPointPressed = onAccessPointPressed,
                    onThermostatPressed = onThermostatPressed
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDetailsBottomSheet(
    sheetState: SheetState,
    shouldShowBottomSheet: Boolean,
    siteDetailsBottomSheetUIModel: SiteDetailsBottomSheetUIModel,
    onDismissRequest: () -> Unit
) {
    if(shouldShowBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Site Info Details",
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
                    text = "Site ID: ${siteDetailsBottomSheetUIModel.siteId}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Site Name: ${siteDetailsBottomSheetUIModel.siteName}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Has Trusted Network: ${siteDetailsBottomSheetUIModel.hasTrustedNetwork}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Pre-Screening: ${siteDetailsBottomSheetUIModel.preScreening}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Time Zone: ${siteDetailsBottomSheetUIModel.timeZone}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}


@Composable
fun DevicesList(
    modifier: Modifier = Modifier,
    loading: Boolean,
    accessPoints: List<AccessPointUIModel>,
    thermostats: List<ResideoThermostatUIModel>,
    onAccessPointPressed: (String, String, String) -> Unit,
    onThermostatPressed: (String) -> Unit
) {
    if (accessPoints.isEmpty() && thermostats.isEmpty() && !loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.access_points_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (thermostats.isEmpty() && accessPoints.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.brivo_access_points_label),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            items(accessPoints) { accessPoint ->
                AccessPointRow(
                    accessPoint = accessPoint,
                    onAccessPointPressed = onAccessPointPressed
                )
            }

            if (thermostats.isNotEmpty() && accessPoints.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.brivo_thermostats_label),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(thermostats) { thermostat ->
                ThermostatRow(
                    thermostat = thermostat,
                    onThermostatPressed = onThermostatPressed
                )
            }
        }
    }
}

@Composable
fun AccessPointRow(
    accessPoint: AccessPointUIModel,
    onAccessPointPressed: (String, String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = MaterialTheme.shapes.small)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.shapes.small
            )
            .clickable {
                onAccessPointPressed(
                    accessPoint.id,
                    accessPoint.accessPointName,
                    accessPoint.doorType.name
                )
            }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val drawable = when (accessPoint.doorType) {
            DoorType.INTERNET -> R.drawable.ic_net
            DoorType.ALLEGION, DoorType.ALLEGION_BLE -> R.drawable.ic_engage
            DoorType.WAVELYNX -> R.drawable.ic_brivo
            DoorType.HID_ORIGO, DoorType.HID_ORIGO_OMNIKEY -> R.drawable.ic_hid
            else -> null
        }
        if (drawable != null) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = drawable),
                contentDescription = stringResource(id = R.string.access_point_door_type)
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            text = accessPoint.accessPointName,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = accessPoint.accessPointName,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ThermostatRow(
    thermostat: ResideoThermostatUIModel,
    onThermostatPressed: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = MaterialTheme.shapes.small)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.shapes.small
            )
            .clickable { onThermostatPressed(thermostat.id) }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            text = thermostat.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = thermostat.currentTemperature,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

//@ThemedPreview
//@Composable
//fun AccessPointsPreview() {
//    val state = AccessPointsViewModel.AccessPointsViewState(
//        passId = "",
//        siteName = "siteName",
//        loading = false,
//        accessPoints = listOf(
//            AccessPointUIModel(
//                id = "0",
//                accessPointName = "Access Point 1",
//                doorType = DoorType.WAVELYNX
//            ),
//            AccessPointUIModel(
//                id = "1",
//                accessPointName = "Access Point 2",
//                doorType = DoorType.ALLEGION
//            ),
//            AccessPointUIModel(
//                id = "2",
//                accessPointName = "Access Point 3",
//                doorType = DoorType.WAVELYNX
//            ),
//            AccessPointUIModel(
//                id = "3",
//                accessPointName = "Access Point 4",
//                doorType = DoorType.INTERNET
//            ),
//            AccessPointUIModel(
//                id = "4",
//                accessPointName = "Access Point 5",
//                doorType = DoorType.WAVELYNX
//            ),
//            AccessPointUIModel(
//                id = "5",
//                accessPointName = "Access Point 6",
//                doorType = DoorType.INTERNET
//            ),
//            AccessPointUIModel(
//                id = "6",
//                accessPointName = "Access Point 7",
//                doorType = DoorType.ALLEGION
//            ),
//            AccessPointUIModel(
//                id = "7",
//                accessPointName = "Access Point 8",
//                doorType = DoorType.WAVELYNX
//            ),
//            AccessPointUIModel(
//                id = "8",
//                accessPointName = "Access Point 9",
//                doorType = DoorType.WAVELYNX
//            ),
//        )
//    )
//    AppTheme {
//        AccessPointsContent(
//            onAccessPointPressed = { _, _ -> },
//            onEvent = { },
//            state = state
//        )
//    }
//}
//
