package com.brivo.app_sdk_public.features.accesspoints

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.common_app.features.accesspoints.presentation.AccessPointsContent

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
