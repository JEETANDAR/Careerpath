package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aistudio.carrerpath.counseling.R
import com.aistudio.carrerpath.counseling.ui.theme.*

@Composable
fun PostLoginAssessmentPromptDialog(
    studentName: String = "Student",
    currentLanguage: String = "en",
    onStartTest: () -> Unit,
    onDismiss: () -> Unit
) {
    val isKn = currentLanguage == "kn"

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_bounce")
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(16.dp),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Banner with Vidyabot Mascot
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                            )
                        )
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(72.dp)
                                .scale(bounceScale)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                VidyabotAvatar(size = 64.dp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isKn) "ಸೈಕೋಮೆಟ್ರಿಕ್ ಪರೀಕ್ಷೆ ತೆಗೆದುಕೊಳ್ಳಿ" else "Take a psychometric test",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isKn)
                            "ನಮಸ್ಕಾರ $studentName! ನಾನು ವಿದ್ಯಾಬಾಟ್ 🌟. ನಿಮ್ಮ ವ್ಯಕ್ತಿತ್ವ, ಸಾಮರ್ಥ್ಯ ಮತ್ತು ಆಸಕ್ತಿಗೆ ಹೊಂದಿಕೆಯಾಗುವ ಆದರ್ಶ ಕಾಲೇಜು ಮತ್ತು ವೃತ್ತಿ ಆಯ್ಕೆ ಮಾಡಲು ಈ ಸೈಕೋಮೆಟ್ರಿಕ್ ಪರೀಕ್ಷೆಯನ್ನು ತೆಗೆದುಕೊಳ್ಳಿ."
                        else
                            "Hello $studentName! I am Vidyabot 🌟. Discover your personality archetype, top matching careers, and best college courses with our psychometric assessment.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isKn) "30 ಪ್ರಶ್ನೆಗಳು • ತ್ವರಿತ ಫಲಿತಾಂಶ ವರದಿ" else "30 Quick Ratings • Instant Career Matches",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onStartTest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isKn) "ಈಗ ಪರೀಕ್ಷೆ ಪ್ರಾರಂಭಿಸಿ" else "Take a psychometric test",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = if (isKn) "ನಂತರ ನೆನಪಿಸಿ" else "Remind Me Later",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
