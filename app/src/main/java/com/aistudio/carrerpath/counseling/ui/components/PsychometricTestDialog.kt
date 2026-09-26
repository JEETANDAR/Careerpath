package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aistudio.carrerpath.counseling.data.repository.PsychometricTestEngine
import com.aistudio.carrerpath.counseling.ui.theme.*

@Composable
fun PsychometricTestDialog(
    currentLanguage: String = "en",
    onDismiss: () -> Unit,
    onComplete: (Map<Int, Int>) -> Unit
) {
    val isKn = currentLanguage == "kn"
    val questions = remember { PsychometricTestEngine.questions }
    val options = remember(isKn) {
        if (isKn) PsychometricTestEngine.scaleOptionsKn else PsychometricTestEngine.scaleOptions
    }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }

    val currentQ = questions[currentQuestionIndex]
    val selectedScore = userAnswers[currentQ.id]

    // Category banner colors and badges
    val categoryColor = when (currentQ.category) {
        "Realistic" -> Color(0xFFE65100)
        "Investigative" -> Color(0xFF1E88E5)
        "Artistic" -> Color(0xFF8E24AA)
        "Social" -> Color(0xFF43A047)
        "Enterprising" -> Color(0xFFD81B60)
        else -> Color(0xFF00897B) // Conventional
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isKn) "ಸೈಕೋಮೆಟ್ರಿಕ್ ಪರೀಕ್ಷೆ" else "Take a psychometric test",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isKn) "ಪ್ರಶ್ನೆ ${currentQuestionIndex + 1} / ${questions.size}" else "Question ${currentQuestionIndex + 1} of ${questions.size}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${((currentQuestionIndex + 1) * 100) / questions.size}% Done",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }

                // Instructions strip
                Surface(
                    color = categoryColor.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = categoryColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentQ.code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = categoryColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentQ.category} Dimension (${currentQ.code})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (isKn) "ಯಾವುದೇ ಸರಿ/ತಪ್ಪು ಉತ್ತರವಿಲ್ಲ" else "No right or wrong answer",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Question Activity & Options
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isKn) "ಈ ಚಟುವಟಿಕೆಯನ್ನು ನೀವು ಎಷ್ಟು ಇಷ್ಟಪಡುತ್ತೀರಿ?" else "How much do you like this activity?",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isKn && currentQ.activityKn.isNotBlank()) currentQ.activityKn else currentQ.activity,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 22.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isKn && currentQ.activityKn.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQ.activity,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = if (isKn) "ನಿಮ್ಮ ಇಷ್ಟದ ಮಟ್ಟವನ್ನು ಆಯ್ಕೆಮಾಡಿ (0 ರಿಂದ 4):" else "Select your degree of liking (0 to 4):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 5 Rating Options (0 - Dislike, 1 - Slightly Dislike, 2 - Neutral, 3 - Slightly Enjoy, 4 - Enjoy)
                        options.forEach { opt ->
                            val isSelected = selectedScore == opt.score
                            val optionHighlightColor = when (opt.score) {
                                0 -> Color(0xFFE53935)
                                1 -> Color(0xFFFB8C00)
                                2 -> Color(0xFF757575)
                                3 -> Color(0xFF43A047)
                                else -> Color(0xFF1E88E5)
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        userAnswers[currentQ.id] = opt.score
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) optionHighlightColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) optionHighlightColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = if (isSelected) optionHighlightColor else MaterialTheme.colorScheme.surface,
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp),
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (isSelected) optionHighlightColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = opt.score.toString(),
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = opt.text,
                                            color = if (isSelected) optionHighlightColor else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = optionHighlightColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer Actions
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentQuestionIndex > 0) {
                            OutlinedButton(
                                onClick = {
                                    if (currentQuestionIndex > 0) {
                                        currentQuestionIndex--
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isKn) "ಹಿಂದೆ" else "Previous")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = {
                                if (selectedScore != null) {
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex++
                                    } else {
                                        onComplete(userAnswers.toMap())
                                    }
                                }
                            },
                            enabled = selectedScore != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EduBluePrimary
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = if (currentQuestionIndex == questions.size - 1) {
                                    if (isKn) "ಫಲಿತಾಂಶ ನೋಡಿ" else "View Test Results"
                                } else {
                                    if (isKn) "ಮುಂದೆ" else "Next (${currentQuestionIndex + 2}/${questions.size})"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
