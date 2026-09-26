package com.aistudio.carrerpath.counseling.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.ui.theme.EduBlueContainer
import com.aistudio.carrerpath.counseling.ui.theme.EduBluePrimary
import com.aistudio.carrerpath.counseling.ui.theme.EduCyanTertiary
import com.aistudio.carrerpath.counseling.ui.theme.EduPurpleContainer
import com.aistudio.carrerpath.counseling.ui.theme.EduPurpleSecondary
import com.aistudio.carrerpath.counseling.ui.theme.OpenGreenContainer
import com.aistudio.carrerpath.counseling.ui.theme.RankBadgeGold
import com.aistudio.carrerpath.counseling.ui.theme.RankBadgeGoldContainer
import com.aistudio.carrerpath.counseling.ui.theme.SuccessGreen
import org.json.JSONArray
import org.json.JSONObject

/**
 * Data representation for an individual milestone step in a Career or Course pathway.
 */
data class PathwayTimelineStep(
    val stepNumber: Int,
    val stage: String,
    val title: String,
    val whatToDo: String,
    val whyItMatters: String = "",
    val icon: ImageVector = Icons.Default.School,
    val gradientColors: List<Color> = listOf(EduBluePrimary, EduCyanTertiary),
    val examLink: Pair<String, String>? = null
)

/**
 * Parsed collection of timeline information with baby-step granularity.
 */
data class ParsedTimelineData(
    val summary: String,
    val steps: List<PathwayTimelineStep>,
    val examLinks: List<Pair<String, String>> = emptyList(),
    val topicsToLearn: List<String> = emptyList(),
    val admissionProcess: String = "",
    val cutoffGuidance: String = "",
    val practicalTraining: String = "",
    val studentNote: String = ""
)

/**
 * Comprehensive catalog of official government and examination portals in India.
 */
private val OFFICIAL_EXAM_PORTALS = mapOf(
    "NEET UG" to "https://neet.nta.nic.in/",
    "NEET PG" to "https://nbe.edu.in/",
    "NEET" to "https://neet.nta.nic.in/",
    "JEE MAIN" to "https://jeemain.nta.nic.in/",
    "JEE ADVANCED" to "https://jeeadv.ac.in/",
    "JEE" to "https://jeemain.nta.nic.in/",
    "KCET" to "https://cetonline.karnataka.gov.in/kea/",
    "CET" to "https://cetonline.karnataka.gov.in/kea/",
    "COMEDK" to "https://www.comedk.org/",
    "COMEDK UGET" to "https://www.comedk.org/",
    "CUET" to "https://cuet.nta.nic.in/",
    "CUET UG" to "https://cuet.nta.nic.in/",
    "CLAT" to "https://consortiumofnlus.ac.in/",
    "CAT" to "https://iimcat.ac.in/",
    "GATE" to "https://gate.iitk.ac.in/",
    "DGCA" to "https://www.dgca.gov.in/",
    "AME" to "https://www.dgca.gov.in/",
    "NDA" to "https://upsc.gov.in/",
    "NID" to "https://admissions.nid.edu/",
    "NID DAT" to "https://admissions.nid.edu/",
    "UCEED" to "https://www.uceed.iitb.ac.in/",
    "NIFT" to "https://www.nift.ac.in/",
    "NATA" to "https://www.nata.in/",
    "ICAI" to "https://www.icai.org/",
    "CA FOUNDATION" to "https://www.icai.org/",
    "ICSI" to "https://www.icsi.edu/",
    "CS EXECUTIVE" to "https://www.icsi.edu/",
    "ICMAI" to "https://icmai.in/",
    "CMA" to "https://icmai.in/",
    "XAT" to "https://xatonline.in/",
    "GMAT" to "https://www.mba.com/exams/gmat"
)

