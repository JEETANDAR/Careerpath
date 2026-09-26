package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.R
import com.aistudio.carrerpath.counseling.data.model.PsychometricAssessmentResult
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CareerProfileAssessmentSheet(
    result: PsychometricAssessmentResult,
    studentName: String = "Student",
    currentLanguage: String = "en",
    onDismiss: () -> Unit,
    onRetakeTest: () -> Unit,
    onBookGuidance: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToCareers: () -> Unit,
    onChatWithMoji: (initialPrompt: String) -> Unit = {}
) {
    val isKn = currentLanguage == "kn"
    var isVidyabotVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        isVidyabotVisible = true
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Top Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                        )
                    )
                    .padding(22.dp)
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
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = result.tag,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isKn) "$studentName ರವರ ಸೈಕೋಮೆಟ್ರಿಕ್ ವೃತ್ತಿ ವಿಶ್ಲೇಷಣೆ" else "$studentName's Psychometric Career Assessment",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = result.archetype,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 27.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = result.archetypeDescription,
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {

                // Vidyabot Interactive Assistant Card (Animated Pop-in from Right)
                AnimatedVisibility(
                    visible = isVidyabotVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth + 400 },
                        animationSpec = androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                        )
                    ) + scaleIn(
                        initialScale = 0.7f,
                        animationSpec = androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
                        )
                    ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(400))
                ) {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, EduBluePrimary.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Vidyabot Mascot Character Avatar
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 6.dp,
                                    modifier = Modifier.size(74.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        VidyabotAvatar(size = 68.dp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = EduBluePrimary,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "AI CAREER BOT • VIDYABOT",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isKn) "ನಮಸ್ಕಾರ, ನಾನು ವಿದ್ಯಾಬಾಟ್! 👋" else "Hi, I am Vidyabot! 👋",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isKn)
                                            "ಅಭಿನಂದನೆಗಳು! ನಿಮ್ಮ ಪರೀಕ್ಷಾ ಫಲಿತಾಂಶಕ್ಕೆ ತಕ್ಕಂತೆ ಸೂಕ್ತ ಕಾಲೇಜು ಮತ್ತು ವೃತ್ತಿ ಆಯ್ಕೆ ಮಾಡಲು ನಾನು ಇಲ್ಲಿದ್ದೇನೆ. ನೀವು ಯಾವ ವೃತ್ತಿಯನ್ನು ಹುಡುಕುತ್ತಿದ್ದೀರಿ?"
                                        else
                                            "Congratulations! I'm here to help you explore your best career matches, college cutoffs, and next steps! What are you looking for?",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Quick Action Buttons with Vidyabot
                            Text(
                                text = if (isKn) "ವಿದ್ಯಾಬಾಟ್ ಅವರೊಂದಿಗೆ ತ್ವರಿತವಾಗಿ ಚರ್ಚಿಸಿ:" else "Quick Ask Vidyabot:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val topCareer = result.recommendedCareers.firstOrNull() ?: "Engineering"
                            val topCourse = result.recommendedCourses.firstOrNull() ?: "Medicine"

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        onDismiss()
                                        onChatWithMoji("Hi Vidyabot, my psychometric archetype is ${result.archetype}. Can you explain top career opportunities and starting salaries for $topCareer?")
                                    },
                                    label = { Text("💼 Tell me about $topCareer", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = EduBlueContainer)
                                )

                                SuggestionChip(
                                    onClick = {
                                        onDismiss()
                                        onChatWithMoji("Hi Vidyabot, what are the top colleges, cutoffs, and fees for $topCourse in India?")
                                    },
                                    label = { Text("🎓 Colleges for $topCourse", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = OpenGreenContainer)
                                )

                                SuggestionChip(
                                    onClick = {
                                        onDismiss()
                                        onChatWithMoji("Hi Vidyabot! Based on my psychometric test score, what are the best scholarships and entrance exams for me?")
                                    },
                                    label = { Text("✨ Scholarships & Exams", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    onDismiss()
                                    onChatWithMoji("Hi Vidyabot! Help me understand my psychometric ${result.archetype} profile and roadmap.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isKn) "ವಿದ್ಯಾಬಾಟ್ ಅವರೊಂದಿಗೆ ಪೂರ್ಣ ಚಾಟ್ ಪ್ರಾರಂಭಿಸಿ" else "Chat with Vidyabot (AI Career Guide)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // RIASEC Personality Dimension Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isKn) "RIASEC ವ್ಯಕ್ತಿತ್ವ ಅಂಕಗಳು (Personality Scores)" else "Your Occupational Personality Scores",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Score / 120",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        result.dimensionScores.forEach { (dim, score) ->
                            val color = when {
                                dim.contains("Realistic") -> Color(0xFFE65100)
                                dim.contains("Investigative") -> Color(0xFF1E88E5)
                                dim.contains("Artistic") -> Color(0xFF8E24AA)
                                dim.contains("Social") -> Color(0xFF43A047)
                                dim.contains("Enterprising") -> Color(0xFFD81B60)
                                else -> Color(0xFF00897B)
                            }

                            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = color,
                                            shape = CircleShape,
                                            modifier = Modifier.size(10.dp)
                                        ) {}
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = dim,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "$score%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                LinearProgressIndicator(
                                    progress = { score / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = color,
                                    trackColor = color.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Core Strengths
                Text(
                    text = if (isKn) "ನಿಮ್ಮ ಪ್ರಮುಖ ಸಾಮರ್ಥ್ಯಗಳು" else "Your Core Strengths",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    result.topStrengths.forEach { strength ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strength,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Good College Majors / Courses Matching Page 3 of PDF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isKn) "ಸೂಕ್ತ ಕಾಲೇಜು ಕೋರ್ಸ್‌ಗಳು (Good College Majors)" else "Good College Majors For You",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        onDismiss()
                        onNavigateToCourses()
                    }) {
                        Text(if (isKn) "ಎಲ್ಲಾ ಕೋರ್ಸ್‌ಗಳು" else "Explore Courses", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EduBluePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                result.recommendedCourses.forEach { courseName ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                onDismiss()
                                onNavigateToCourses()
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = EduPurpleSecondary, modifier = Modifier.size(17.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = courseName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Suitable Career Pathways Matching Page 3 of PDF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isKn) "ಸೂಕ್ತ ವೃತ್ತಿ ಆಯ್ಕೆಗಳು (Career Pathways)" else "Suitable Career Pathways",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        onDismiss()
                        onNavigateToCareers()
                    }) {
                        Text(if (isKn) "ಎಲ್ಲಾ ವೃತ್ತಿಗಳು" else "Explore Careers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EduBluePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                result.recommendedCareers.forEach { careerName ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                onDismiss()
                                onNavigateToCareers()
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Work, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(17.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = careerName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Button(
                    onClick = {
                        onDismiss()
                        onBookGuidance()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary)
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isKn) "ತಜ್ಞರೊಂದಿಗೆ ನೇರ ಕೌನ್ಸೆಲಿಂಗ್ ಬುಕ್ ಮಾಡಿ" else "Book 1-on-1 Expert Guidance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetakeTest,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isKn) "ಮರು ಪರೀಕ್ಷೆ" else "Retake Test", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(if (isKn) "ಮುಗಿದಿದೆ" else "Done", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
