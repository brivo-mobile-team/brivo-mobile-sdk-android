package com.brivo.common_app.features.magicbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brivo.common_app.R


@Composable
fun MagicButtonDevicesList(
    magicButtonUIState: MagicButtonUIState,
    discoveredPeripherals: List<BleDeviceUIModel>,
    onUnlockButtonClick: (BleDeviceUIModel) -> Unit,
    modifier: Modifier = Modifier
) {

    //Create a list with the discovered peripherals and show them in a LazyColumn and an unlock button bellow the entire list
    if (discoveredPeripherals.isEmpty() || magicButtonUIState == MagicButtonUIState.BLE_OFF) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            val text =
                if (magicButtonUIState == MagicButtonUIState.BLE_OFF) R.string.magic_button_ble_turned_off
                else R.string.magic_button_access_points_not_found
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(discoveredPeripherals) { index, bledevice ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUnlockButtonClick(bledevice) }
                        .shadow(1.dp, shape = MaterialTheme.shapes.small)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(4.dp),
                        text = bledevice.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        modifier = Modifier
                            .padding(4.dp),
                        text = bledevice.readerUUID,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        modifier = Modifier
                            .padding(4.dp),
                        text = "RSSI: ${bledevice.rssi}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MagicButtonDevicesListPreview() {
    MaterialTheme {
        MagicButtonDevicesList(
            discoveredPeripherals = listOf(
                BleDeviceUIModel(
                    name = "Main Entrance",
                    readerUUID = "A1B2C3D4-E5F6-7890-ABCD-EF1234567890",
                    rssi = -45
                ),
                BleDeviceUIModel(
                    name = "Back Door",
                    readerUUID = "B2C3D4E5-F6A7-8901-BCDE-F12345678901",
                    rssi = -67
                ),
                BleDeviceUIModel(
                    name = "Garage Access",
                    readerUUID = "C3D4E5F6-A7B8-9012-CDEF-123456789012",
                    rssi = -52
                )
            ),
            magicButtonUIState = MagicButtonUIState.SCANNING,
            onUnlockButtonClick = { /* Preview click action */ }
        )
    }
}

// Empty state preview
@Preview(showBackground = true)
@Composable
fun MagicButtonDevicesListEmptyPreview() {
    MaterialTheme {
        MagicButtonDevicesList(
            discoveredPeripherals = emptyList(),
            magicButtonUIState = MagicButtonUIState.SCANNING,
            onUnlockButtonClick = {}
        )
    }
}

