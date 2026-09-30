package com.example.stopwatchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * TASK 03 — Stopwatch App (colorful version)
 * Displays minutes:seconds.milliseconds and supports
 * Start, Pause, and Reset — with a gradient background,
 * a glowing ring around the timer, and colored action buttons.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StopwatchScreen()
            }
        }
    }
}

// Palette
private val BgTop = Color(0xFF1A1B3A)
private val BgBottom = Color(0xFF0D0E21)
private val AccentIdle = Color(0xFF7C4DFF)   // purple
private val AccentRunning = Color(0xFF00E5A0) // teal-green
private val StartColor = Color(0xFF00E676)
private val PauseColor = Color(0xFFFF5252)
private val ResetColor = Color(0xFFFFC107)

@Composable
private fun StopwatchScreen() {
    var elapsedMillis by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            var lastTick = System.currentTimeMillis()
            while (isRunning) {
                delay(16L)
                val now = System.currentTimeMillis()
                elapsedMillis += (now - lastTick)
                lastTick = now
            }
        }
    }

    val minutes = (elapsedMillis / 1000 / 60) % 60
    val seconds = (elapsedMillis / 1000) % 60
    val millis = elapsedMillis % 1000

    val ringColor by animateColorAsState(
        targetValue = if (isRunning) AccentRunning else AccentIdle,
        animationSpec = tween(400),
        label = "ringColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgBottom))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing ring around the time display
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .shadow(24.dp, CircleShape, spotColor = ringColor)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(ringColor.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset.Unspecified
                        )
                    )
                    .background(Color(0xFF15173A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.US, "%02d:%02d", minutes, seconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = String.format(Locale.US, ".%03d", millis),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = ringColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                ActionButton(
                    label = if (isRunning) "Pause" else "Start",
                    color = if (isRunning) PauseColor else StartColor,
                    onClick = { isRunning = !isRunning }
                )
                ActionButton(
                    label = "Reset",
                    color = ResetColor,
                    onClick = {
                        isRunning = false
                        elapsedMillis = 0L
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.Black),
        modifier = Modifier
            .height(56.dp)
            .shadow(8.dp, RoundedCornerShape(50), spotColor = color),
        contentPadding = PaddingValues(horizontal = 28.dp)
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}