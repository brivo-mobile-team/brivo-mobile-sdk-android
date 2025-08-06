package com.brivo.app_sdk_public.features.redeempass

import AlertMessageDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.brivo.app_sdk_public.R
import com.brivo.common_app.features.redeempass.model.RedeemPassUIEvent
import com.brivo.common_app.features.redeempass.presentation.LoadingIndicator
import com.brivo.common_app.features.redeempass.presentation.RedeemPassContent

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