/**
 * Modern, clean, student-friendly visual timeline UI for Career & Course pathways.
 * Provides a granular, baby-step milestone journey from Class 10/12 to entrance exams,
 * syllabus study topics, cutoffs, college choice filling, degree coursework, and career launch.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PathwayTimelineView(
    detailedPathwayJson: String = "",
    pathwayText: String = "",
    entranceExamsText: String = "",
    officialExamLinksJson: String = "",
    cutoffGuidanceText: String = "",
    subjectsText: String = "",
    eligibilityText: String = "",
    isKannada: Boolean = false,
    modifier: Modifier = Modifier,
    onBookCounselingClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val parsedData = remember(
        detailedPathwayJson,
        pathwayText,
        entranceExamsText,
        officialExamLinksJson,
        cutoffGuidanceText,
        subjectsText,
        eligibilityText,
        isKannada
    ) {
        parsePathwayTimeline(
            detailedJson = detailedPathwayJson,
            rawText = pathwayText,
            examsText = entranceExamsText,
            officialExamLinksJson = officialExamLinksJson,
            cutoffGuidanceText = cutoffGuidanceText,
            subjectsText = subjectsText,
            eligibilityText = eligibilityText,
            isKn = isKannada
        )
    }

    if (parsedData.steps.isEmpty() && parsedData.summary.isBlank()) {
        return
    }

    var selectedStepIndex by remember { mutableIntStateOf(0) }
    var expandedStepIndex by remember { mutableIntStateOf(0) }

    // Pulsing transition for the active step badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring()),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Icon + Title + Total Steps Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(EduBluePrimary, EduPurpleSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AltRoute,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isKannada) "ವೃತ್ತಿ & ಶೈಕ್ಷಣಿಕ ಮಾರ್ಗಸೂಚಿ" else "Career & Academic Pathway",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isKannada) "ಹಂತ-ಹಂತದ ಸಂಪೂರ್ಣ ಮಾರ್ಗದರ್ಶಿ" else "Granular Step-by-Step Road Map",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (parsedData.steps.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EduBlueContainer,
                        border = BorderStroke(1.dp, EduBluePrimary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isKannada) "${parsedData.steps.size} ಹಂತಗಳು" else "${parsedData.steps.size} Steps",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduBluePrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // High-Level "At a Glance" Summary Banner
            if (parsedData.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = EduBluePrimary,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isKannada) "ಸಂಕ್ಷಿಪ್ತ ನೋಟ (Roadmap Summary)" else "Roadmap Summary",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduBluePrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = parsedData.summary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Quick Interactive Stage Pills Scroll Row
            if (parsedData.steps.size > 2) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    parsedData.steps.forEachIndexed { index, step ->
                        val isSelected = selectedStepIndex == index
                        val bgCol by animateColorAsState(
                            targetValue = if (isSelected) EduBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            label = "pill_bg"
                        )
                        val textCol by animateColorAsState(
                            targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "pill_text"
                        )

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = bgCol,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) EduBluePrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .clickable {
                                    selectedStepIndex = index
                                    expandedStepIndex = index
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textCol
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = step.title.take(18) + if (step.title.length > 18) "…" else "",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textCol,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (index < parsedData.steps.size - 1) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // The Timeline Steps with Connecting Line
            parsedData.steps.forEachIndexed { index, step ->
                val isLast = index == parsedData.steps.size - 1
                val isExpanded = expandedStepIndex == index
                val isCurrentActive = selectedStepIndex == index

                TimelineStepRow(
                    step = step,
                    isLast = isLast,
                    isExpanded = isExpanded,
                    isActive = isCurrentActive,
                    pulseScale = if (isCurrentActive) pulseScale else 1f,
                    isKn = isKannada,
                    onToggleExpand = {
                        selectedStepIndex = index
                        expandedStepIndex = if (expandedStepIndex == index) -1 else index
                    },
                    onOpenExamLink = { url ->
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                )
            }

            // Topics & Curriculum Covered in Exam & Degree
            if (parsedData.topicsToLearn.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = EduBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isKannada) "ಪರೀಕ್ಷೆ ಮತ್ತು ಪದವಿಯಲ್ಲಿ ಕಲಿಯುವ ಮುಖ್ಯ ವಿಷಯಗಳು" else "Exam & Curriculum Topics Covered",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isKannada) "ಈ ವೃತ್ತಿ ಅಥವಾ ಕೋರ್ಸ್‌ನಲ್ಲಿ ನೀವು ಕಲಿಯುವ ಪ್ರಮುಖ ಪರಿಕಲ್ಪನೆಗಳು ಮತ್ತು ವಿಷಯಗಳು:" else "Core concepts and subjects you will study for entrance exams and degree curriculum:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            parsedData.topicsToLearn.forEach { topic ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = EduBlueContainer.copy(alpha = 0.7f),
                                    border = BorderStroke(1.dp, EduBluePrimary.copy(alpha = 0.25f))
                                ) {
                                    Text(
                                        text = "• $topic",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = EduBluePrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Cutoff & College Seat Allotment Guide
            if (parsedData.cutoffGuidance.isNotBlank() || parsedData.admissionProcess.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = RankBadgeGoldContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, RankBadgeGold.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = RankBadgeGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isKannada) "ಕಟ್-ಆಫ್ ಮತ್ತು ಸೀಟು ಹಂಚಿಕೆ ಮಾಹಿತಿ" else "Cutoff & College Seat Allotment Guide",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RankBadgeGold
                            )
                        }

                        if (parsedData.cutoffGuidance.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = parsedData.cutoffGuidance,
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (parsedData.admissionProcess.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isKannada) "ಪ್ರವೇಶ ಪ್ರಕ್ರಿಯೆ:" else "Admission Flow:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = parsedData.admissionProcess,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Official Entrance Exam Links (Clickable Government / Examination Portals)
            if (parsedData.examLinks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EduPurpleContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, EduPurpleSecondary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = EduPurpleSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isKannada) "ಅಧಿಕೃತ ಪ್ರವೇಶ ಪರೀಕ್ಷಾ ಲಿಂಕ್‌ಗಳು (Official Exam Portals)" else "Official Exam Portals & Direct Links",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduPurpleSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isKannada) "ಅರ್ಜಿ ಸಲ್ಲಿಸಲು, ದಿನಾಂಕಗಳನ್ನು ಪರಿಶೀಲಿಸಲು ಮತ್ತು ಪರೀಕ್ಷೆಯ ಅಧಿಕೃತ ಪೋರ್ಟಲ್‌ಗೆ ಭೇಟಿ ನೀಡಿ:" else "Tap to visit verified official government portals for notifications, syllabus & applications:",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        parsedData.examLinks.forEach { (examName, url) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, EduPurpleSecondary.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        if (url.isNotBlank()) {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = EduPurpleSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = examName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (url.isNotBlank()) {
                                                Text(
                                                    text = url.removePrefix("https://").removePrefix("http://").trimEnd('/'),
                                                    fontSize = 11.sp,
                                                    color = EduPurpleSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    FilledTonalButton(
                                        onClick = {
                                            if (url.isNotBlank()) {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {}
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = EduPurpleSecondary,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isKannada) "ತೆರೆಯಿರಿ" else "Visit",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Launch,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Student Encouragement & Counselling Shortcut Banner
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = OpenGreenContainer.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isKannada) "ನಿಮ್ಮ ವೃತ್ತಿ ಕನಸನ್ನು ನನಸು ಮಾಡಿ!" else "Empower Your Career Goal",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = if (isKannada) "ತಜ್ಞ ಕೌನ್ಸಿಲರ್‌ಗಳೊಂದಿಗೆ ವೈಯಕ್ತಿಕ ಮಾರ್ಗದರ್ಶನ ಪಡೆಯಿರಿ" else "Talk to certified counsellors for cutoffs & personalized guidance",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (onBookCounselingClick != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalButton(
                            onClick = onBookCounselingClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SuccessGreen,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isKannada) "ಸಂಪರ್ಕಿಸಿ" else "Guidance",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Timeline Step Row with left-side number node & connecting vertical line.
 */
