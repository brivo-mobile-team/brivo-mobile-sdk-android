package com.brivo.common_app.features.continuousscanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brivo.common_app.R

/**
 * Stateful, ViewModel-agnostic Continuous Scanner screen.
 *
 * Each sample app owns its own ViewModel and supplies the state and callbacks below, keeping the
 * continuous-scanning UI shared while leaving the ViewModel implementation per-app.
 */
@Composable
fun ContinuousScannerScreen(
    continuousScannerState: ContinuousScannerViewState,
    discoveredPeripherals: List<BleDeviceUIModel>,
    onCheckPermissions: suspend () -> Boolean,
    onStartScanning: () -> Unit,
    onDismissError: () -> Unit,
    onUnlockClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(continuousScannerState.scanningErrorMessage) {
        val message = continuousScannerState.scanningErrorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onDismissError()
        }
    }

    LaunchedEffect(Unit) {
        if (onCheckPermissions()) {
            onStartScanning()
        }
    }

    ContinuousScannerContent(
        continuousScannerState = continuousScannerState,
        discoveredPeripherals = discoveredPeripherals,
        snackbarHostState = snackbarHostState,
        onUnlockButtonClick = onUnlockClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinuousScannerContent(
    continuousScannerState: ContinuousScannerViewState,
    discoveredPeripherals: List<BleDeviceUIModel>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onUnlockButtonClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(id = R.string.continuous_scanner_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                ContinuousScannerDevicesList(
                    discoveredPeripherals = discoveredPeripherals,
                    onUnlockButtonClick = { },
                    continuousScannerUIState = continuousScannerState.uiState,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContinuousScannerButton(
                        viewState = continuousScannerState,
                        onUnlockClick = onUnlockButtonClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ContinuousScannerContentPreview() {
    val sampleDevices = listOf(
        BleDeviceUIModel("Device A", "UUID-1111", -50),
        BleDeviceUIModel("Device B", "UUID-2222", -60),
        BleDeviceUIModel("Device C", "UUID-3333", -70)
    )

    MaterialTheme {
        ContinuousScannerContent(
            continuousScannerState = ContinuousScannerViewState(uiState = ContinuousScannerUIState.SCANNING),
            discoveredPeripherals = sampleDevices,
            onUnlockButtonClick = {}
        )
    }
}
