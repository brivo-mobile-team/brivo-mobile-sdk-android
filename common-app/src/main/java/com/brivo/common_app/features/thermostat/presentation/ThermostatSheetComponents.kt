package com.brivo.common_app.features.thermostat.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brivo.common_app.ui.theme.Blue100
import com.brivo.common_app.ui.theme.Gray300
import com.brivo.common_app.ui.theme.Gray700
import com.brivo.common_app.ui.theme.Gray800
import com.brivo.common_app.ui.theme.Gray900
import com.brivo.common_app.ui.theme.Gray950

@Composable
internal fun ThermostatBaseSheet(
    title: String,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Gray950)
            .padding(bottom = 18.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 86.dp, height = 8.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Gray800)
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(26.dp))

        content()

        Spacer(Modifier.height(26.dp))

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            color = Gray700,
            thickness = 1.dp
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Close",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClose() }
                .padding(10.dp),
            color = Blue100,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun SheetOptionCell(
    label: String,
    selected: Boolean,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isLoading -> Gray900
        selected -> Gray700
        else -> Color(0xFF0E1820)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Blue100,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = label.take(4).uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label.lowercase().replaceFirstChar { it.uppercase() },
            color = Gray300,
            fontSize = 14.sp
        )
    }
}

@Composable
internal fun SheetErrorBanner(visible: Boolean, message: String, modifier: Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFCDAD7))
                .padding(12.dp)
        ) {
            Text(text = message, color = Color(0xFF10171F), fontSize = 13.sp)
        }
    }
}
