package com.brivo.app_sdk_public.features.home.presentation

import AlertMessageDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.brivo.app_sdk_public.R
import com.brivo.app_sdk_public.features.home.model.HomeUIEvent
import com.brivo.app_sdk_public.ui.theme.AppTheme
import com.brivo.app_sdk_public.view.ThemedPreview
import com.brivo.common_app.features.home.model.BrivoOnairPassUIModel
import com.brivo.common_app.features.home.model.BrivoSiteUIModel
import com.brivo.common_app.features.home.presentation.PassesList
import com.brivo.common_app.view.ComposableLifecycle

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRedeemPassPressed: () -> Unit,
    onSitePressed: (String, String) -> Unit,
    onContinuousScannerPressed: () -> Unit,
    onUnlockNearestAccessPointPressed: () -> Unit,
) {

    ComposableLifecycle { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(HomeUIEvent.LoadPasses)
            viewModel.onEvent(HomeUIEvent.RefreshPasses)
        }
    }

    val state by viewModel.state.collectAsState()
    HomeScreenContent(
        state = state,
        onRedeemPassPressed = onRedeemPassPressed,
        onSitePressed = onSitePressed,
        onContinuousScannerPressed = onContinuousScannerPressed,
        onUnlockNearestAccessPointPressed = onUnlockNearestAccessPointPressed,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeViewModel.HomeViewState,
    onRedeemPassPressed: () -> Unit,
    onSitePressed: (String, String) -> Unit,
    onContinuousScannerPressed: () -> Unit,
    onUnlockNearestAccessPointPressed: () -> Unit,
    onEvent: (HomeUIEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.sdk_version, state.version)) },
                actions = {
                    IconButton(
                        onClick = onRedeemPassPressed
                    ) {
                        Icon(Icons.Filled.Add, stringResource(id = R.string.redeem_pass))
                    }
                }
            )
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
                        onDialogClosed = { onEvent(HomeUIEvent.UpdateAlertMessage("")) }
                    )
                }
                if (state.passes.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onContinuousScannerPressed() }
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(12.dp),
                        text = stringResource(id = R.string.home_continuous_scanner),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUnlockNearestAccessPointPressed() }
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(12.dp),
                        text = stringResource(id = R.string.home_unlock_nearest_access_point),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.padding(12.dp))
                }
                PassesList(
                    passes = state.passes,
                    loading = state.loading,
                    refreshing = state.refreshing,
                    onSitePressed = onSitePressed,
                    onRefresh = {
                        onEvent(HomeUIEvent.RefreshPasses)
                    },
                    shouldShowBottomSheet = state.passDetailsBottomSheetUIModel.shouldShowBottomSheet,
                    passDetailsBottomSheetUIModel = state.passDetailsBottomSheetUIModel,
                    onInfoButtonClicked = {
                        onEvent(
                            HomeUIEvent.ShouldShowBotomSheet(
                                shouldShow = true
                            )
                        )
                    },
                    onUpdateBottomSheetInformation = { hasAllegionBleCredentials, hasHidOrigoMobilePass, hidOrigoWalletPassEnabled, hasBrivoWalletPass, dormakabaMobilePassEnabled ->
                        onEvent(
                            HomeUIEvent.UpdateBottomSheetInformation(
                                hasAllegionBleCredentials = hasAllegionBleCredentials,
                                hasHidOrigoMobilePass = hasHidOrigoMobilePass,
                                hidOrigoWalletPassEnabled = hidOrigoWalletPassEnabled,
                                hasBrivoWalletPass = hasBrivoWalletPass,
                                dormakabaMobilePassEnabled = dormakabaMobilePassEnabled
                            )
                        )
                    },
                    onDismissBottomSheet = {
                        onEvent(HomeUIEvent.ShouldShowBotomSheet(shouldShow = false))
                    }
                )
            }
        }
    )
}


@ThemedPreview
@Composable
fun HomePreview() {

    val passes = listOf(
        BrivoOnairPassUIModel(
            passId = "passId1",
            accountId = 1,
            accountName = "accountName",
            firstName = "firstName",
            lastName = "lastName",
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            sites = listOf(
                BrivoSiteUIModel(id = "0", siteName = "Site 1"),
                BrivoSiteUIModel(id = "1", siteName = "Site 2"),
                BrivoSiteUIModel(id = "2", siteName = "Site 3"),
                BrivoSiteUIModel(id = "3", siteName = "Site 4"),
                BrivoSiteUIModel(id = "4", siteName = "Site 5"),
                BrivoSiteUIModel(id = "5", siteName = "Site 6"),
                BrivoSiteUIModel(id = "6", siteName = "Site 7"),
                BrivoSiteUIModel(id = "7", siteName = "Site 8"),
                BrivoSiteUIModel(id = "8", siteName = "Site 9"),
            ),
            hasAllegionBleCredentials = false,
            hasHidOrigoMobilePass = false,
            hidOrigoWalletPassEnabled = false,
            hasBrivoWalletPass = false,
            dormakabaMobilePassEnabled = true
        ),
        BrivoOnairPassUIModel(
            passId = "passId2",
            accountId = 1,
            accountName = "accountName",
            firstName = "firstName",
            lastName = "lastName",
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            sites = emptyList(),
            hasAllegionBleCredentials = false,
            hasHidOrigoMobilePass = false,
            hidOrigoWalletPassEnabled = false,
            hasBrivoWalletPass = false,
            dormakabaMobilePassEnabled = false
        )
    )

    val state = HomeViewModel.HomeViewState(
        passes = passes,
        version = "BrivoSDK v1.18.0",
        alertMessage = "",
        loading = false
    )

    AppTheme {
        HomeScreenContent(
            state = state,
            onSitePressed = { _, _ -> },
            onRedeemPassPressed = { },
            onContinuousScannerPressed = { },
            onUnlockNearestAccessPointPressed = { },
            onEvent = { }
        )
    }
}

@ThemedPreview
@Composable
fun HomeEmptyPreview() {

    val state = HomeViewModel.HomeViewState(
        passes = emptyList(),
        version = "BrivoSDK v1.18.0",
        alertMessage = "",
        loading = false
    )

    AppTheme {
        HomeScreenContent(
            state = state,
            onSitePressed = { _, _ -> },
            onRedeemPassPressed = { },
            onContinuousScannerPressed = { },
            onUnlockNearestAccessPointPressed = { },
            onEvent = { }
        )
    }
}
