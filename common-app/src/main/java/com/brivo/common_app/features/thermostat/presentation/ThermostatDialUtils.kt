package com.brivo.common_app.features.thermostat.presentation

import android.graphics.Paint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import com.brivo.common_app.features.thermostat.model.ThermostatMode
import com.brivo.common_app.features.thermostat.model.ThermostatUiState
import com.brivo.common_app.ui.theme.AutoHigh
import com.brivo.common_app.ui.theme.AutoLow
import com.brivo.common_app.ui.theme.CoolDark
import com.brivo.common_app.ui.theme.CoolLight
import com.brivo.common_app.ui.theme.HeatDark
import com.brivo.common_app.ui.theme.HeatLight
import com.brivo.common_app.ui.theme.OffRing
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// ── Active-thumb enum ─────────────────────────────────────────────────────────
internal enum class ActiveThumb { LOW, HIGH }

// ── Drag-interaction state ─────────────────────────────────────────────────────
@Stable
internal class DialPendingState {
    var dragging by mutableStateOf(false)
    var activeThumb by mutableStateOf(ActiveThumb.HIGH)
    var pendingLow by mutableStateOf<Float?>(null)
    var pendingHigh by mutableStateOf<Float?>(null)

    fun resetPending() {
        pendingLow = null; pendingHigh = null
    }
}

// ── Dial geometry constants ────────────────────────────────────────────────────
internal const val GAP_CENTER_DEG = 90f
internal const val DIAL_START = 125f
internal const val DIAL_SWEEP = 290f
internal const val DIAL_SCALE = 0.72f
internal const val RING_WIDTH_DP = 17
internal const val HIT_SLOP_MULT = 1.4f
internal const val TIE_EPSILON_PX = 0.5f
internal const val PILL_W = 18f
internal const val PILL_H = 88f
internal const val PILL_CORNER = 18f
internal const val LABEL_R_OFFSET = 80f
internal const val LABEL_TEXT_PX = 40f
internal const val LABEL_BASE_PX = 14f

// ── Canvas draw helpers ────────────────────────────────────────────────────────

internal fun DrawScope.drawThumb(center: Offset, radius: Float, angleDeg: Float, label: String) {
    val pos = pointOnCircle(center, radius, angleDeg)
    rotate(angleDeg + 90f, pivot = pos) {
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(pos.x - PILL_W / 2f, pos.y - PILL_H / 2f),
            size = Size(PILL_W, PILL_H),
            cornerRadius = CornerRadius(PILL_CORNER)
        )
    }
    val labelPos = pointOnCircle(center, radius + LABEL_R_OFFSET, angleDeg)
    drawIntoCanvas { c ->
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = LABEL_TEXT_PX
        }
        c.nativeCanvas.drawText(label, labelPos.x, labelPos.y + LABEL_BASE_PX, paint)
    }
}

// ── Gradient brushes ───────────────────────────────────────────────────────────

internal fun arcBrushForMode(mode: ThermostatMode): Brush = when (mode) {
    ThermostatMode.AUTO -> Brush.sweepGradient(listOf(AutoLow, AutoLow, AutoHigh, AutoHigh))
    ThermostatMode.HEAT -> Brush.sweepGradient(listOf(HeatDark, HeatLight))
    ThermostatMode.COOL -> Brush.sweepGradient(listOf(CoolDark, CoolLight))
    ThermostatMode.OFF -> Brush.sweepGradient(listOf(OffRing, OffRing))
}

// ── Pure math utilities ────────────────────────────────────────────────────────

internal fun valueToDialAngle(value: Float, ts: ThermostatUiState): Float {
    val fraction = ((value - ts.minTemp) / (ts.maxTemp - ts.minTemp)).coerceIn(0f, 1f)
    return (DIAL_START + fraction * DIAL_SWEEP) % 360f
}

internal fun angleFromCenter(center: Offset, point: Offset): Float {
    val dx = point.x - center.x
    val dy = point.y - center.y
    var deg = (atan2(dy, dx) * 180.0 / Math.PI).toFloat()
    if (deg < 0f) deg += 360f
    return deg
}

internal fun clampAngleToArc(angle: Float, start: Float, sweep: Float): Float {
    var rel = (angle - start) % 360f
    if (rel < 0f) rel += 360f
    if (rel <= sweep) return angle
    return if (rel - sweep < 360f - rel) (start + sweep) % 360f else start
}

internal fun angleToValue(angle: Float, start: Float, sweep: Float, min: Float, max: Float): Float {
    var diff = (angle - start) % 360f
    if (diff < 0f) diff += 360f
    return min + (diff / sweep).coerceIn(0f, 1f) * (max - min)
}

internal fun pointOnCircle(center: Offset, radius: Float, angleDeg: Float): Offset {
    val rad = Math.toRadians(angleDeg.toDouble())
    return Offset(center.x + radius * cos(rad).toFloat(), center.y + radius * sin(rad).toFloat())
}

internal fun isTouchNearRing(
    touch: Offset, center: Offset, radius: Float, ringW: Float, hitSlop: Float
): Boolean {
    val d = (touch - center).getDistance()
    return d in (radius - ringW / 2f - hitSlop)..(radius + ringW / 2f + hitSlop)
}

internal fun pickActiveThumb(
    mode: ThermostatMode,
    touch: Offset, center: Offset, radius: Float,
    low: Float, high: Float, minTemp: Float, maxTemp: Float,
    preferred: ActiveThumb
): ActiveThumb {
    if (mode != ThermostatMode.AUTO) return if (mode == ThermostatMode.HEAT) ActiveThumb.LOW else ActiveThumb.HIGH
    val la =
        (DIAL_START + ((low - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f) * DIAL_SWEEP) % 360f
    val ha =
        (DIAL_START + ((high - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f) * DIAL_SWEEP) % 360f
    val lp = pointOnCircle(center, radius, la)
    val hp = pointOnCircle(center, radius, ha)
    val dl = (touch - lp).getDistance()
    val dh = (touch - hp).getDistance()
    return when {
        abs(dl - dh) <= TIE_EPSILON_PX -> preferred
        dl < dh -> ActiveThumb.LOW
        else -> ActiveThumb.HIGH
    }
}
