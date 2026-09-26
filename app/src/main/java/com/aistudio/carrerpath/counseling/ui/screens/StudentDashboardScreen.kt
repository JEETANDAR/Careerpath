package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.aistudio.carrerpath.counseling.data.model.AdmissionFormEntity
import com.aistudio.carrerpath.counseling.data.model.CollegeEntity
import com.aistudio.carrerpath.counseling.data.model.CounsellingBookingEntity
import com.aistudio.carrerpath.counseling.data.model.PsychometricAssessmentEntity
import com.aistudio.carrerpath.counseling.data.model.StudentProfile
import com.aistudio.carrerpath.counseling.data.model.capitalizeWords
import com.aistudio.carrerpath.counseling.ui.components.CollegeDetailDialog
import com.aistudio.carrerpath.counseling.ui.theme.*
import com.aistudio.carrerpath.counseling.ui.viewmodel.AppNavTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    profile: StudentProfile,
    appliedForms: List<AdmissionFormEntity>,
    counsellingBookings: List<CounsellingBookingEntity> = emptyList(),
    savedColleges: List<CollegeEntity>,
    assessment: PsychometricAssessmentEntity? = null,
    onSelectCollege: (CollegeEntity) -> Unit,
    onNavigateTo: (AppNavTab) -> Unit,
    onOpenAssessmentReport: () -> Unit = {},
    onTakeAssessment: () -> Unit = {},
    currentLanguage: String = "en",
    onLanguageChange: (String) -> Unit = {},
    onUpdateProfile: (name: String, phone: String, city: String) -> Unit = { _, _, _ -> },
    onOpenAdmissionForm: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    isDeletingAccount: Boolean = false
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Bookings, 1: Applied, 2: Saved, 3: Settings
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedCollegeDetail by remember { mutableStateOf<CollegeEntity?>(null) }

    var isEditingProfile by remember { mutableStateOf(false) }
    var editName by remember(profile.name) { mutableStateOf(profile.name) }
    var editPhone by remember(profile.phone) { mutableStateOf(profile.phone) }
    var editCity by remember(profile.city, profile.preferredCity) {
        mutableStateOf((if (profile.city.isNotBlank()) profile.city else profile.preferredCity).ifBlank { "Bangalore" })
    }

    val isKn = currentLanguage == "kn"
    val tabs = listOf(
        if (isKn) "ಬುಕಿಂಗ್‌ಗಳು (${counsellingBookings.size})" else "Bookings (${counsellingBookings.size})",
        if (isKn) "ಅರ್ಜಿಗಳು (${appliedForms.size})" else "Applied (${appliedForms.size})",
        if (isKn) "ಉಳಿಸಲಾಗಿದೆ (${savedColleges.size})" else "Saved (${savedColleges.size})",
        if (isKn) "ಪ್ರೊಫೈಲ್ / ಸೆಟ್ಟಿಂಗ್ಸ್" else "Profile & Settings"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Card
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isKn) "ವಿದ್ಯಾರ್ಥಿ ಪೋರ್ಟಲ್" else "STUDENT PORTAL",
                            color = EduBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = profile.name.ifBlank { if (isKn) "ವಿದ್ಯಾರ್ಥಿ" else "Student" },
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Quick Language Switch Chip
                    AssistChip(
                        onClick = { onLanguageChange(if (isKn) "en" else "kn") },
                        label = { Text(if (isKn) "English" else "ಕನ್ನಡ", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = EduBlueContainer)
                    )
                }
            }
        }

        // Tab Selector Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EduBluePrimary,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> {
                // Booked Counselling Sessions
                if (counsellingBookings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(56.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                if (isKn) "ಇನ್ನೂ ಯಾವುದೇ ಕೌನ್ಸೆಲಿಂಗ್ ಸೆಷನ್‌ಗಳನ್ನು ಬುಕ್ ಮಾಡಲಾಗಿಲ್ಲ." else "No counselling sessions booked yet.",
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTo(AppNavTab.COURSES) },
                                colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary)
                            ) {
                                Text(if (isKn) "ಕೋರ್ಸ್‌ಗಳನ್ನು ಹುಡುಕಿ & ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್ ಪಡೆಯಿರಿ" else "Explore Courses & Book Free Session")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(counsellingBookings) { booking ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, EduCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "REF #COUNS-2026-${booking.id}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EduPurpleSecondary
                                        )

                                        Surface(
                                            color = OpenGreenContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (isKn && booking.status == "Confirmed") "ಖಚಿತಪಡಿಸಲಾಗಿದೆ" else booking.status,
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "${if (isKn) "ಉದ್ದೇಶಿತ ಕೋರ್ಸ್: " else "Target Course: "}${booking.targetCourse}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${if (isKn) "ಆಯ್ದ ಕೌನ್ಸಿಲರ್: " else "Counsellor: "}${booking.counsellorName}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EduBluePrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${booking.bookingDate} (${booking.timeSlot})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (booking.feeAmount == 0.0) (if (isKn) "ಉಚಿತ 1-ಆನ್-1 ಮಾರ್ಗದರ್ಶನ (₹0)" else "FREE 1-on-1 Session (₹0)") else "₹${booking.feeAmount.toInt()}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EduBluePrimary
                                            )
                                            Text(
                                                text = if (isKn && booking.paymentStatus == "Confirmed") "ಪಾವತಿ ಪೂರ್ಣಗೊಂಡಿದೆ" else booking.paymentStatus,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Applied Colleges & Courses
                if (appliedForms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(56.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                if (isKn) "ಇನ್ನೂ ಯಾವುದೇ ಪ್ರವೇಶ ಅರ್ಜಿಗಳನ್ನು ಸಲ್ಲಿಸಲಾಗಿಲ್ಲ." else "No admission applications submitted yet.",
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { onNavigateTo(AppNavTab.COLLEGES) }) {
                                Text(if (isKn) "ಕಾಲೇಜುಗಳನ್ನು ಪರಿಶೀಲಿಸಿ & ಅರ್ಜಿ ಸಲ್ಲಿಸಿ" else "Browse Colleges & Apply")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(appliedForms) { form ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Ref #EDU-2026-${form.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                                        val (statusText, statusColor, containerColor) = when (form.status) {
                                            "Accepted" -> Triple(if (isKn) "ಸಮ್ಮತಿಸಲಾಗಿದೆ" else "Accepted", SuccessGreen, OpenGreenContainer)
                                            "Shortlisted" -> Triple(if (isKn) "ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ" else "Shortlisted", EduBluePrimary, EduBlueContainer)
                                            "Under Review" -> Triple(if (isKn) "ಪರಿಶೀಲನೆಯಲ್ಲಿದೆ" else "Under Review", WarningAmber, RankBadgeGoldContainer)
                                            else -> Triple(if (isKn) "ಸಲ್ಲಿಕೆಯಾಗಿದೆ" else form.status, EduBluePrimary, EduBlueContainer)
                                        }

                                        Surface(color = containerColor, shape = RoundedCornerShape(8.dp)) {
                                            Text(statusText, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = form.preferredCollege.ifEmpty { if (isKn) "ಸಾಮಾನ್ಯ ಪ್ರವೇಶ ಅರ್ಜಿ" else "General Admission Application" },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${if (isKn) "ಕೋರ್ಸ್: " else "Course: "}${form.preferredCourse}",
                                        fontSize = 13.sp,
                                        color = EduBluePrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (form.city.isNotBlank() || form.qualification.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "${form.city} • ${form.qualification} ${if (form.percentageMark > 0.0) "(${form.percentageMark}%)" else ""}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Saved Colleges
                if (savedColleges.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(56.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                if (isKn) "ಇನ್ನೂ ಯಾವುದೇ ಕಾಲೇಜುಗಳನ್ನು ಉಳಿಸಲಾಗಿಲ್ಲ." else "No saved colleges yet.",
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedColleges) { college ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCollegeDetail = college
                                        onSelectCollege(college)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val imgUrl = college.imageUrl.ifBlank { college.heroBannerUrl }
                                    if (imgUrl.isNotBlank()) {
                                        Surface(
                                            color = Color.White,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            shadowElevation = 2.dp,
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(imgUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = college.name,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp)
                                            )
                                        }
                                    } else {
                                        Icon(Icons.Default.School, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(32.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(college.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(college.location, fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Profile & Settings
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isKn) "ವಿದ್ಯಾರ್ಥಿ ವಿವರಗಳು" else "Student Profile Details",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = EduBluePrimary
                                    )
                                    IconButton(
                                        onClick = { isEditingProfile = !isEditingProfile },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isEditingProfile) Icons.Default.Close else Icons.Default.Edit,
                                            contentDescription = if (isEditingProfile) "Close Edit" else "Edit Details",
                                            tint = EduBluePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                if (isEditingProfile) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = editName,
                                        onValueChange = { editName = it.capitalizeWords() },
                                        label = { Text(if (isKn) "ಪೂರ್ಣ ಹೆಸರು" else "Full Name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = editPhone,
                                        onValueChange = { editPhone = it },
                                        label = { Text(if (isKn) "ಮೊಬೈಲ್ ಸಂಖ್ಯೆ" else "Phone Number") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = editCity,
                                        onValueChange = { editCity = it.capitalizeWords() },
                                        label = { Text(if (isKn) "ನಗರ" else "City") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            onUpdateProfile(editName, editPhone, editCity)
                                            isEditingProfile = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isKn) "ವಿವರಗಳನ್ನು ಉಳಿಸಿ" else "Save Details", fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val displayCity = (if (profile.city.isNotBlank()) profile.city else profile.preferredCity).ifBlank { "Bangalore" }
                                    DetailRow(label = if (isKn) "ಹೆಸರು" else "Full Name", value = profile.name.ifBlank { "Student" })
                                    DetailRow(label = if (isKn) "ಮೊಬೈಲ್ ಸಂಖ್ಯೆ" else "Phone Number", value = profile.phone.ifBlank { "N/A" })
                                    DetailRow(label = if (isKn) "ನಗರ" else "City", value = displayCity)
                                }
                            }
                        }
                    }

                    // Psychometric Career Assessment Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Psychology,
                                                contentDescription = null,
                                                tint = EduBluePrimary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isKn) "AI ವೃತ್ತಿ ಸಾಮರ್ಥ್ಯ ಮೌಲ್ಯಮಾಪನ" else "AI Psychometric Assessment",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = if (assessment != null) "${if (isKn) "ಪ್ರವೃತ್ತಿ" else "Archetype"}: ${assessment.archetype}" else if (isKn) "ನಿಮ್ಮ ವೃತ್ತಿ ಮಾರ್ಗವನ್ನು ಅನ್ವೇಷಿಸಲು 3 ನಿಮಿಷದ ಪರೀಕ್ಷೆ ತೆಗೆದುಕೊಳ್ಳಿ" else "Take a 3-minute test to find your career match",
                                            fontSize = 12.sp,
                                            color = if (assessment != null) EduBluePrimary else Color.Gray,
                                            fontWeight = if (assessment != null) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                if (assessment != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = onOpenAssessmentReport,
                                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (isKn) "ವರದಿ ನೋಡಿ" else "View Report", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }

                                        OutlinedButton(
                                            onClick = onTakeAssessment,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (isKn) "ಮರುಪರೀಕ್ಷೆ" else "Retake Test", fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = onTakeAssessment,
                                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isKn) "ಪರೀಕ್ಷೆ ಪ್ರಾರಂಭಿಸಿ" else "Take Career Assessment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isKn) "ಭಾಷೆಯ ಆದ್ಯತೆಗಳು" else "App Language Preferences",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = EduBluePrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onLanguageChange("en") },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (!isKn) EduBlueContainer else Color.Transparent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("English", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onLanguageChange("kn") },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isKn) EduBlueContainer else Color.Transparent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("ಕನ್ನಡ", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Sign Out Button
                        Button(
                            onClick = onLogoutClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EduBluePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("dashboard_logout_button")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isKn) "ನಿರ್ಗಮಿಸಿ (Sign Out)" else "Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Delete Account Button (Styled similarly to Sign Out button with a destructive red color)
                        Button(
                            onClick = { showDeleteConfirmation = true },
                            enabled = !isDeletingAccount,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("dashboard_delete_account_button")
                        ) {
                            if (isDeletingAccount) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Deleting Account...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            } else {
                                Icon(Icons.Default.DeleteForever, contentDescription = "Delete Account")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isKn) "ಖಾತೆ ಅಳಿಸಿ (Delete Account)" else "Delete Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(140.dp))
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isDeletingAccount) showDeleteConfirmation = false },
            icon = {
                Icon(
                    Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isKn) "ಖಾತೆಯನ್ನು ಶಾಶ್ವತವಾಗಿ ಅಳಿಸುವುದೇ?" else "Delete Account Permanently?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isKn) "ನಿಮ್ಮ CareerPath ಖಾತೆಯನ್ನು ಖಚಿತವಾಗಿ ಅಳಿಸಲು ನೀವು ಬಯಸುವಿರಾ?" else "Are you sure you want to delete your CareerPath account?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isKn) "ಈ ಕ್ರಿಯೆಯು ಶಾಶ್ವತವಾಗಿದೆ. ನಿಮ್ಮ ಪ್ರೊಫೈಲ್, ಬುಕಿಂಗ್‌ಗಳು, ಅರ್ಜಿಗಳು ಮತ್ತು ಮೌಲ್ಯಮಾಪನ ವಿವರಗಳನ್ನು ಶಾಶ್ವತವಾಗಿ ಅಳಿಸಲಾಗುತ್ತದೆ." else "This action is permanent and cannot be undone. All your personal data—including profile info, counselling bookings, admission applications, and assessment records—will be permanently erased.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteAccountClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isDeletingAccount
                ) {
                    Text(if (isKn) "ಖಾತೆ ಅಳಿಸಿ" else "Delete Account", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !isDeletingAccount
                ) {
                    Text(if (isKn) "ರದ್ದುಮಾಡಿ" else "Cancel")
                }
            }
        )
    }

    selectedCollegeDetail?.let { item ->
        CollegeDetailDialog(
            college = item,
            currentLanguage = currentLanguage,
            onDismiss = { selectedCollegeDetail = null },
            onOpenAdmissionForm = { name ->
                selectedCollegeDetail = null
                onOpenAdmissionForm(name)
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
