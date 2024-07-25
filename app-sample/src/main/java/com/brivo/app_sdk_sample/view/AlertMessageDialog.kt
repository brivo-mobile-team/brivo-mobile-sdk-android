package com.brivo.app_sdk_sample.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.brivo.app_sdk_sample.R
import com.brivo.app_sdk_sample.ui.theme.AppTheme

@Composable
fun AlertMessageDialog(
    message: String,
    onDialogClosed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDialogClosed() },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = { onDialogClosed() }
            ) {
                Text(stringResource(id = R.string.alert_dialog_button_ok))
            }
        }
    )
}

@ThemedPreview
@Composable
fun AlertMessageDialogPreview() {
    AppTheme {
        AlertMessageDialog(
            message = "This is a test alert message",
            onDialogClosed = { }
        )
    }
}