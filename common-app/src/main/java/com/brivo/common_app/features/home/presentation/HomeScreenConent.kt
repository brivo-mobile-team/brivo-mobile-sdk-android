package com.brivo.common_app.features.home.presentation

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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brivo.common_app.R
import com.brivo.common_app.TestTags
import com.brivo.common_app.features.home.model.BrivoOnairPassUIModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PassesList(
    loading: Boolean,
    refreshing: Boolean,
    passes: List<BrivoOnairPassUIModel>,
    onSitePressed: (String, String) -> Unit,
    onRefresh: () -> Unit
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
            onRefresh = onRefresh
        )

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .clipToBounds()
                .pullRefresh(pullToRefreshState)
        ) {
            LazyColumn(
                Modifier.testTag(TestTags.PASSES_LIST)
            ) {
                itemsIndexed(passes) { _, pass ->
                    MobilePassHeader(pass = pass)
                    MobilePassChild(
                        pass = pass,
                        onSitePressed = onSitePressed,
                        Modifier.testTag(TestTags.PASSES_LIST_ITEM)
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
    onSitePressed: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pass.sites.isEmpty()) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_pass_no_sites)
        )
    } else {
        pass.sites.forEach { site ->
            Row(
                modifier = modifier
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
