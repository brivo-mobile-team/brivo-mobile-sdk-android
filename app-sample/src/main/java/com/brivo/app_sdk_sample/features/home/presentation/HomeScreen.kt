package com.brivo.app_sdk_sample.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.brivo.app_sdk_sample.R
import com.brivo.app_sdk_sample.features.home.model.BrivoOnairPassUIModel
import com.brivo.app_sdk_sample.features.home.model.BrivoSiteUIModel
import com.brivo.app_sdk_sample.features.home.model.HomeUIEvent
import com.brivo.app_sdk_sample.ui.theme.AppTheme
import com.brivo.app_sdk_sample.view.AlertMessageDialog
import com.brivo.app_sdk_sample.view.ComposableLifecycle
import com.brivo.app_sdk_sample.view.ThemedPreview

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRedeemPassPressed: () -> Unit,
    onSitePressed: (String, Int) -> Unit,
    onMagicButtonPressed: () -> Unit,
) {

    ComposableLifecycle { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(HomeUIEvent.LoadPasses)
        }
    }

    val state by viewModel.state.collectAsState()
    HomeScreenContent(
        state = state,
        onRedeemPassPressed = onRedeemPassPressed,
        onSitePressed = onSitePressed,
        onMagicButtonPressed = onMagicButtonPressed,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeViewModel.HomeViewState,
    onRedeemPassPressed: () -> Unit,
    onSitePressed: (String, Int) -> Unit,
    onMagicButtonPressed: () -> Unit,
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
                        Icon(Icons.Filled.Add, stringResource(id = R.string.redeem_pass_title))
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
                            .clickable { onMagicButtonPressed() }
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(12.dp),
                        text = stringResource(id = R.string.home_magic_button),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                PassesList(
                    passes = state.passes,
                    loading = state.loading,
                    refreshing = state.refreshing,
                    onSitePressed = onSitePressed,
                    onEvent = onEvent
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PassesList(
    loading: Boolean,
    refreshing: Boolean,
    passes: List<BrivoOnairPassUIModel>,
    onSitePressed: (String, Int)  -> Unit,
    onEvent: (HomeUIEvent) -> Unit
) {

    if (passes.isEmpty() && !loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.home_sites_empty)
            )
        }
    } else {

        val pullToRefreshState = rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = { onEvent(HomeUIEvent.RefreshPasses) }
        )

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .clipToBounds()
                .pullRefresh(pullToRefreshState)
        ) {
            LazyColumn {
                itemsIndexed(passes) { _, pass ->
                    MobilePassHeader(pass = pass)
                    MobilePassChild(
                        pass = pass,
                        onSitePressed = onSitePressed
                    )
                }
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun MobilePassHeader(
    pass: BrivoOnairPassUIModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(8.dp)
    ) {
        Text(
            fontSize = 12.sp,
            text = stringResource(id = R.string.home_pass_id, pass.passId),
            color = MaterialTheme.colorScheme.onSecondary,
            softWrap = false
        )
        Text(
            fontSize = 12.sp,
            text = stringResource(id = R.string.home_pass_account_id, pass.accountId),
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            fontSize = 12.sp,
            text = stringResource(id = R.string.home_pass_account_name, pass.accountName),
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            fontSize = 12.sp,
            text = stringResource(id = R.string.home_pass_user_name, pass.firstName, pass.lastName),
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun MobilePassChild(
    pass: BrivoOnairPassUIModel,
    onSitePressed: (String, Int) -> Unit,
) {
    if (pass.sites.isEmpty()) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_pass_no_sites)
        )
    } else {
        pass.sites.forEach { site ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSitePressed(pass.passId, site.id) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(site.siteName)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = site.siteName
                )
            }
        }
    }
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
                BrivoSiteUIModel(id = 0, siteName = "Site 1"),
                BrivoSiteUIModel(id = 1, siteName = "Site 2"),
                BrivoSiteUIModel(id = 2, siteName = "Site 3"),
                BrivoSiteUIModel(id = 3, siteName = "Site 4"),
                BrivoSiteUIModel(id = 4, siteName = "Site 5"),
                BrivoSiteUIModel(id = 5, siteName = "Site 6"),
                BrivoSiteUIModel(id = 6, siteName = "Site 7"),
                BrivoSiteUIModel(id = 7, siteName = "Site 8"),
                BrivoSiteUIModel(id = 8, siteName = "Site 9"),
            )
        ),
        BrivoOnairPassUIModel(
            passId = "passId2",
            accountId = 1,
            accountName = "accountName",
            firstName = "firstName",
            lastName = "lastName",
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            sites = emptyList()
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
            onMagicButtonPressed = { },
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
            onMagicButtonPressed = { },
            onEvent = { }
        )
    }
}