package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.ui.theme.EduBluePrimary
import com.aistudio.carrerpath.counseling.ui.theme.EduPurpleSecondary
import com.aistudio.carrerpath.counseling.ui.theme.GeoGradientStart
import com.aistudio.carrerpath.counseling.ui.theme.GeoGradientMiddle
import com.aistudio.carrerpath.counseling.ui.theme.GeoGradientEnd

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badgeText: String
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Top Courses",
                description = "Explore popular courses across Engineering, Medical, Aviation, Law, Management, and IT with duration, eligibility, fees, and career pathways.",
                icon = Icons.Default.School,
                badgeText = "POPULAR COURSES"
            ),
            OnboardingPage(
                title = "AI-Powered Student Career and Guidance",
                description = "Meet expert counsellors in your region and get personalized guidance for your education and career.",
                icon = Icons.Default.AutoAwesome,
                badgeText = "PERSONALIZED GUIDANCE"
            ),
            OnboardingPage(
                title = "Direct Counselling by Experts",
                description = "Connect directly with certified career and admission experts for 1-on-1 sessions, scholarship assistance, and college applications.",
                icon = Icons.Default.WorkspacePremium,
                badgeText = "EXPERT SESSIONS"
            )
        )
    }

    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        GeoGradientStart,
                        GeoGradientMiddle,
                        GeoGradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "CareerPath",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                TextButton(onClick = onFinish) {
                    Text(
                        text = "Skip",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Animated Center Content
            AnimatedContent(
                targetState = currentPage,
                label = "OnboardingSlide"
            ) { pageIdx ->
                val item = pages[pageIdx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    // Icon Badge
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = EduBluePrimary,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = item.badgeText,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = item.description,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Bottom Navigation Indicators & Next Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .width(if (index == currentPage) 28.dp else 10.dp)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentPage) Color.White else Color.White.copy(alpha = 0.35f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = EduBluePrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = if (currentPage == pages.size - 1) "Get Started" else "Next",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
