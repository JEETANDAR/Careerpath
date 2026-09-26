package com.aistudio.carrerpath.counseling.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aistudio.carrerpath.counseling.data.model.CareerEntity
import com.aistudio.carrerpath.counseling.ui.components.PathwayTimelineView
import com.aistudio.carrerpath.counseling.ui.components.SwipeToConfirmButton
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerDetailScreen(
    career: CareerEntity,
    currentLanguage: String,
    onBackClick: () -> Unit,
    onBookCounsellingClick: () -> Unit
) {
    val isKn = currentLanguage == "kn"

    fun getText(en: String, kn: String): String {
        return if (isKn && kn.isNotBlank()) kn else en
    }

    val titleText = getText(career.title, career.titleKn)
    val taglineText = getText(career.tagline, career.taglineKn)
    val descriptionText = getText(career.description, career.descriptionKn)
    val overviewText = getText(career.overview, career.overviewKn)
    val whyChooseText = getText(career.whyChoose, career.whyChooseKn)
    val benefitsText = getText(career.benefits, career.benefitsKn)
    val eligibilityText = getText(career.eligibility, career.eligibilityKn)
    val subjectsText = getText(career.subjects, career.subjectsKn)
    val examsText = getText(career.exams, career.examsKn)
    val pathwayText = getText(career.pathway, career.pathwayKn)
    val pathwayDetailedText = getText(career.pathwayDetailed, career.pathwayDetailedKn)
    val skillsText = getText(career.skills, career.skillsKn)
    val jobRolesText = getText(career.jobRoles, career.jobRolesKn)
    val opportunitiesText = getText(career.opportunities, career.opportunitiesKn)
    val topCollegesText = getText(career.topColleges, career.topCollegesKn)
    val topRecruitersText = getText(career.topRecruiters, career.topRecruitersKn)
    val scholarshipsText = getText(career.scholarships, career.scholarshipsKn)
    val futureScopeText = getText(career.futureScope, career.futureScopeKn)
    val certificationsText = getText(career.certifications, career.certificationsKn)
    val faqsText = getText(career.faqs, career.faqsKn)

    val scrollState = rememberScrollState()
    LaunchedEffect(career.id) {
        scrollState.scrollTo(0)
    }

    // Ensure fallback content so sections are always rich and never blank
    val descDisplay = descriptionText.ifBlank {
        if (isKn) "ಈ ವೃತ್ತಿಯ ಸಂಪೂರ್ಣ ವಿವರಗಳು, ಶೈಕ್ಷಣಿಕ ಹಂತಗಳು, ಉನ್ನತ ಕಾಲೇಜುಗಳು ಮತ್ತು ಭವಿಷ್ಯದ ವೃತ್ತಿ ಸರಪಳಿ."
        else "Discover full educational pathways, job scope, top NIRF colleges, entrance exams, and eligibility criteria for $titleText."
    }
    val overviewDisplay = overviewText.ifBlank {
        if (isKn) "$titleText ಭಾರತದಲ್ಲಿ ಮತ್ತು ಜಾಗತಿಕವಾಗಿ ಅತ್ಯಂತ ಹೆಚ್ಚಿನ ಬೇಡಿಕೆಯಿರುವ ಪ್ರಮುಖ ವೃತ್ತಿ ಕ್ಷೇತ್ರವಾಗಿದೆ."
        else "$titleText is a highly sought-after professional field offering great career stability, attractive compensation, and high growth."
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Header Bar
            TopAppBar(
                title = {
                    Text(
                        titleText,
                        maxLines = 1,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 90.dp)
            ) {
                // Rich Banner Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                            )
                        )
                ) {
                    val imgUrl = career.image.ifBlank {
                        when {
                            career.category.contains("Medical", true) || career.category.contains("Health", true) -> "https://images.pexels.com/photos/5452292/pexels-photo-5452292.jpeg?auto=compress&cs=tinysrgb&w=1200"
                            career.category.contains("Tech", true) || career.category.contains("Engineering", true) -> "https://images.pexels.com/photos/3861958/pexels-photo-3861958.jpeg?auto=compress&cs=tinysrgb&w=1200"
                            career.category.contains("Design", true) || career.category.contains("Art", true) -> "https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=1200"
                            career.category.contains("Aviation", true) || career.category.contains("Pilot", true) -> "https://images.pexels.com/photos/2026324/pexels-photo-2026324.jpeg?auto=compress&cs=tinysrgb&w=1200"
                            career.category.contains("Law", true) || career.category.contains("Legal", true) -> "https://images.pexels.com/photos/5668772/pexels-photo-5668772.jpeg?auto=compress&cs=tinysrgb&w=1200"
                            career.category.contains("Commerce", true) || career.category.contains("Management", true) -> "https://images.pexels.com/photos/3184292/pexels-photo-3184292.jpeg?auto=compress&cs=tinysrgb&w=1200"
                            else -> "https://images.pexels.com/photos/3184418/pexels-photo-3184418.jpeg?auto=compress&cs=tinysrgb&w=1200"
                        }
                    }

                    AsyncImage(
                        model = imgUrl,
                        contentDescription = titleText,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = EduBluePrimary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                career.category.uppercase(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            titleText,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (taglineText.isNotBlank()) {
                            Text(
                                taglineText,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Quick Stats Highlights - Clean 2x2 Grid Layout
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                QuickStatItem(if (isKn) "ಸರಾಸರಿ ವೇತನ" else "Avg Salary", career.salaryAverage.ifBlank { "₹8 - 15 LPA" }, Icons.Default.Payments)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                QuickStatItem(if (isKn) "ಉನ್ನತ ವೇತನ" else "Top Salary", career.salaryHighest.ifBlank { "₹25+ LPA" }, Icons.Default.TrendingUp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                QuickStatItem(if (isKn) "ಅವಧಿ" else "Duration", career.duration.ifBlank { "4-5 Years" }, Icons.Default.Schedule)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                QuickStatItem(if (isKn) "ಶುಲ್ಕ" else "Avg Fees", career.feesAverage.ifBlank { "₹1.5L - 4L/yr" }, Icons.Default.AccountBalance)
                            }
                        }
                    }
                }

                // Description & Overview
                DetailSectionCard(if (isKn) "ವಿವರಣೆ ಮತ್ತು ಅವಲೋಕನ" else "Overview & Description", descDisplay, Icons.Default.Info)

                DetailSectionCard(if (isKn) "ವೃತ್ತಿಯ ಪೂರ್ಣ ವಿವರಗಳು" else "Career Overview", overviewDisplay, Icons.Default.Description)

                // Why Choose
                if (whyChooseText.isNotBlank()) {
                    DetailSectionCard(if (isKn) "ಈ ವೃತ್ತಿಯನ್ನು ಏಕೆ ಆಯ್ಕೆ ಮಾಡಬೇಕು?" else "Why Choose This Career?", whyChooseText, Icons.Default.Stars)
                }

                // Benefits
                if (benefitsText.isNotBlank()) {
                    DetailSectionCard(if (isKn) "ಪ್ರಮುಖ ಪ್ರಯೋಜನಗಳು" else "Key Benefits", benefitsText, Icons.Default.CardGiftcard)
                }

                // Eligibility & Subjects
                if (eligibilityText.isNotBlank() || subjectsText.isNotBlank()) {
                    DetailSectionCard(
                        if (isKn) "ಅರ್ಹತೆ ಮತ್ತು ವಿಷಯಗಳು" else "Eligibility & Subjects",
                        "${if (eligibilityText.isNotBlank()) "${if (isKn) "ಅರ್ಹತೆ" else "Eligibility"}: $eligibilityText\n\n" else ""}${if (subjectsText.isNotBlank()) "${if (isKn) "ವಿಷಯಗಳು" else "Subjects"}: $subjectsText" else ""}".trim(),
                        Icons.Default.School
                    )
                }

                // Entrance Exams & Visual Pathway Timeline
                if (examsText.isNotBlank() || pathwayText.isNotBlank() || pathwayDetailedText.isNotBlank()) {
                    val detailedJson = if (isKn && career.pathwayDetailedKn.isNotBlank()) career.pathwayDetailedKn else career.pathwayDetailed
                    PathwayTimelineView(
                        detailedPathwayJson = detailedJson,
                        pathwayText = pathwayText,
                        entranceExamsText = examsText,
                        subjectsText = subjectsText,
                        eligibilityText = eligibilityText,
                        isKannada = isKn,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        onBookCounselingClick = onBookCounsellingClick
                    )
                }

                // Required Skills
                if (skillsText.isNotBlank()) {
                    DetailSectionCard(if (isKn) "ಅಗತ್ಯವಿರುವ ಕೌಶಲ್ಯಗಳು" else "Key Skills Required", skillsText, Icons.Default.Psychology)
                }

                // Job Roles & Opportunities
                if (jobRolesText.isNotBlank() || opportunitiesText.isNotBlank()) {
                    DetailSectionCard(
                        if (isKn) "ಉದ್ಯೋಗ ಪಾತ್ರಗಳು ಮತ್ತು ಅವಕಾಶಗಳು" else "Job Roles & Career Opportunities",
                        "${if (jobRolesText.isNotBlank()) "${if (isKn) "ಪಾತ್ರಗಳು" else "Roles"}: $jobRolesText\n\n" else ""}${if (opportunitiesText.isNotBlank()) "${if (isKn) "ಅವಕಾಶಗಳು" else "Opportunities"}: $opportunitiesText" else ""}".trim(),
                        Icons.Default.Work
                    )
                }

                // Top Colleges & Recruiters
                if (topCollegesText.isNotBlank() || topRecruitersText.isNotBlank()) {
                    DetailSectionCard(
                        if (isKn) "ಉನ್ನತ ಕಾಲೇಜುಗಳು ಮತ್ತು ಕಂಪನಿಗಳು" else "Top Colleges & Top Recruiters",
                        "${if (topCollegesText.isNotBlank()) "${if (isKn) "ಕಾಲೇಜುಗಳು" else "Colleges"}: $topCollegesText\n\n" else ""}${if (topRecruitersText.isNotBlank()) "${if (isKn) "ನೆಮಕಾತಿದಾರರು" else "Recruiters"}: $topRecruitersText" else ""}".trim(),
                        Icons.Default.Apartment
                    )
                }

                // Scholarships & Future Scope
                if (scholarshipsText.isNotBlank() || futureScopeText.isNotBlank()) {
                    DetailSectionCard(
                        if (isKn) "ವೇತನ ಮತ್ತು ವಿದ್ಯಾರ್ಥಿವೇತನ" else "Scholarships & Future Scope",
                        "${if (scholarshipsText.isNotBlank()) "${if (isKn) "ವಿದ್ಯಾರ್ಥಿವೇತನ" else "Scholarships"}: $scholarshipsText\n\n" else ""}${if (futureScopeText.isNotBlank()) "${if (isKn) "ಭವಿಷ್ಯದ ವ್ಯಾಪ್ತಿ" else "Future Scope"}: $futureScopeText" else ""}".trim(),
                        Icons.Default.CardMembership
                    )
                }

                // Certifications & FAQs
                if (certificationsText.isNotBlank() || faqsText.isNotBlank()) {
                    DetailSectionCard(
                        if (isKn) "ಪ್ರಮಾಣಪತ್ರಗಳು ಮತ್ತು ಪ್ರಶ್ನೋತ್ತರಗಳು" else "Certifications & FAQs",
                        "${if (certificationsText.isNotBlank()) "${if (isKn) "ಪ್ರಮಾಣಪತ್ರಗಳು" else "Certifications"}: $certificationsText\n\n" else ""}${if (faqsText.isNotBlank()) "${if (isKn) "ಪ್ರಶ್ನೋತ್ತರಗಳು" else "FAQs"}: $faqsText" else ""}".trim(),
                        Icons.Default.Quiz
                    )
                }

                // Coupon Card for Career Counselling
                Card(
                    colors = CardDefaults.cardColors(containerColor = RankBadgeGold.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, RankBadgeGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onBookCounsellingClick() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = RankBadgeGold, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isKn) "ಕೂಪನ್ ಸಿದ್ಧವಾಗಿದೆ: FIRSTFREE" else "COUPON READY: FIRSTFREE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RankBadgeGold
                                )
                                Text(
                                    text = if (isKn) "ನಿಮ್ಮ 1ನೇ ವೃತ್ತಿ ಕೌನ್ಸೆಲಿಂಗ್ ಸೆಷನ್ 100% ಉಚಿತವಾಗಿದೆ (₹100 ಉಳಿತಾಯ)" else "Your 1st Career Counselling Session is 100% FREE (Saved ₹100)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onBookCounsellingClick,
                            colors = ButtonDefaults.buttonColors(containerColor = RankBadgeGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isKn) "ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್ ಬುಕ್ ಮಾಡಿ (₹0)" else "Book Free Counselling Form (₹0)",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Sticky Bottom Bar - Swipe to Book Free Career Counselling
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = if (isKn) "ಉಚಿತ 1-ಆನ್-1 ವೃತ್ತಿ ಮಾರ್ಗದರ್ಶನ ಬುಕ್ ಮಾಡಿ:" else "Book 1-on-1 Career Counselling:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    SwipeToConfirmButton(
                        text = if (isKn) "ಸ್ವೈಪ್ ಮಾಡಿ (ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್)" else "Swipe for Free Counselling",
                        onConfirmed = onBookCounsellingClick
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
        }
    }
}

@Composable
private fun DetailSectionCard(title: String, content: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = EduBlueContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EduBluePrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                content,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
            )
        }
    }
}
