package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aistudio.carrerpath.counseling.data.model.CourseEntity
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailSheet(
    course: CourseEntity,
    currentLanguage: String = "en",
    onDismiss: () -> Unit,
    onBookCounsellingSwiped: (CourseEntity) -> Unit
) {
    val isKn = currentLanguage == "kn"
    val cName = if (isKn && course.nameKn.isNotBlank()) course.nameKn else course.name
    val cDesc = if (isKn && course.descriptionKn.isNotBlank()) course.descriptionKn else course.description
    val cElig = if (isKn && course.eligibilityKn.isNotBlank()) course.eligibilityKn else course.eligibility
    val cCareer = if (isKn && course.careerScopeKn.isNotBlank()) course.careerScopeKn else course.careerScope
    val cWhyChoose = if (isKn && course.whyChooseKn.isNotBlank()) course.whyChooseKn else course.whyChoose
    val cExams = if (isKn && course.examsKn.isNotBlank()) course.examsKn else course.exams
    val cSubjects = if (isKn && course.subjectsKn.isNotBlank()) course.subjectsKn else course.subjects
    val cPathway = if (isKn && course.pathwayKn.isNotBlank()) course.pathwayKn else course.pathway
    val cRecruiters = if (isKn && course.topRecruitersKn.isNotBlank()) course.topRecruitersKn else course.topRecruiters
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
                .padding(bottom = 32.dp)
        ) {
            // Header Banner with Dynamic Animated Category Visuals
            CourseCategoryAnimationHeader(
                category = course.category,
                courseName = cName,
                duration = course.durationYears,
                fees = course.avgFeesLakhs,
                isKn = isKn,
                onDismiss = onDismiss
            )

            if (course.imageUrl.isNotBlank()) {
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF334155))
                            )
                        )
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(course.imageUrl)
                            .crossfade(300)
                            .build(),
                        contentDescription = cName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Overview
                Text(
                    text = if (isKn) "ಕೋರ್ಸ್ ಅವಲೋಕನ" else "Course Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = cDesc,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Eligibility Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = EduBlueContainer.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, EduBluePrimary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EduBluePrimary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isKn) "ಅರ್ಹತೆಯ ಮಾನದಂಡ" else "ELIGIBILITY CRITERIA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduBluePrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cElig,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Key Benefits & Career Scope
                Text(
                    text = if (isKn) "ವೃತ್ತಿ ಅವಕಾಶಗಳು ಮತ್ತು ಪ್ರಯೋಜನಗಳು" else "Career Opportunities & Benefits",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                val benefitsList = if (isKn) listOf(
                    "ಹೆಚ್ಚಿನ ಬೇಡಿಕೆಯ ಉದ್ಯೋಗ ಕ್ಷೇತ್ರ: $cCareer",
                    "ಅಂದಾಜು ಆರಂಭಿಕ ವೇತನ: ₹${course.avgPackageLakhs} LPA - ₹${course.avgPackageLakhs * 1.8} LPA",
                    "ಉನ್ನತ ಶಿಕ್ಷಣ ಮತ್ತು ಜಾಗತಿಕ ಲೈಸೆನ್ಸಿಂಗ್‌ಗೆ ನೇರ ಅರ್ಹತೆ",
                    "${course.topCollegesCount}+ ಕ್ಕೂ ಹೆಚ್ಚು ಉನ್ನತ ಎನ್‌ಐಆರ್‌ಎಫ್ ಶ್ರೇಯಾಂಕದ ಕಾಲೇಜುಗಳು"
                ) else listOf(
                    "High Demand Industry Scope: $cCareer",
                    "Estimated Starting Package: ₹${course.avgPackageLakhs} LPA - ₹${course.avgPackageLakhs * 1.8} LPA",
                    "Direct eligibility for Higher Specializations & Global Licensing",
                    "Over ${course.topCollegesCount}+ Top NIRF Ranked Govt & Private Partner Colleges"
                )

                benefitsList.forEach { benefit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = benefit,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }

                if (cWhyChoose.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isKn) "ಈ ಕೋರ್ಸ್ ಏಕೆ ಆರಿಸಿಕೊಳ್ಳಬೇಕು?" else "Why Choose This Course?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cWhyChoose,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }

                if (cExams.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isKn) "ಪ್ರಮುಖ ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳು" else "Key Entrance Exams",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cExams,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }

                val cDetailedPathway = if (isKn && course.detailedPathwayKn.isNotBlank()) course.detailedPathwayKn else course.detailedPathway
                if (cPathway.isNotBlank() || cDetailedPathway.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    PathwayTimelineView(
                        detailedPathwayJson = cDetailedPathway,
                        pathwayText = cPathway,
                        entranceExamsText = cExams,
                        officialExamLinksJson = course.officialExamLinks,
                        cutoffGuidanceText = course.cutoffGuidance,
                        subjectsText = if (isKn && course.subjectsKn.isNotBlank()) course.subjectsKn else course.subjects,
                        eligibilityText = if (isKn && course.eligibilityKn.isNotBlank()) course.eligibilityKn else course.eligibility,
                        isKannada = isKn,
                        onBookCounselingClick = { onBookCounsellingSwiped(course) }
                    )
                }

                if (cRecruiters.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isKn) "ಉನ್ನತ ನೇಮಕಾತಿ ಸಂಸ್ಥೆಗಳು" else "Top Recruiting Companies",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cRecruiters,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Free Counselling Promo Card
                Surface(
                    color = OpenGreenContainer,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isKn) "100% ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್ ಸೇವೆ" else "100% FREE EXPERT COUNSELLING",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SuccessGreen
                            )
                            Text(
                                text = if (isKn) "ಎಲ್ಲಾ ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ ಕೌನ್ಸೆಲಿಂಗ್ ಸಂಪೂರ್ಣ ಉಚಿತವಾಗಿದೆ" else "1-on-1 expert guidance is completely free for all students",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Swipe Action
                Text(
                    text = if (isKn) "ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್ ಬುಕ್ ಮಾಡಲು ಕೆಳಗೆ ಸ್ವೈಪ್ ಮಾಡಿ:" else "Swipe below to book your 1-on-1 session:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SwipeToConfirmButton(
                    text = if (isKn) "ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್‌ಗೆ ಸ್ವೈಪ್ ಮಾಡಿ" else "Swipe to Book Free Counselling",
                    onConfirmed = {
                        onBookCounsellingSwiped(course)
                    }
                )
            }
        }
    }
}