@Composable
private fun TimelineStepRow(
    step: PathwayTimelineStep,
    isLast: Boolean,
    isExpanded: Boolean,
    isActive: Boolean,
    pulseScale: Float,
    isKn: Boolean,
    onToggleExpand: () -> Unit,
    onOpenExamLink: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Left Column: Number Node + Vertical Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(44.dp)
        ) {
            // Circle Number Node
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .scale(if (isActive) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(colors = step.gradientColors)
                    )
                    .border(
                        width = if (isActive) 2.5.dp else 1.5.dp,
                        color = if (isActive) Color.White else step.gradientColors.first().copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step.stepNumber}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            // Connecting Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .padding(vertical = 3.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    step.gradientColors.last(),
                                    step.gradientColors.first().copy(alpha = 0.35f)
                                )
                            )
                        )
                )
            } else {
                // Milestone Flag for Final Step
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: Expandable Card with Stage, Title, Description, and Link
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 6.dp else 16.dp)
        ) {
            OutlinedCard(
                onClick = onToggleExpand,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isActive) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                ),
                border = BorderStroke(
                    width = if (isActive) 1.5.dp else 1.dp,
                    color = if (isActive) EduBluePrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = spring())
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Stage Pill & Icon Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = step.gradientColors.first().copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = step.icon,
                                    contentDescription = null,
                                    tint = step.gradientColors.first(),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = step.stage.ifBlank { if (isKn) "ಹಂತ ${step.stepNumber}" else "Step ${step.stepNumber}" },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = step.gradientColors.first()
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bold Title
                    Text(
                        text = step.title,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    // Expandable Description and "Why It Matters" Note
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            if (step.whatToDo.isNotBlank() && step.whatToDo != step.title) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = step.whatToDo,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // If this step links to an official exam portal, show direct button!
                            if (step.examLink != null && step.examLink.second.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { onOpenExamLink(step.examLink.second) },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, EduBluePrimary),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = EduBluePrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Launch,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${step.examLink.first} ${if (isKn) "ಅಧಿಕೃತ ಪೋರ್ಟಲ್" else "Official Website"}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (step.whyItMatters.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = RankBadgeGoldContainer.copy(alpha = 0.45f),
                                    border = BorderStroke(1.dp, RankBadgeGold.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = RankBadgeGold,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(top = 1.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isKn) "ಇದು ಏಕೆ ಮುಖ್ಯ?" else "Why this step matters:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RankBadgeGold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = step.whyItMatters,
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Intelligent parser that extracts granular, baby-step milestone steps from JSON objects or free-text strings.
 */
private fun parsePathwayTimeline(
    detailedJson: String,
    rawText: String,
    examsText: String,
    officialExamLinksJson: String = "",
    cutoffGuidanceText: String = "",
    subjectsText: String = "",
    eligibilityText: String = "",
    isKn: Boolean
): ParsedTimelineData {
    var summary = ""
    val steps = mutableListOf<PathwayTimelineStep>()
    val examLinks = mutableListOf<Pair<String, String>>()
    val topicsToLearn = mutableListOf<String>()
    var admissionProcess = ""
    var cutoffGuidance = cutoffGuidanceText
    var practicalTraining = ""
    var studentNote = ""

    // Helper to find known official exam link
    fun findOfficialExamLink(query: String): Pair<String, String>? {
        val upper = query.uppercase()
        for ((key, url) in OFFICIAL_EXAM_PORTALS) {
            if (upper.contains(key)) {
                return Pair(key, url)
            }
        }
        return null
    }

    // Helper to parse official exam links array from JSON string
    fun parseExamLinksArray(jsonStr: String) {
        if (jsonStr.isBlank()) return
        try {
            val arr = JSONArray(jsonStr.trim())
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val name = obj.optString("name").ifBlank { obj.optString("title") }
                val url = obj.optString("url").ifBlank { obj.optString("officialWebsite") }
                if (name.isNotBlank() && examLinks.none { it.first.equals(name, true) }) {
                    examLinks.add(Pair(name, url))
                }
            }
        } catch (_: Exception) {}
    }

    // Attempt 1: Parse from structured JSON if provided
    if (detailedJson.isNotBlank()) {
        try {
            var normalizedJson = detailedJson.trim()
            if (normalizedJson.startsWith("{") && !normalizedJson.contains("\":") && normalizedJson.contains("=")) {
                // Convert Map representation {key=val} into standard JSON
                normalizedJson = normalizedJson.replace(Regex("([{, ])([a-zA-Z0-9_]+)="), "$1\"$2\":")
            }

            val json = JSONObject(normalizedJson)
            summary = json.optString("simpleSummary")

            // Check course 'babySteps' array (Ultra-detailed 10-12 steps!)
            val babyStepsArray = json.optJSONArray("babySteps")
            if (babyStepsArray != null && babyStepsArray.length() > 0) {
                for (i in 0 until babyStepsArray.length()) {
                    val obj = babyStepsArray.optJSONObject(i) ?: continue
                    val stepNum = obj.optInt("step", i + 1)
                    val title = obj.optString("title")
                    val whatToDo = obj.optString("whatToDo")
                    val whyItMatters = obj.optString("whyItMatters")
                    val link = findOfficialExamLink("$title $whatToDo")

                    steps.add(
                        PathwayTimelineStep(
                            stepNumber = stepNum,
                            stage = if (isKn) "ಹಂತ $stepNum" else "Step $stepNum",
                            title = title,
                            whatToDo = whatToDo,
                            whyItMatters = whyItMatters,
                            icon = getIconForStage(title, whatToDo, stepNum),
                            gradientColors = getGradientsForStep(stepNum, babyStepsArray.length()),
                            examLink = link
                        )
                    )
                }
            }

            // Check career 'steps' array
            val stepsArray = json.optJSONArray("steps")
            if (steps.isEmpty() && stepsArray != null && stepsArray.length() > 0) {
                for (i in 0 until stepsArray.length()) {
                    val obj = stepsArray.optJSONObject(i) ?: continue
                    val stepNum = obj.optInt("step", i + 1)
                    val stage = obj.optString("stage")
                    val whatToDo = obj.optString("whatToDo")
                    val title = obj.optString("title").ifBlank { stage.ifBlank { "Step $stepNum" } }
                    val whyItMatters = obj.optString("whyItMatters")
                    val link = findOfficialExamLink("$title $whatToDo $stage")

                    steps.add(
                        PathwayTimelineStep(
                            stepNumber = stepNum,
                            stage = stage.ifBlank { if (isKn) "ಹಂತ $stepNum" else "Step $stepNum" },
                            title = title,
                            whatToDo = whatToDo,
                            whyItMatters = whyItMatters,
                            icon = getIconForStage(stage, whatToDo, stepNum),
                            gradientColors = getGradientsForStep(stepNum, stepsArray.length()),
                            examLink = link
                        )
                    )
                }
            }

            // Extract entranceAndAdmission object
            val entranceObj = json.optJSONObject("entranceAndAdmission")
            if (entranceObj != null) {
                val examsArr = entranceObj.optJSONArray("exams")
                if (examsArr != null) {
                    for (i in 0 until examsArr.length()) {
                        val examItem = examsArr.optJSONObject(i) ?: continue
                        val name = examItem.optString("name")
                        val url = examItem.optString("officialWebsite").ifBlank { examItem.optString("url") }
                        if (name.isNotBlank() && examLinks.none { it.first.equals(name, true) }) {
                            examLinks.add(Pair(name, url))
                        }
                    }
                }
                admissionProcess = entranceObj.optString("admissionProcess")
                if (cutoffGuidance.isBlank()) {
                    cutoffGuidance = entranceObj.optString("cutoffInfo")
                }
            }

            // Extract collegeStage object
            val collegeObj = json.optJSONObject("collegeStage")
            if (collegeObj != null) {
                val whatYouLearnArr = collegeObj.optJSONArray("whatYouLearn")
                if (whatYouLearnArr != null) {
                    for (i in 0 until whatYouLearnArr.length()) {
                        val topic = whatYouLearnArr.optString(i)
                        if (topic.isNotBlank()) topicsToLearn.add(topic)
                    }
                }
                practicalTraining = collegeObj.optString("practicalTraining")
            }

            // Extract cutoff object (from careers.json)
            val cutoffObj = json.optJSONObject("cutoff")
            if (cutoffObj != null && cutoffGuidance.isBlank()) {
                val type = cutoffObj.optString("type")
                val note = cutoffObj.optString("note")
                val example = cutoffObj.optString("example")
                cutoffGuidance = listOf(type, note, example).filter { it.isNotBlank() }.joinToString("\n\n")
            }

            // Extract exam links from root
            val linksArray = json.optJSONArray("examLinks") ?: json.optJSONArray("officialExamLinks")
            if (linksArray != null) {
                for (i in 0 until linksArray.length()) {
                    val linkObj = linksArray.optJSONObject(i) ?: continue
                    val name = linkObj.optString("name")
                    val url = linkObj.optString("url").ifBlank { linkObj.optString("officialWebsite") }
                    if (name.isNotBlank() && examLinks.none { it.first.equals(name, true) }) {
                        examLinks.add(Pair(name, url))
                    }
                }
            }

            studentNote = json.optString("studentNote").ifBlank { json.optString("importantNote") }

        } catch (_: Exception) {
            // Fallback continues below
        }
    }

    // Parse separate officialExamLinksJson if passed
    if (officialExamLinksJson.isNotBlank()) {
        parseExamLinksArray(officialExamLinksJson)
    }

    // Attempt 2: If no steps extracted yet, parse from raw text
    if (steps.isEmpty() && rawText.isNotBlank()) {
        // If rawText has arrows (→ or -> or >), split by arrows to create all individual steps!
        if (rawText.contains("→") || rawText.contains("->") || (rawText.contains(">") && !rawText.contains("<"))) {
            val arrowTokens = rawText.split(Regex("→|->|>"))
                .map { it.trim() }
                .filter { it.isNotBlank() && it.length > 2 }

            if (arrowTokens.size > 1) {
                arrowTokens.forEachIndexed { idx, token ->
                    val stepNum = idx + 1
                    val (generatedStage, generatedWhatToDo, generatedWhy) = generateStepDetails(
                        token = token,
                        stepNumber = stepNum,
                        totalSteps = arrowTokens.size,
                        examsText = examsText,
                        subjectsText = subjectsText,
                        eligibilityText = eligibilityText,
                        isKn = isKn
                    )
                    val link = findOfficialExamLink("$token $examsText")

                    steps.add(
                        PathwayTimelineStep(
                            stepNumber = stepNum,
                            stage = generatedStage,
                            title = token,
                            whatToDo = generatedWhatToDo,
                            whyItMatters = generatedWhy,
                            icon = getIconForStage(generatedStage, token, stepNum),
                            gradientColors = getGradientsForStep(stepNum, arrowTokens.size),
                            examLink = link
                        )
                    )
                }
            }
        }

        // If still empty, check for "Step 1: ... Step 2: ..."
        if (steps.isEmpty()) {
            val regex = Regex("""(?:Step|ಹಂತ)\s*(\d+)[:.-]?\s*(.+?)(?=(?:(?:Step|ಹಂತ)\s*\d+[:.-]|$))""", RegexOption.DOT_MATCHES_ALL)
            val matches = regex.findAll(rawText).toList()

            if (matches.isNotEmpty()) {
                matches.forEachIndexed { idx, match ->
                    val stepNum = match.groupValues[1].toIntOrNull() ?: (idx + 1)
                    val fullText = match.groupValues[2].trim()

                    val parts = fullText.split(Regex("[:\n—–]"), limit = 2)
                    val title = parts.firstOrNull()?.trim() ?: fullText
                    val desc = if (parts.size > 1) parts[1].trim() else fullText
                    val link = findOfficialExamLink("$title $desc $examsText")

                    steps.add(
                        PathwayTimelineStep(
                            stepNumber = stepNum,
                            stage = if (isKn) "ಹಂತ $stepNum" else "Step $stepNum",
                            title = title,
                            whatToDo = desc,
                            whyItMatters = generateGenericWhyItMatters(stepNum, matches.size, isKn),
                            icon = getIconForStage(title, desc, stepNum),
                            gradientColors = getGradientsForStep(stepNum, matches.size),
                            examLink = link
                        )
                    )
                }
            } else {
                // Freeform sentence fallback
                val sentences = rawText.split("\n", ".").map { it.trim() }.filter { it.length > 5 }
                if (sentences.isNotEmpty()) {
                    sentences.forEachIndexed { i, s ->
                        val stepNum = i + 1
                        val link = findOfficialExamLink("$s $examsText")
                        steps.add(
                            PathwayTimelineStep(
                                stepNumber = stepNum,
                                stage = if (isKn) "ಹಂತ $stepNum" else "Step $stepNum",
                                title = s,
                                whatToDo = s,
                                whyItMatters = generateGenericWhyItMatters(stepNum, sentences.size, isKn),
                                icon = getIconForStage(s, s, stepNum),
                                gradientColors = getGradientsForStep(stepNum, sentences.size),
                                examLink = link
                            )
                        )
                    }
                }
            }
        }
    }

    // Populate topicsToLearn from subjectsText if empty
    if (topicsToLearn.isEmpty() && subjectsText.isNotBlank()) {
        val splitSubjects = subjectsText.split(",", "•", ";", "\n").map { it.trim() }.filter { it.length > 2 }
        topicsToLearn.addAll(splitSubjects)
    }

    // Ensure entrance exams mentioned in examsText or steps are mapped to their official links
    if (examsText.isNotBlank()) {
        for ((examKey, examUrl) in OFFICIAL_EXAM_PORTALS) {
            if (examsText.uppercase().contains(examKey) && examLinks.none { it.first.equals(examKey, true) }) {
                examLinks.add(Pair(examKey, examUrl))
            }
        }
    }
    steps.forEach { step ->
        for ((examKey, examUrl) in OFFICIAL_EXAM_PORTALS) {
            if ((step.title.uppercase().contains(examKey) || step.whatToDo.uppercase().contains(examKey)) &&
                examLinks.none { it.first.equals(examKey, true) }) {
                examLinks.add(Pair(examKey, examUrl))
            }
        }
    }

    return ParsedTimelineData(
        summary = summary,
        steps = steps,
        examLinks = examLinks,
        topicsToLearn = topicsToLearn,
        admissionProcess = admissionProcess,
        cutoffGuidance = cutoffGuidance,
        practicalTraining = practicalTraining,
        studentNote = studentNote
    )
}

/**
 * Generates granular, contextual baby-step descriptions and rationale for pathway milestones.
 */
private fun generateStepDetails(
    token: String,
    stepNumber: Int,
    totalSteps: Int,
    examsText: String,
    subjectsText: String,
    eligibilityText: String,
    isKn: Boolean
): Triple<String, String, String> {
    val lower = token.lowercase()

    return when {
        lower.contains("10") || lower.contains("class 10") || lower.contains("ಶಾಲೆ") -> {
            val stage = if (isKn) "ಶಾಲಾ ಶಿಕ್ಷಣ" else "School Foundation"
            val whatToDo = if (isKn)
                "10ನೇ ತರಗತಿ ಪರೀಕ್ಷೆಯಲ್ಲಿ ಗಣಿತ, ವಿಜ್ಞಾನ ಮತ್ತು ಭಾಷೆಗಳಲ್ಲಿ ಉತ್ತಮ ಅಂಕಗಳೊಂದಿಗೆ ಉತ್ತೀರ್ಣರಾಗಿ."
            else
                "Pass Class 10 board examination with strong fundamentals in Science and Mathematics."
            val why = if (isKn)
                "10ನೇ ತರಗತಿಯ ಫಲಿತಾಂಶವು 11-12ನೇ ತರಗತಿ / ಪಿಯುಸಿಯಲ್ಲಿ ನಿಮ್ಮ ನೆಚ್ಚಿನ ವಿಭಾಗ ಆಯ್ಕೆ ಮಾಡಲು ಅಡಿಪಾಯವಾಗಿದೆ."
            else
                "Class 10 completion qualifies you for higher secondary (10+2 / PUC) stream selection."
            Triple(stage, whatToDo, why)
        }
        lower.contains("11") || lower.contains("12") || lower.contains("subject") || lower.contains("stream") -> {
            val stage = if (isKn) "ವಿಷಯಗಳ ಆಯ್ಕೆ" else "Stream & Subject Selection"
            val whatToDo = if (subjectsText.isNotBlank()) {
                if (isKn) "11-12ನೇ ತರಗತಿಯಲ್ಲಿ ಈ ಕೋರ್ಸ್‌ಗೆ ಅಗತ್ಯವಿರುವ ವಿಷಯಗಳನ್ನು ಆಯ್ಕೆ ಮಾಡಿ: $subjectsText."
                else "Enroll in 11th & 12th / PUC with required core subjects: $subjectsText."
            } else {
                if (isKn) "ಕೋರ್ಸ್ ಅರ್ಹತೆಗೆ ಅನುಗುಣವಾಗಿ ವಿಜ್ಞಾನ/ವಾಣಿಜ್ಯ/ಕಲಾ ವಿಭಾಗವನ್ನು ಆಯ್ಕೆ ಮಾಡಿ."
                else "Select the required stream (PCB / PCM / Commerce / Arts) matching the course eligibility criteria."
            }
            val why = if (isKn)
                "ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳು ಮತ್ತು ಕಾಲೇಜುಗಳಿಗೆ ನಿರ್ದಿಷ್ಟ ವಿಷಯ ಸಂಯೋಜನೆ ಕಡ್ಡಾಯವಾಗಿದೆ."
            else
                "Entrance examinations and degree admissions require mandatory subject combinations in 10+2."
            Triple(stage, whatToDo, why)
        }
        lower.contains("pass class 12") || lower.contains("complete 12") || lower.contains("12th") || lower.contains("ಪಿಯುಸಿ") -> {
            val stage = if (isKn) "ಪಿಯುಸಿ ಪೂರ್ಣಗೊಳಿಸಿ" else "12th Board Qualification"
            val whatToDo = if (eligibilityText.isNotBlank()) {
                if (isKn) "ಕನಿಷ್ಠ ಅಂಕಗಳೊಂದಿಗೆ 12ನೇ ತರಗತಿ ಪಾಸು ಮಾಡಿ. ಅರ್ಹತೆ: $eligibilityText."
                else "Clear 12th Board Exams meeting eligibility: $eligibilityText."
            } else {
                if (isKn) "ಕನಿಷ್ಠ 50%+ ಅಂಕಗಳೊಂದಿಗೆ 12ನೇ ತರಗತಿ ಬೋರ್ಡ್ ಪರೀಕ್ಷೆಯಲ್ಲಿ ತೇರ್ಗಡೆಯಾಗಿ."
                else "Complete 12th Board Examinations with qualifying aggregate marks (typically 50%+)."
            }
            val why = if (isKn)
                "12ನೇ ತರಗತಿಯ ಅಂಕಗಳು ಪ್ರವೇಶ ಪರೀಕ್ಷೆಯ ಅರ್ಹತೆ ಮತ್ತು ಕೌನ್ಸಿಲಿಂಗ್ ಮೆರಿಟ್‌ಗೆ ಅತ್ಯಗತ್ಯ."
            else
                "10+2 results establish your baseline eligibility for competitive entrance counselling."
            Triple(stage, whatToDo, why)
        }
        lower.contains("exam") || lower.contains("entrance") || lower.contains("neet") || lower.contains("jee") || lower.contains("cet") || lower.contains("ಪರೀಕ್ಷೆ") -> {
            val stage = if (isKn) "ಪ್ರವೇಶ ಪರೀಕ್ಷೆ" else "Entrance Examination"
            val whatToDo = if (examsText.isNotBlank()) {
                if (isKn) "ಅಗತ್ಯ ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳಿಗೆ ಅರ್ಜಿ ಸಲ್ಲಿಸಿ ಮತ್ತು ಬರೆಯಿರಿ: $examsText. ಅಧಿಕೃತ ಪಠ್ಯಕ್ರಮ ಮತ್ತು ಹಿಂದಿನ ಪ್ರಶ್ನೆ ಪತ್ರಿಕೆಗಳನ್ನು ಅಭ್ಯಾಸ ಮಾಡಿ."
                else "Register, prepare, and appear for $examsText. Study official exam syllabus, take mock tests, and master time management."
            } else {
                if (isKn) "ರಾಜ್ಯ ಅಥವಾ ರಾಷ್ಟ್ರೀಯ ಮಟ್ಟದ ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗೆ ಹಾಜರಾಗಿ."
                else "Appear for mandatory national or state entrance examinations."
            }
            val why = if (isKn)
                "ಪ್ರವೇಶ ಪರೀಕ್ಷೆಯ ರ್ಯಾಂಕ್ ಮತ್ತು ಪರ್ಸೆಂಟೈಲ್ ನಿಮ್ಮ ಕಾಲೇಜು ಆಯ್ಕೆ ಮತ್ತು ಸೀಟು ಪಡೆಯಲು ನೇರ ಮಾನದಂಡವಾಗಿದೆ."
            else
                "Your entrance exam score/rank determines your merit position and college admission counselling eligibility."
            Triple(stage, whatToDo, why)
        }
        lower.contains("apply") || lower.contains("admission") || lower.contains("college") || lower.contains("seat") || lower.contains("ಕಾಲೇಜು") || lower.contains("ಪ್ರವೇಶ") -> {
            val stage = if (isKn) "ಕೌನ್ಸಿಲಿಂಗ್ ಮತ್ತು ಸೀಟು ಆಯ್ಕೆ" else "Counselling & College Choice"
            val whatToDo = if (isKn)
                "ಕೇಂದ್ರೀಕೃತ ಕೌನ್ಸಿಲಿಂಗ್ ಪೋರ್ಟಲ್‌ನಲ್ಲಿ ನೋಂದಾಯಿಸಿ, ಕಾಲೇಜುಗಳ ಆದ್ಯತಾ ಪಟ್ಟಿಯನ್ನು ಭರ್ತಿ ಮಾಡಿ, ಕಟ್-ಆಫ್ ಆಧರಿಸಿ ಸೀಟು ಆಯ್ಕೆ ಮಾಡಿಕೊಳ್ಳಿ."
            else
                "Register for centralized counselling (e.g. KEA / MCC / JoSAA), fill preferred college choices, monitor cutoff rounds, and accept allotted seat."
            val why = if (isKn)
                "ಸರಿಯಾದ ಕೌನ್ಸಿಲಿಂಗ್ ಆಯ್ಕೆಗಳು ಮತ್ತು ಕಟ್-ಆಫ್ ವಿಶ್ಲೇಷಣೆಯು ನಿಮ್ಮ ರ್ಯಾಂಕ್‌ಗೆ ಅತ್ಯುತ್ತಮ ಕಾಲೇಜು ಪಡೆಯಲು ಸಹಾಯ ಮಾಡುತ್ತದೆ."
            else
                "Strategic choice filling and cutoff tracking ensures you secure the best college matching your rank."
            Triple(stage, whatToDo, why)
        }
        lower.contains("study") || lower.contains("course") || lower.contains("degree") || lower.contains("ಅಧ್ಯಯನ") -> {
            val stage = if (isKn) "ಪದವಿ ಕೋರ್ಸ್ ಅಧ್ಯಯನ" else "Degree Coursework & Labs"
            val whatToDo = if (isKn)
                "ಕಾಲೇಜಿನಲ್ಲಿ ತರಗತಿಗಳು, ಪ್ರಾಯೋಗಿಕ ಲ್ಯಾಬ್‌ಗಳು, ಸೆಮಿಸ್ಟರ್ ಪರೀಕ್ಷೆಗಳು ಮತ್ತು ಪ್ರಾಜೆಕ್ಟ್‌ಗಳನ್ನು ಯಶಸ್ವಿಯಾಗಿ ಪೂರ್ಣಗೊಳಿಸಿ."
            else
                "Attend lectures, laboratory practical sessions, clear all semester exams, and complete required capstone projects."
            val why = if (isKn)
                "ಕೋರ್ಸ್‌ನ ಮುಖ್ಯ ಪರಿಕಲ್ಪನೆಗಳು ಮತ್ತು ಪ್ರಾಯೋಗಿಕ ಜ್ಞಾನವನ್ನು ಕರಗತ ಮಾಡಿಕೊಳ್ಳುವುದು ನಿಮ್ಮ ವೃತ್ತಿಪರ ನೈಪುಣ್ಯಕ್ಕೆ ಅಡಿಪಾಯ."
            else
                "Mastering core syllabus concepts and practical laboratory experiments forms your technical expertise."
            Triple(stage, whatToDo, why)
        }
        lower.contains("intern") || lower.contains("practical") || lower.contains("train") || lower.contains("ತರಬೇತಿ") -> {
            val stage = if (isKn) "ಪ್ರಾಯೋಗಿಕ ತರಬೇತಿ" else "Practical Training & Internship"
            val whatToDo = if (isKn)
                "ಕಡ್ಡಾಯ ಇಂಟರ್ನ್‌ಶಿಪ್, ಆಸ್ಪತ್ರೆ/ಉದ್ಯಮ ತರಬೇತಿ ಮತ್ತು ಕ್ಲಿನಿಕಲ್/ತಾಂತ್ರಿಕ ಕೆಲಸಗಳಲ್ಲಿ ಅನುಭವ ಪಡೆಯಿರಿ."
            else
                "Complete mandatory rotational internship, clinical postings, or industrial on-the-job training."
            val why = if (isKn)
                "ನೈಜ ಜಗತ್ತಿನ ಪ್ರಾಯೋಗಿಕ ಅನುಭವವು ವೃತ್ತಿ ಪರವಾನಗಿ ಪಡೆಯಲು ಮತ್ತು ಉದ್ಯೋಗದಲ್ಲಿ ಯಶಸ್ವಿಯಾಗಲು ನಿರ್ಣಾಯಕ."
            else
                "Real-world clinical or industrial internship provides hands-on competency required for professional practice."
            Triple(stage, whatToDo, why)
        }
        lower.contains("licen") || lower.contains("certif") || lower.contains("qualif") || lower.contains("ನೋಂದಣಿ") -> {
            val stage = if (isKn) "ವೃತ್ತಿಪರ ಪರವಾನಗಿ" else "Licensure & Registration"
            val whatToDo = if (isKn)
                "ಸಂಬಂಧಿತ ಶಾಸನಬದ್ಧ ಮಂಡಳಿ ಅಥವಾ ಕೌನ್ಸಿಲ್‌ನಲ್ಲಿ ನೋಂದಾಯಿಸಿ ಅಧಿಕೃತ ವೃತ್ತಿಪರ ಪ್ರಮಾಣಪತ್ರ ಪಡೆಯಿರಿ."
            else
                "Register with the statutory council (e.g. Medical Council, DGCA, Bar Council, or ICAI) to obtain official license to practice."
            val why = if (isKn)
                "ಕಾನೂನುಬದ್ಧವಾಗಿ ವೃತ್ತಿ ಅಭ್ಯಾಸ ಮಾಡಲು ಶಾಸನಬದ್ಧ ಮಂಡಳಿಯ ನೋಂದಣಿ ಮತ್ತು ಪರವಾನಗಿ ಕಡ್ಡಾಯವಾಗಿದೆ."
            else
                "Statutory council licensure is legally mandated to practice independently in regulated professions."
            Triple(stage, whatToDo, why)
        }
        lower.contains("job") || lower.contains("career") || lower.contains("apply for jobs") || lower.contains("ಉದ್ಯೋಗ") -> {
            val stage = if (isKn) "ವೃತ್ತಿ ಜೀವನ ಪ್ರಾರಂಭ" else "Career Launch"
            val whatToDo = if (isKn)
                "ಉತ್ತಮ ರೆಸ್ಯೂಮೆ ತಯಾರಿಸಿ, ಕ್ಯಾಂಪಸ್ ಸಂದರ್ಶನಗಳಲ್ಲಿ ಭಾಗವಹಿಸಿ, ನಿಮ್ಮ ವೃತ್ತಿ ಕ್ಷೇತ್ರವನ್ನು ಯಶಸ್ವಿಯಾಗಿ ಪ್ರಾರಂಭಿಸಿ."
            else
                "Build your professional resume, attend campus recruitment interviews, and step into your first professional career role."
            val why = if (isKn)
                "ನಿಮ್ಮ ಆರಂಭಿಕ ಉದ್ಯೋಗವು ವೃತ್ತಿ ಬೆಳವಣಿಗೆ ಮತ್ತು ವಿಶೇಷ ಅಧ್ಯಯನಕ್ಕೆ ಉತ್ತಮ ಮಾರ್ಗ ತೆರೆಯುತ್ತದೆ."
            else
                "Your initial career placement launches your professional journey and advancement in the field."
            Triple(stage, whatToDo, why)
        }
        else -> {
            val stage = if (isKn) "ಹಂತ $stepNumber" else "Step $stepNumber"
            val whatToDo = token
            val why = generateGenericWhyItMatters(stepNumber, totalSteps, isKn)
            Triple(stage, whatToDo, why)
        }
    }
}

private fun generateGenericWhyItMatters(stepNumber: Int, totalSteps: Int, isKn: Boolean): String {
    return if (isKn) {
        when {
            stepNumber <= 2 -> "ಈ ಆರಂಭಿಕ ಹಂತವು ನಿಮ್ಮ ಭವಿಷ್ಯದ ಪ್ರವೇಶ ಪರೀಕ್ಷೆ ಮತ್ತು ಉನ್ನತ ಶಿಕ್ಷಣಕ್ಕೆ ಮೂಲಭೂತ ಅರ್ಹತೆಯನ್ನು ನೀಡುತ್ತದೆ."
            stepNumber < totalSteps -> "ಈ ಹಂತದಲ್ಲಿ ಸಿದ್ಧತೆ ಮತ್ತು ಪರಿಶ್ರಮವು ನಿಮ್ಮ ಕಾಲೇಜು ಆಯ್ಕೆ ಮತ್ತು ವೃತ್ತಿ ಅವಕಾಶಗಳನ್ನು ಹೆಚ್ಚಿಸುತ್ತದೆ."
            else -> "ಇದು ನಿಮ್ಮ ಸ್ವಾವಲಂಬಿ ವೃತ್ತಿಪರ ಜೀವನವನ್ನು ಆರಂಭಿಸಲು ಸಹಾಯ ಮಾಡುತ್ತದೆ."
        }
    } else {
        when {
            stepNumber <= 2 -> "This foundational step satisfies mandatory prerequisites for entrance exams and degree admissions."
            stepNumber < totalSteps -> "Diligent preparation at this stage opens doors to top-tier colleges and merit allotments."
            else -> "Achieving this milestone launches your independent professional career."
        }
    }
}

/**
 * Returns an appropriate Material icon based on stage or content semantics.
 */
private fun getIconForStage(stage: String, desc: String, stepNumber: Int): ImageVector {
    val combined = "$stage $desc".lowercase()
    return when {
        combined.contains("school") || combined.contains("10") || combined.contains("12") || combined.contains("puc") || combined.contains("ಶಾಲೆ") || combined.contains("ತರಗತಿ") -> Icons.Default.School
        combined.contains("exam") || combined.contains("neet") || combined.contains("jee") || combined.contains("cet") || combined.contains("test") || combined.contains("ಪರೀಕ್ಷೆ") -> Icons.Default.Assignment
        combined.contains("admiss") || combined.contains("apply") || combined.contains("seat") || combined.contains("counsell") || combined.contains("ಪ್ರವೇಶ") || combined.contains("ಕಾಲೇಜು") -> Icons.Default.AccountBalance
        combined.contains("study") || combined.contains("degree") || combined.contains("learn") || combined.contains("course") || combined.contains("ಅಧ್ಯಯನ") || combined.contains("ಕೋರ್ಸ್") -> Icons.Default.MenuBook
        combined.contains("intern") || combined.contains("train") || combined.contains("practic") || combined.contains("ಇಂಟರ್ನ್‌ಶಿಪ್") || combined.contains("ತರಬೇತಿ") -> Icons.Default.Handshake
        combined.contains("qualif") || combined.contains("licen") || combined.contains("certif") || combined.contains("ನೋಂದಣಿ") || combined.contains("ಅರ್ಹತೆ") -> Icons.Default.WorkspacePremium
        combined.contains("career") || combined.contains("job") || combined.contains("work") || combined.contains("start") || combined.contains("ವೃತ್ತಿ") || combined.contains("ಉದ್ಯೋಗ") -> Icons.Default.RocketLaunch
        stepNumber == 1 -> Icons.Default.School
        stepNumber == 2 -> Icons.Default.Assignment
        stepNumber == 3 -> Icons.Default.AccountBalance
        else -> Icons.Default.Work
    }
}

/**
 * Provides a dynamic gradient palette progressing from Indigo -> Cyan -> Purple -> Green as steps advance.
 */
private fun getGradientsForStep(stepNumber: Int, totalSteps: Int): List<Color> {
    val progress = if (totalSteps > 1) stepNumber.toFloat() / totalSteps.toFloat() else 0.5f
    return when {
        progress <= 0.35f -> listOf(EduBluePrimary, EduCyanTertiary)
        progress <= 0.70f -> listOf(EduCyanTertiary, EduPurpleSecondary)
        else -> listOf(EduPurpleSecondary, SuccessGreen)
    }
}
