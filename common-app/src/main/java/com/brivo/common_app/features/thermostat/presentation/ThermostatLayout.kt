package com.brivo.common_app.features.thermostat.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brivo.common_app.R
import com.brivo.common_app.features.thermostat.model.ThermostatUiState
import com.brivo.common_app.features.thermostat.model.ThermostatUnit
import com.brivo.common_app.ui.theme.Blue100
import com.brivo.common_app.ui.theme.Gray200

@Composable
internal fun ThermostatTopBar(name: String, onBackPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.thermostat_back),
            tint = Blue100,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable { onBackPressed() }
        )
        Text(
            text = name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun ThermostatInnerContent(
    ts: ThermostatUiState,
    shouldShowTemperatureError: Boolean,
    controller: ThermostatRangeController,
    onBackPressed: () -> Unit,
    onOpenModeSheet: () -> Unit,
    onOpenFanSheet: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ThermostatTopBar(name = ts.deviceName, onBackPressed = onBackPressed)

            Spacer(modifier = Modifier.height(24.dp))

            ThermostatCenterText(ts = ts)

            Spacer(modifier = Modifier.height(32.dp))

            ThermostatRangeControlPanel(ts = ts, controller = controller)

            Spacer(modifier = Modifier.height(24.dp))

            ThermostatBottomActionBar(
                ts = ts,
                onModeClick = onOpenModeSheet,
                onFanClick = onOpenFanSheet
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        AnimatedVisibility(
            visible = shouldShowTemperatureError,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFCDAD7))
                    .padding(12.dp)
            ) {
                Text(
                    text     = stringResource(R.string.thermostat_temperature_error),
                    color    = Color(0xFF10171F),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
internal fun ThermostatCenterText(ts: ThermostatUiState) {
    val unit = if (ts.unit == ThermostatUnit.CELSIUS) "°C" else "°F"
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.thermostat_currently),
            color = Gray200,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = buildAnnotatedString {
                append("${ts.currentTemp.toInt()}")
                withStyle(
                    SpanStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        baselineShift = BaselineShift.Superscript
                    )
                ) { append(unit) }
            },
            color = Color.White,
            fontSize = 64.sp,
            lineHeight = 64.sp
        )
    }
}
