package com.brivo.common_app.features.unlockdoor.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.brivo.common_app.R
import com.brivo.common_app.features.unlockdoor.model.DoorState
import com.brivo.common_app.features.unlockdoor.model.UnlockDoorUIEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockDoorContent(
    doorState: DoorState,
    accessPointName: String,
    showSnackbar: Boolean,
    hasTrustedNetwork: Boolean,
    onEvent: (UnlockDoorUIEvent) -> Unit,
    onCheckPermissions: suspend (Boolean) -> Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (showSnackbar) {
            UnlockResultSnackbar(doorState)
            Spacer(Modifier.height(8.dp))
        }

        PulsatingLockButton(
            doorState = doorState,
            hasTrustedNetwork = hasTrustedNetwork,
            onEvent = onEvent,
            onCheckPermissions = onCheckPermissions
        )

    }
}

@Composable
fun UnlockResultSnackbar(doorState: DoorState) {
    val containerColor = when (doorState) {
        DoorState.UNLOCKED -> Color(0xFFC8E6C9)
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when (doorState) {
        DoorState.UNLOCKED -> Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    Snackbar(
        containerColor = containerColor,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = if (doorState == DoorState.UNLOCKED) painterResource(id = R.drawable.lock_open) else painterResource(id = R.drawable.lock),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (doorState == DoorState.UNLOCKED) stringResource(id = R.string.unlock_door_success) else stringResource(id = R.string.unlock_door_failed),
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun PulsatingLockButton(
    doorState: DoorState,
    nbPulsar: Int = 4,
    pulsarRadius: Float = 250f,
    pulsarColor: Color = MaterialTheme.colorScheme.primary,
    hasTrustedNetwork: Boolean,
    onEvent: (UnlockDoorUIEvent) -> Unit,
    onCheckPermissions: suspend (Boolean) -> Boolean
) {
    var fabSize by remember { mutableStateOf(IntSize(0, 0)) }
    val effects: List<Pair<Float, Float>> = List(nbPulsar) {
        pulsarBuilder(pulsarRadius = pulsarRadius, size = fabSize.width, delay = it * 500)
    }

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        if (doorState == DoorState.UNLOCKING) {
            Canvas(
                modifier = Modifier
            ) {
                for (i in 0 until nbPulsar) {
                    val (radius, alpha) = effects[i]
                    drawCircle(color = pulsarColor, radius = radius, alpha = alpha)
                }
            }
        }
        PulsatingLockButtonContent(
            modifier = Modifier
                .onGloballyPositioned {
                    if (it.isAttached) {
                        fabSize = it.size
                    }
                },
            doorState = doorState,
            hasTrustedNetwork = hasTrustedNetwork,
            onEvent = onEvent,
            onCheckPermissions = onCheckPermissions
        )
    }
}

@Composable
fun PulsatingLockButtonContent(
    modifier: Modifier,
    doorState: DoorState,
    hasTrustedNetwork: Boolean,
    onEvent: (UnlockDoorUIEvent) -> Unit,
    onCheckPermissions: suspend (Boolean) -> Boolean
) {
    val activity = LocalContext.current as FragmentActivity
    val lifecycleOwner = LocalLifecycleOwner.current

    val containerColor = when (doorState) {
        DoorState.UNLOCKED -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (doorState) {
        DoorState.UNLOCKED -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    ElevatedButton(
        modifier = modifier
            .size(250.dp),
        onClick = {
            if (doorState == DoorState.LOCKED) {
                lifecycleOwner.lifecycleScope.launch {
                    onCheckPermissions(hasTrustedNetwork)
                    onEvent(UnlockDoorUIEvent.UnlockDoor(activity = activity))
                }
            }
        },
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Icon(
            painter = painterResource(
                id = when (doorState) {
                    DoorState.UNLOCKED -> R.drawable.lock_open
                    else -> R.drawable.lock
                }
            ),
            contentDescription = stringResource(id = R.string.unlock_door_button),
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
fun pulsarBuilder(pulsarRadius: Float, size: Int, delay: Int): Pair<Float, Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val radius by infiniteTransition.animateFloat(
        initialValue = (size / 2).toFloat(),
        targetValue = size + (pulsarRadius * 2),
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000),
            initialStartOffset = StartOffset(delay),
            repeatMode = RepeatMode.Restart
        ), label = "animateRadius"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000),
            initialStartOffset = StartOffset(delay + 100),
            repeatMode = RepeatMode.Restart
        ), label = "animateAlpha"
    )

    return radius to alpha
}
