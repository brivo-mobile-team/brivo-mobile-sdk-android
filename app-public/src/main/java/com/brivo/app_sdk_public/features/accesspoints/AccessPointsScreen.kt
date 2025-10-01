package com.brivo.app_sdk_public.features.accesspoints

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.common_app.features.accesspoints.presentation.AccessPointsContent

@Composable
fun AccessPointsScreen(
    viewModel: AccessPointsViewModel = hiltViewModel(),
    onAccessPointPressed: (String, String, String, String, String, Boolean) -> Unit
) {

    val state by viewModel.state.collectAsState()

    AccessPointsContent(
        state = state,
        onAccessPointPressed = { accessPointId, accessPointName, accessPointType ->
            onAccessPointPressed(state.passId, accessPointId, state.siteId,accessPointName, accessPointType, state.selectedSiteHasTrustedNetwork)
        },
        onEvent = viewModel::onEvent,
    )
}
