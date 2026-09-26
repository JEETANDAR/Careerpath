package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.R
import kotlinx.coroutines.delay

// Vidyabot Character Colors matching AI companion theme
val VidyabotCyanGlow = Color(0xFF00E5FF)
val VidyabotBlueAccent = Color(0xFF0091EA)
val VidyabotGreenBubble = Color(0xFF10B981) // Vibrant green speech bubble

// Backward-compatibility color aliases
val MojiCyanGlow = VidyabotCyanGlow
val MojiBlueAccent = VidyabotBlueAccent
val MojiGreenBubble = VidyabotGreenBubble

/**
 * Avatar displaying the Vidyabot AI Bot
 */
@Composable
fun VidyabotAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 68.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .testTag("vidyabot_avatar"),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_vidyabot_avatar),
            contentDescription = "Vidyabot AI Bot Mascot",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

/**
 * Backward compatibility alias for MojiRobotAvatar
 */
@Composable
fun MojiRobotAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 68.dp
) {
    VidyabotAvatar(modifier = modifier, size = size)
}

/**
 * Animated Floating Mascot Widget:
 * - Slides in from right to left with a bouncy spring animation
 * - Pop up scale & fade entry
 * - Continuous smooth floating bobbing animation (up & down)
 * - Glowing cyan aura matching Vidyabot companion
 * - Vibrant green speech bubble ("Hey! I am Vidyabot, how can I help? 👋")
 * - Clicking Vidyabot opens the AI Career & College Companion!
 */
@Composable
fun VidyabotFloatingCompanion(
    isKannada: Boolean = false,
    onOpenChatbot: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    var currentSpeechIndex by remember { mutableStateOf(0) }

    // Multi-message speech bubble cycling
    val speechMessages = if (isKannada) {
        listOf(
            "ನಮಸ್ಕಾರ! ನಾನು ವಿದ್ಯಾಬಾಟ್ 👋",
            "ಕಾಲೇಜು & ಕಟ್‌ಆಫ್ ಮಾಹಿತಿ ಕೇಳಿ 🎓",
            "ವೃತ್ತಿ & ಸ್ಕಾಲರ್‌ಶಿಪ್ ಮಾರ್ಗದರ್ಶನ 🌟",
            "ಸೈಕೋಮೆಟ್ರಿಕ್ ಪರೀಕ್ಷೆ ಫಲಿತಾಂಶ ಚರ್ಚಿಸಿ 💡"
        )
    } else {
        listOf(
            "Hey! I am Vidyabot 👋",
            "Ask about college cutoffs & fees! 🎓",
            "Psychometric & career guidance 🌟",
            "Explore top scholarships & exams 💡"
        )
    }

    // Trigger entry slide-in on load
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    // Auto cycle friendly greeting messages every 4.5 seconds
    LaunchedEffect(isKannada) {
        while (true) {
            delay(4500)
            currentSpeechIndex = (currentSpeechIndex + 1) % speechMessages.size
        }
    }

    // Gentle idle floating bobbing animation
    val infiniteTransition = rememberInfiniteTransition(label = "vidyabot_bobbing")
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing_y"
    )

    // Gentle pulse for speech bubble & glowing ring
    val bubblePulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Animated Slide In from Right + Pop Up Scale
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth + 400 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + scaleIn(
            initialScale = 0.6f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(500)),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth + 400 },
            animationSpec = tween(300)
        ) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(0, bobbingOffset.dp.roundToPx()) }
                .padding(end = 12.dp, bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            onOpenChatbot()
                        }
                    )
                    .testTag("vidyabot_companion_widget")
            ) {
                // 1. Green Speech Bubble
                val mascotGradient = remember {
                    Brush.sweepGradient(
                        listOf(
                            VidyabotCyanGlow,
                            VidyabotBlueAccent,
                            Color.White,
                            VidyabotCyanGlow
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 4.dp
                    ),
                    color = VidyabotGreenBubble,
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .padding(bottom = 22.dp, end = 6.dp)
                        .graphicsLayer {
                            scaleX = bubblePulseScale
                            scaleY = bubblePulseScale
                        }
                        .widthIn(max = 200.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(
                            targetState = speechMessages[currentSpeechIndex],
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(300)) + slideInVertically { it / 2 }) togetherWith
                                        (fadeOut(animationSpec = tween(200)) + slideOutVertically { -it / 2 })
                            },
                            label = "speech_text_transition"
                        ) { msg ->
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // 2. Vidyabot Robot Mascot with Cyan Glow & Active Status
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(10.dp, shape = CircleShape, ambientColor = VidyabotCyanGlow.copy(alpha = 0.5f))
                        .background(
                            mascotGradient,
                            CircleShape
                        )
                        .graphicsLayer {
                            alpha = glowAlpha
                        }
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    VidyabotAvatar(size = 70.dp)

                    // Active online green indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Backward compatibility alias for MojiFloatingCompanion
 */
@Composable
fun MojiFloatingCompanion(
    isKannada: Boolean = false,
    onOpenChatbot: () -> Unit,
    modifier: Modifier = Modifier
) {
    VidyabotFloatingCompanion(
        isKannada = isKannada,
        onOpenChatbot = onOpenChatbot,
        modifier = modifier
    )
}
