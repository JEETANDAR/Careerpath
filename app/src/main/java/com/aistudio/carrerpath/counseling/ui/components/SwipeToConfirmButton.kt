package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.ui.theme.EduBluePrimary
import com.aistudio.carrerpath.counseling.ui.theme.EduPurpleSecondary
import kotlin.math.roundToInt

@Composable
fun SwipeToConfirmButton(
    text: String = "Swipe to Book Counselling",
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isConfirmed by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isConfirmed) {
        if (isConfirmed) {
            kotlinx.coroutines.delay(1200)
            isConfirmed = false
            offsetX = 0f
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    colors = if (isConfirmed) listOf(Color(0xFF10B981), Color(0xFF059669))
                    else listOf(EduBluePrimary, EduPurpleSecondary)
                )
            )
            .clickable {
                if (!isConfirmed) {
                    isConfirmed = true
                    onConfirmed()
                }
            }
            .padding(4.dp)
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val thumbSizePx = with(LocalDensity.current) { 48.dp.toPx() }
        val maxOffset = (widthPx - thumbSizePx - 8f).coerceAtLeast(1f)

        val draggableState = rememberDraggableState { delta ->
            if (!isConfirmed) {
                offsetX = (offsetX + delta).coerceIn(0f, maxOffset)
                if (offsetX >= maxOffset * 0.65f) {
                    isConfirmed = true
                    offsetX = maxOffset
                    onConfirmed()
                }
            }
        }

        // Center Text Prompt
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val progress = (offsetX / maxOffset).coerceIn(0f, 1f)
            AnimatedVisibility(
                visible = !isConfirmed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = text,
                    color = Color.White.copy(alpha = (1f - progress).coerceIn(0.2f, 1f)),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            AnimatedVisibility(
                visible = isConfirmed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Opening Checkout Form...",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Draggable Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (isConfirmed || offsetX >= maxOffset * 0.65f) {
                            isConfirmed = true
                            offsetX = maxOffset
                            onConfirmed()
                        } else {
                            offsetX = 0f
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isConfirmed) Icons.Default.CheckCircle else Icons.Default.ArrowForward,
                contentDescription = null,
                tint = if (isConfirmed) Color(0xFF10B981) else EduBluePrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
