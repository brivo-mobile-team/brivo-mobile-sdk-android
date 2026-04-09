package com.brivo.app_sdk_public.features.thermostat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.common_app.features.thermostat.presentation.ThermostatContent

@Composable
fun ThermostatScreen(
    onBackPressed: () -> Unit,
    viewModel: ThermostatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        ThermostatContent(
            modifier      = Modifier.padding(innerPadding),
            state         = state,
            uiEffect      = viewModel.uiEffect,
            onBackPressed = onBackPressed,
            onEvent       = viewModel::onEvent
        )
    }
}
