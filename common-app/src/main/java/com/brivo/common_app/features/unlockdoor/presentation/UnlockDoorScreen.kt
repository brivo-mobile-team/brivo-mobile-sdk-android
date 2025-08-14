package com.brivo.common_app.features.unlockdoor.presentation

import android.widget.Toast
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
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
    onCheckPermissions: suspend (hasTrustedNetwork: Boolean) -> Boolean
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(8.dp),
                title = {
                    Text(
                        textAlign = TextAlign.Center,
                        text = if (accessPointName.isNotEmpty())
                            stringResource(id = R.string.unlock_door_title, accessPointName)
                        else stringResource(id = R.string.unlock_door_with_magic_button_title)
                    )
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                contentAlignment = Alignment.Center
            ) {
                PulsatingLockButton(
                    doorState = doorState,
                    hasTrustedNetwork = hasTrustedNetwork,
                    onEvent = onEvent,
                    onCheckPermissions = onCheckPermissions
                )
            }
        },
        snackbarHost = {
            if (showSnackbar) {
                UnlockResultSnackbar(doorState)
            }
        }
    )
}

@Composable
fun UnlockResultSnackbar(doorState: DoorState) {
    Snackbar(
        containerColor = if (doorState == DoorState.UNLOCKED) Color.Green else Color.Red
    ) {
        Row {
            Image(
                modifier = Modifier.size(16.dp),
                painter =
                    if (doorState == DoorState.UNLOCKED) painterResource(id = R.drawable.lock_open)
                    else painterResource(id = R.drawable.lock),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text =
                    if (doorState == DoorState.UNLOCKED) stringResource(id = R.string.unlock_door_success)
                    else stringResource(id = R.string.unlock_door_failed),
                color = Color.Black
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
    onCheckPermissions: suspend (hasTrustedNetwork: Boolean) -> Boolean
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
                modifier = Modifier,
                onDraw = {
                    for (i in 0 until nbPulsar) {
                        val (radius, alpha) = effects[i]
                        drawCircle(color = pulsarColor, radius = radius, alpha = alpha)
                    }
                }
            )
        }
        PulsatingLockButtonContent(
            modifier = Modifier
                .padding((pulsarRadius * 2).dp)
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
    onCheckPermissions: suspend (hasTrustedNetwork: Boolean) -> Boolean
) {

    val activity = LocalContext.current as FragmentActivity

    val lifecycleOwner = LocalLifecycleOwner.current

    IconButton(
        modifier = Modifier.size(250.dp),
        onClick = {
            if (doorState == DoorState.LOCKED) {
                lifecycleOwner.lifecycleScope.launch {
                    onCheckPermissions(hasTrustedNetwork)
                    onEvent(UnlockDoorUIEvent.UnlockDoor(activity = activity))
                }
            }
        }
    ) {
        Image(
            painter = painterResource(
                id = when (doorState) {
                    DoorState.UNLOCKED -> R.drawable.lock_open
                    else -> R.drawable.lock
                }
            ),
            colorFilter = ColorFilter.tint(
                color = when (doorState) {
                    DoorState.UNLOCKED -> Color.Green
                    else -> Color.Red
                },
                blendMode = BlendMode.SrcAtop
            ),
            contentDescription = stringResource(id = R.string.unlock_door_button)
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
