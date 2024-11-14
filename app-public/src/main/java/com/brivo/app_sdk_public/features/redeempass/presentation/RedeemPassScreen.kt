package com.brivo.app_sdk_public.features.redeempass.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.app_sdk_public.R
import com.brivo.app_sdk_public.features.redeempass.model.RedeemPassUIEvent
import com.brivo.app_sdk_public.ui.theme.AppTheme
import com.brivo.app_sdk_public.view.AlertMessageDialog
import com.brivo.app_sdk_public.view.ThemedPreview

@Composable
fun RedeemPassScreen(
    viewModel: RedeemPassViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        RedeemPassContent(
            onEvent = viewModel::onEvent
        )

        if (state.isRedeemingPass) {
            LoadingIndicator()
        }

        if (state.mobilePassRedeemed) {
            AlertMessageDialog(
                message = stringResource(id = R.string.redeem_pass_success),
                onDialogClosed = { onBackPressed() }
            )
        }

        if (state.alertMessage.isNotEmpty()) {
            AlertMessageDialog(
                message = state.alertMessage,
                onDialogClosed = { viewModel.onEvent(RedeemPassUIEvent.UpdateAlertMessage("")) }
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemPassContent(
    onEvent: (RedeemPassUIEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.redeem_pass)) }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RedeemPassForm(
                    onEvent = onEvent
                )
            }
        }
    )
}

@Composable
fun RedeemPassForm(
    onEvent: (RedeemPassUIEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        var emailValue by remember { mutableStateOf(TextFieldValue("")) }
        var tokenValue by remember { mutableStateOf(TextFieldValue("")) }
        var isRegionUS by remember { mutableStateOf(true) }

        Switch(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            checked = isRegionUS,
            onCheckedChange = { newValue ->
                isRegionUS = newValue
                onEvent(RedeemPassUIEvent.UpdateRegion(newValue))
            }
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp),
            text = stringResource(id = R.string.redeem_pass_region)
        )
        TextField(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp),
            value = emailValue,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(id = R.string.redeem_pass_email)
                )
            },
            onValueChange = { newValue ->
                emailValue = newValue
                onEvent(RedeemPassUIEvent.UpdateEmail(newValue))
            }
        )
        TextField(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp),
            value = tokenValue,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(id = R.string.redeem_pass_access_code)
                )
            },
            onValueChange =  { newValue ->
                tokenValue = newValue
                onEvent(RedeemPassUIEvent.UpdateToken(newValue))
            }
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onEvent(RedeemPassUIEvent.RedeemPass) },
            enabled = emailValue.text.isNotEmpty() && tokenValue.text.isNotEmpty()
        ) {
            Text(
                text = stringResource(id = R.string.redeem_pass_submit)
            )
        }
    }
}

@ThemedPreview
@Composable
fun RedeemPassPreview() {
    AppTheme {
        RedeemPassContent(
            onEvent = { }
        )
    }
}