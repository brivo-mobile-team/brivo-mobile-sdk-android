package com.brivo.app_sdk_public.features.accesspoints.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.app_sdk_public.R
import com.brivo.sdk.enums.DoorType
import com.brivo.app_sdk_public.features.accesspoints.model.AccessPointUIModel
import com.brivo.app_sdk_public.features.accesspoints.model.AccessPointsUIEvent
import com.brivo.app_sdk_public.ui.theme.AppTheme
import com.brivo.app_sdk_public.view.AlertMessageDialog
import com.brivo.app_sdk_public.view.ThemedPreview

@Composable
fun AccessPointsScreen(
    viewModel: AccessPointsViewModel = hiltViewModel(),
    onAccessPointPressed: (String, String, String, Boolean) -> Unit
) {

    val state by viewModel.state.collectAsState()

    AccessPointsContent(
        onAccessPointPressed = { accessPointId, accessPointName ->
            onAccessPointPressed(state.passId, accessPointId, accessPointName, state.selectedSiteHasTrustedNetwork)
        },
        onEvent = viewModel::onEvent,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessPointsContent(
    onAccessPointPressed: (String, String) -> Unit,
    onEvent: (AccessPointsUIEvent) -> Unit,
    state: AccessPointsViewModel.AccessPointsViewState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (state.siteName.isNotEmpty()) {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(8.dp),
                    title = {
                        Text(
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(id = R.string.access_points, state.siteName)
                        )
                    }
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

                AccessPointsList(
                    accessPoints = state.accessPoints,
                    loading = state.loading,
                    onAccessPointPressed = onAccessPointPressed
                )
            }
        }
    )
}

@Composable
fun AccessPointsList(
    modifier: Modifier = Modifier,
    loading: Boolean,
    accessPoints: List<AccessPointUIModel>,
    onAccessPointPressed: (String, String) -> Unit
) {

    if (accessPoints.isEmpty() && !loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.access_points_empty)
            )
        }
    } else {
        LazyColumn {
            itemsIndexed(accessPoints) { _, accessPoint ->
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable { onAccessPointPressed(accessPoint.id, accessPoint.accessPointName) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val drawable = when (accessPoint.doorType) {
                        DoorType.INTERNET -> R.drawable.ic_net
                        DoorType.ALLEGION, DoorType.ALLEGION_BLE -> R.drawable.ic_engage
                        DoorType.WAVELYNX -> R.drawable.ic_brivo
                        else -> null
                    }
                    if (drawable != null) {
                        Image(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(id = drawable),
                            contentDescription = stringResource(id = R.string.access_point_door_type)
                        )
                    }
                    Text(
                        modifier = Modifier.padding(start = 16.dp).weight(1f),
                        text = accessPoint.accessPointName
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = accessPoint.accessPointName
                    )
                }
            }
        }
    }
}

@ThemedPreview
@Composable
fun AccessPointsPreview() {
    val state = AccessPointsViewModel.AccessPointsViewState(
        passId = "",
        siteName = "siteName",
        loading = false,
        accessPoints = listOf(
            AccessPointUIModel(id = "0", accessPointName = "Access Point 1", doorType = DoorType.WAVELYNX),
            AccessPointUIModel(id = "1", accessPointName = "Access Point 2", doorType = DoorType.ALLEGION),
            AccessPointUIModel(id = "2", accessPointName = "Access Point 3", doorType = DoorType.WAVELYNX),
            AccessPointUIModel(id = "3", accessPointName = "Access Point 4", doorType = DoorType.INTERNET),
            AccessPointUIModel(id = "4", accessPointName = "Access Point 5", doorType = DoorType.WAVELYNX),
            AccessPointUIModel(id = "5", accessPointName = "Access Point 6", doorType = DoorType.INTERNET),
            AccessPointUIModel(id = "6", accessPointName = "Access Point 7", doorType = DoorType.ALLEGION),
            AccessPointUIModel(id = "7", accessPointName = "Access Point 8", doorType = DoorType.WAVELYNX),
            AccessPointUIModel(id = "8", accessPointName = "Access Point 9", doorType = DoorType.WAVELYNX),
        )
    )
    AppTheme {
        AccessPointsContent(
            onAccessPointPressed = { _, _ -> },
            onEvent = { },
            state = state
        )
    }
}

@ThemedPreview
@Composable
fun AccessPointsEmptyPreview() {
    val state = AccessPointsViewModel.AccessPointsViewState(
        passId = "",
        siteName = "siteName",
        loading = false,
        accessPoints = emptyList()
    )
    AppTheme {
        AccessPointsContent(
            onAccessPointPressed = { _, _ -> },
            onEvent = { },
            state = state
        )
    }
}
