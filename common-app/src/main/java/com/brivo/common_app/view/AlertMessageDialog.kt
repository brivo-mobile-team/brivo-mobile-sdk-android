
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.brivo.common_app.R

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

//@ThemedPreview
//@Composable
//fun AlertMessageDialogPreview() {
//    AppTheme {
//        AlertMessageDialog(
//            message = "This is a test alert message",
//            onDialogClosed = { }
//        )
//    }
//}
