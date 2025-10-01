package com.brivo.common_app.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brivo.common_app.R
import com.brivo.common_app.TestTags
import com.brivo.common_app.features.home.model.BrivoOnairPassUIModel
import com.brivo.common_app.features.home.model.PassDetailsBottomSheetUIModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PassesList(
    loading: Boolean,
    refreshing: Boolean,
    passes: List<BrivoOnairPassUIModel>,
    shouldShowBottomSheet: Boolean,
    passDetailsBottomSheetUIModel: PassDetailsBottomSheetUIModel,
    onSitePressed: (String, String) -> Unit,
    onRefresh: () -> Unit,
    onInfoButtonClicked: () -> Unit,
    onUpdateBottomSheetInformation: (Boolean, Boolean, Boolean, Boolean) -> Unit,
    onDismissBottomSheet: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (passes.isEmpty() && !loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.home_sites_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val pullToRefreshState = rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = onRefresh
        )

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .clipToBounds()
                .pullRefresh(pullToRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier.testTag(TestTags.PASSES_LIST),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(passes) { _, pass ->
                    Column {
                        MobilePassHeader(
                            pass = pass,
                            onClick = { hasAllegionBleCredentials, hasHidOrigoMobilePass, hidOrigoWalletPassEnabled, hasBrivoWalletPass ->
                                onInfoButtonClicked()
                                onUpdateBottomSheetInformation(hasAllegionBleCredentials, hasHidOrigoMobilePass, hidOrigoWalletPassEnabled, hasBrivoWalletPass)
                            }
                        )
                        MobilePassChild(
                            pass = pass,
                            onSitePressed = onSitePressed,
                            Modifier.testTag(TestTags.PASSES_LIST_ITEM)
                        )
                    }
                }
            }
            PassDetailsBottomSheet(
                shouldShowBottomSheet = shouldShowBottomSheet,
                passDetailsBottomSheetUIModel = passDetailsBottomSheetUIModel,
                sheetState = sheetState,
                onDismissRequest = onDismissBottomSheet
            )
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassDetailsBottomSheet(
    passDetailsBottomSheetUIModel: PassDetailsBottomSheetUIModel,
    shouldShowBottomSheet: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    if (shouldShowBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pass Info Details",
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
                    text = "Has Allegion BLE Credentials: ${passDetailsBottomSheetUIModel.hasAllegionBleCredentials}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Has HID-Origo Mobile Pass: ${passDetailsBottomSheetUIModel.hidOrigoWalletPassEnabled}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Has HID-Origo Wallet Enabled: ${passDetailsBottomSheetUIModel.hasHidOrigoMobilePass}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Has Brivo Wallet Pass: ${passDetailsBottomSheetUIModel.hasBrivoWalletPass}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
fun MobilePassHeader(
    pass: BrivoOnairPassUIModel,
    onClick: (Boolean, Boolean, Boolean, Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.home_pass_id, pass.passId),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(id = R.string.home_pass_account_id, pass.accountId),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(id = R.string.home_pass_account_name, pass.accountName),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(
                    id = R.string.home_pass_user_name,
                    pass.firstName,
                    pass.lastName
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }

        IconButton(
            onClick = {
                onClick(
                    pass.hasAllegionBleCredentials,
                    pass.hasHidOrigoMobilePass,
                    pass.hidOrigoWalletPassEnabled,
                    pass.hasBrivoWalletPass
                )
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Pass details",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun MobilePassChild(
    pass: BrivoOnairPassUIModel,
    onSitePressed: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pass.sites.isEmpty()) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_pass_no_sites),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            pass.sites.forEach { site ->
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .shadow(1.dp, shape = MaterialTheme.shapes.small)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { onSitePressed(pass.passId, site.id) }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        site.siteName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = site.siteName,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
