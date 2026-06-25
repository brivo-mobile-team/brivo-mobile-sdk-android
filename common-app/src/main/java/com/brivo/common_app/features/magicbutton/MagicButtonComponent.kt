package com.brivo.common_app.features.magicbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brivo.common_app.R
import com.brivo.common_app.utils.DoorTypeIconMapper

/**
 * Magic Button with dynamic icon and device label
 */
@Composable
fun MagicButton(
    viewState: MagicButtonViewState,
    onUnlockClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MagicButtonContent(
            modifier = Modifier,
            viewState = viewState,
            onUnlockClick = onUnlockClick
        )
    }
}


@Composable
private fun MagicButtonContent(
    modifier: Modifier,
    viewState: MagicButtonViewState,
    onUnlockClick: () -> Unit
) {
    val containerColor = when (viewState.unlockStatus) {
        is UnlockStatus.Success -> MaterialTheme.colorScheme.primaryContainer
        is UnlockStatus.Error -> MaterialTheme.colorScheme.errorContainer
        is UnlockStatus.Idle -> MaterialTheme.colorScheme.primary
        is UnlockStatus.InProgress -> MaterialTheme.colorScheme.primary
    }

    val contentColor = when (viewState.unlockStatus) {
        is UnlockStatus.Success -> MaterialTheme.colorScheme.onPrimaryContainer
        is UnlockStatus.Error -> MaterialTheme.colorScheme.onErrorContainer
        is UnlockStatus.Idle -> MaterialTheme.colorScheme.onPrimary
        is UnlockStatus.InProgress -> MaterialTheme.colorScheme.onPrimary
    }

    ElevatedButton(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onUnlockClick,
        enabled = viewState.isButtonEnabled,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewState.unlockStatus == UnlockStatus.InProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = contentColor,
                    strokeWidth = 3.dp
                )
            } else {
                val iconRes = when (viewState.unlockStatus) {
                    is UnlockStatus.Success -> R.drawable.lock_open
                    is UnlockStatus.Error -> R.drawable.lock
                    is UnlockStatus.Idle -> DoorTypeIconMapper.getIconForDoorType(viewState.nearestDeviceDoorType)
                        ?: R.drawable.lock
                    is UnlockStatus.InProgress -> DoorTypeIconMapper.getIconForDoorType(viewState.nearestDeviceDoorType)
                        ?: R.drawable.lock
                }

                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = stringResource(id = R.string.unlock_door_with_magic_button_title),
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            val defaultDeviceName = stringResource(R.string.magic_button_default_device_name)
            val deviceName = viewState.nearestDeviceName.ifEmpty { defaultDeviceName }

            val buttonText = when {
                viewState.unlockStatus is UnlockStatus.Success ->
                    stringResource(R.string.magic_button_unlocked_device, deviceName)

                viewState.unlockStatus is UnlockStatus.Error ->
                    stringResource(R.string.magic_button_failed_to_unlock)

                viewState.unlockStatus is UnlockStatus.InProgress ->
                    stringResource(R.string.magic_button_unlocking_device, deviceName)

                viewState.nearestDeviceName.isEmpty() ->
                    stringResource(R.string.magic_button_no_devices)

                else ->
                    stringResource(R.string.magic_button_unlock_device, deviceName)
            }

            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
