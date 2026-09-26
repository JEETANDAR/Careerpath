package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.aistudio.carrerpath.counseling.data.model.*
import com.aistudio.carrerpath.counseling.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatAdminTimestamp(timestamp: Long): String {
    if (timestamp <= 0) return "Date unavailable"
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
    sdf.timeZone = java.util.TimeZone.getDefault()
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    careers: List<CareerEntity> = emptyList(),
    colleges: List<CollegeEntity>,
    courses: List<CourseEntity>,
    students: List<StudentProfileEntity> = emptyList(),
    counsellingBookings: List<CounsellingBookingEntity> = emptyList(),
    admissionForms: List<AdmissionFormEntity>,
    onUpdateFormStatus: (formId: Int, newStatus: String) -> Unit,
    onUpdateCounsellingStatus: (bookingId: Int, newStatus: String) -> Unit = { _, _ -> },
    onSaveCareer: (CareerEntity) -> Unit = {},
    onDeleteCareer: (id: Int) -> Unit = {},
    onSaveCollege: (CollegeEntity) -> Unit,
    onDeleteCollege: (id: Int) -> Unit,
    onSaveCourse: (CourseEntity) -> Unit,
    onDeleteCourse: (id: Int) -> Unit,
    onBroadcastNotification: (title: String, message: String, category: String) -> Unit,
    onSyncCloud: () -> Unit = {},
    onReseedCareers: () -> Unit = {},
    onReseedCourses: () -> Unit = {},
    onReseedColleges: () -> Unit = {},
    isSyncing: Boolean = false,
    onRefreshStudents: () -> Unit = {},
    isRefreshingStudents: Boolean = false,
    onDiagnoseFirestore: ((Boolean, String) -> Unit) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    var showFirestoreDiagDialog by remember { mutableStateOf(false) }
    var diagReportText by remember { mutableStateOf("") }
    var isDiagnosing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onSyncCloud()
        onRefreshStudents()
    }

    var adminTab by remember { mutableIntStateOf(0) }
    val adminTabs = listOf(
        "Dashboard",
        "Applications (${admissionForms.size})",
        "Counselling (${counsellingBookings.size})",
        "Catalog",
        "Students (${students.size})",
        "Broadcast Alert"
    )

    // Catalog Sub-Tab (0: Careers, 1: Courses, 2: Colleges)
    var catalogSubTab by remember { mutableIntStateOf(0) }

    // Search States
    var appSearchQuery by remember { mutableStateOf("") }
    var counsSearchQuery by remember { mutableStateOf("") }
    var userSearchQuery by remember { mutableStateOf("") }
    var careerSearchQuery by remember { mutableStateOf("") }
    var courseSearchQuery by remember { mutableStateOf("") }
    var collegeSearchQuery by remember { mutableStateOf("") }

    // Dialog States
    var showCareerDialog by remember { mutableStateOf(false) }
    var editingCareer by remember { mutableStateOf<CareerEntity?>(null) }
    var carTitle by remember { mutableStateOf("") }
    var carTitleKn by remember { mutableStateOf("") }
    var carCategory by remember { mutableStateOf("Medical") }
    var carTagline by remember { mutableStateOf("") }
    var carDescription by remember { mutableStateOf("") }
    var carSalaryAvg by remember { mutableStateOf("₹8 - 15 LPA") }
    var carDuration by remember { mutableStateOf("5.5 Years") }
    var carImage by remember { mutableStateOf("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800") }
    var carOverview by remember { mutableStateOf("") }
    var carEligibility by remember { mutableStateOf("") }

    var showCourseDialog by remember { mutableStateOf(false) }
    var editingCourse by remember { mutableStateOf<CourseEntity?>(null) }
    var courseCode by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var courseCategory by remember { mutableStateOf("Medical") }
    var courseDuration by remember { mutableStateOf("4 Years") }
    var courseEligibility by remember { mutableStateOf("12th PCB/PCM 50%") }
    var courseAvgFees by remember { mutableStateOf("3.5") }
    var courseAvgPackage by remember { mutableStateOf("8.0") }
    var courseCareerScope by remember { mutableStateOf("Specialist, Consultant, Researcher") }
    var courseDescription by remember { mutableStateOf("") }
    var courseImageUrl by remember { mutableStateOf("") }

    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var customCategoriesList by remember { mutableStateOf(listOf("Medical", "Engineering", "Commerce & Management", "Arts & Law", "Aviation & Paramedical", "Design & Media", "Science & Research", "General")) }
    var newCategoryInput by remember { mutableStateOf("") }

    var showCollegeDialog by remember { mutableStateOf(false) }
    var editingCollege by remember { mutableStateOf<CollegeEntity?>(null) }
    var colName by remember { mutableStateOf("") }
    var colLocation by remember { mutableStateOf("") }
    var colCourses by remember { mutableStateOf("") }
    var colMinFees by remember { mutableStateOf("2.5") }
    var colMaxFees by remember { mutableStateOf("10.0") }
    var colRanking by remember { mutableStateOf("10") }
    var colImageUrl by remember { mutableStateOf("") }
    var colHeroBannerUrl by remember { mutableStateOf("") }
    var colOverview by remember { mutableStateOf("") }

    // Broadcast State
    var notifyTitle by remember { mutableStateOf("") }
    var notifyMessage by remember { mutableStateOf("") }
    var notifyCategory by remember { mutableStateOf("Admission Open") }
    var broadcastSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Filtered lists
    val filteredApplications = remember(appSearchQuery, admissionForms) {
        val list = admissionForms.sortedByDescending { it.timestamp }
        if (appSearchQuery.isBlank()) list
        else list.filter {
            it.studentName.contains(appSearchQuery, ignoreCase = true) ||
            it.phone.contains(appSearchQuery, ignoreCase = true) ||
            it.preferredCollege.contains(appSearchQuery, ignoreCase = true) ||
            it.preferredCourse.contains(appSearchQuery, ignoreCase = true)
        }
    }

    val filteredCounselling = remember(counsSearchQuery, counsellingBookings) {
        val list = counsellingBookings.sortedByDescending { it.timestamp }
        if (counsSearchQuery.isBlank()) list
        else list.filter {
            it.studentName.contains(counsSearchQuery, ignoreCase = true) ||
            it.studentPhone.contains(counsSearchQuery, ignoreCase = true) ||
            it.targetCourse.contains(counsSearchQuery, ignoreCase = true)
        }
    }

    val filteredStudents = remember(userSearchQuery, students) {
        if (userSearchQuery.isBlank()) students
        else students.filter {
            it.name.contains(userSearchQuery, ignoreCase = true) ||
            it.phone.contains(userSearchQuery, ignoreCase = true) ||
            it.email.contains(userSearchQuery, ignoreCase = true) ||
            it.studentClass.contains(userSearchQuery, ignoreCase = true)
        }
    }

    var selectedStudentDetails by remember { mutableStateOf<StudentProfileEntity?>(null) }

    val filteredCareersAdmin = remember(careerSearchQuery, careers) {
        if (careerSearchQuery.isBlank()) careers
        else careers.filter { it.title.contains(careerSearchQuery, ignoreCase = true) }
    }

    val filteredCoursesAdmin = remember(courseSearchQuery, courses) {
        if (courseSearchQuery.isBlank()) courses
        else courses.filter { it.name.contains(courseSearchQuery, ignoreCase = true) || it.code.contains(courseSearchQuery, ignoreCase = true) }
    }

    val filteredCollegesAdmin = remember(collegeSearchQuery, colleges) {
        if (collegeSearchQuery.isBlank()) colleges
        else colleges.filter { it.name.contains(collegeSearchQuery, ignoreCase = true) || it.location.contains(collegeSearchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Executive Header Console
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ADMIN EXECUTIVE DASHBOARD",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "CareerPath Admin HQ",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                isDiagnosing = true
                                onDiagnoseFirestore { _, report ->
                                    isDiagnosing = false
                                    diagReportText = report
                                    showFirestoreDiagDialog = true
                                }
                            },
                            enabled = !isDiagnosing,
                            modifier = Modifier
                                .background(Color(0xFF1E293B), CircleShape)
                                .size(36.dp)
                        ) {
                            if (isDiagnosing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFFFACC15),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.CloudQueue, contentDescription = "Firestore Diagnostics", tint = Color(0xFFFACC15), modifier = Modifier.size(20.dp))
                            }
                        }

                        IconButton(
                            onClick = { if (!isSyncing) onSyncCloud() },
                            enabled = !isSyncing,
                            modifier = Modifier
                                .background(Color(0xFF1E293B), CircleShape)
                                .size(36.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF38BDF8),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = "Sync Cloud", tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            }
                        }

                        OutlinedButton(
                            onClick = onLogout,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Logout", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricPill("Applications", admissionForms.size.toString(), Color(0xFF38BDF8), Modifier.weight(1f))
                    MetricPill("Counselling", counsellingBookings.size.toString(), Color(0xFF4ADE80), Modifier.weight(1f))
                    MetricPill("Students", students.size.toString(), Color(0xFFFACC15), Modifier.weight(1f))
                    MetricPill("Colleges", colleges.size.toString(), Color(0xFFF472B6), Modifier.weight(1f))
                }
            }
        }

        // Navigation Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = adminTab,
            containerColor = Color(0xFF1E293B),
            contentColor = Color.White,
            edgePadding = 12.dp
        ) {
            adminTabs.forEachIndexed { idx, title ->
                Tab(
                    selected = adminTab == idx,
                    onClick = { adminTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (adminTab == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (adminTab == idx) Color(0xFF38BDF8) else Color.LightGray,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Main Tab Content Area
        Box(modifier = Modifier.weight(1f)) {
            when (adminTab) {
                0 -> {
                    // Dashboard Overview Tab
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Recent Admission Applications Received",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        if (admissionForms.isEmpty()) {
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "No admission applications received yet.",
                                        modifier = Modifier.padding(16.dp),
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        } else {
                            items(admissionForms.sortedByDescending { it.timestamp }.take(5)) { form ->
                                ApplicationAdminCard(form = form, onUpdateFormStatus = onUpdateFormStatus)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Recent Counselling Requests Received",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        if (counsellingBookings.isEmpty()) {
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "No counselling session bookings received yet.",
                                        modifier = Modifier.padding(16.dp),
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        } else {
                            items(counsellingBookings.sortedByDescending { it.timestamp }.take(5)) { booking ->
                                CounsellingAdminCard(booking = booking, onUpdateStatus = onUpdateCounsellingStatus)
                            }
                        }
                    }
                }

                1 -> {
                    // All Admission Applications Tab
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        OutlinedTextField(
                            value = appSearchQuery,
                            onValueChange = { appSearchQuery = it },
                            placeholder = { Text("Search applications by student, phone, college...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EduBluePrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Showing ${filteredApplications.size} Applications (Newest First)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(filteredApplications) { form ->
                                ApplicationAdminCard(form = form, onUpdateFormStatus = onUpdateFormStatus)
                            }
                        }
                    }
                }

                2 -> {
                    // All Counselling Bookings Tab
                    var selectedCounsFilter by remember { mutableStateOf("All") }
                    var selectedCounsellorFilter by remember { mutableStateOf("All") }

                    val careerCount = remember(counsellingBookings) {
                        counsellingBookings.count {
                            it.bookingType.contains("career", ignoreCase = true) ||
                            it.targetCourse.contains("doctor", ignoreCase = true) ||
                            it.targetCourse.contains("engineer", ignoreCase = true) ||
                            it.targetCourse.contains("pilot", ignoreCase = true) ||
                            it.targetCourse.contains("career", ignoreCase = true)
                        }
                    }
                    val collegeCount = remember(counsellingBookings) {
                        counsellingBookings.count {
                            it.bookingType.contains("college", ignoreCase = true) ||
                            it.targetCourse.contains("college", ignoreCase = true) ||
                            it.targetCourse.contains("university", ignoreCase = true)
                        }
                    }
                    val courseCount = remember(counsellingBookings) {
                        counsellingBookings.size - careerCount - collegeCount
                    }

                    val displayingCounselling = remember(filteredCounselling, selectedCounsFilter, selectedCounsellorFilter) {
                        filteredCounselling.filter { b ->
                            val t = b.bookingType.lowercase()
                            val tc = b.targetCourse.lowercase()
                            val categoryMatch = when (selectedCounsFilter) {
                                "Career" -> t.contains("career") || tc.contains("career") || tc.contains("doctor") || tc.contains("engineer") || tc.contains("pilot") || tc.contains("lawyer")
                                "College" -> t.contains("college") || tc.contains("college") || tc.contains("university")
                                "Course" -> !t.contains("career") && !t.contains("college")
                                else -> true
                            }
                            val counsellorMatch = if (selectedCounsellorFilter == "All") true else b.counsellorName.equals(selectedCounsellorFilter, ignoreCase = true)
                            categoryMatch && counsellorMatch
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        OutlinedTextField(
                            value = counsSearchQuery,
                            onValueChange = { counsSearchQuery = it },
                            placeholder = { Text("Search counselling bookings by student, phone...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EduBluePrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                "All" to "All (${counsellingBookings.size})",
                                "Career" to "Career (${careerCount})",
                                "Course" to "Courses (${courseCount})",
                                "College" to "Colleges (${collegeCount})"
                            ).forEach { (key, label) ->
                                val isSel = selectedCounsFilter == key
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedCounsFilter = key },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EduBluePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Counsellor Filter Chips Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Counsellor:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            listOf("All", "Nimi", "Janardhan", "Xavier", "Aishwarya").forEach { counsellor ->
                                val isSel = selectedCounsellorFilter.equals(counsellor, ignoreCase = true)
                                val count = if (counsellor == "All") {
                                    counsellingBookings.size
                                } else {
                                    counsellingBookings.count { it.counsellorName.equals(counsellor, ignoreCase = true) }
                                }

                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedCounsellorFilter = counsellor },
                                    label = {
                                        Text(
                                            text = if (counsellor == "All") "All ($count)" else "$counsellor ($count)",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    leadingIcon = {
                                        if (counsellor != "All") {
                                            Icon(
                                                imageVector = Icons.Default.SupportAgent,
                                                contentDescription = null,
                                                tint = if (isSel) Color.White else EduPurpleSecondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EduPurpleSecondary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Showing ${displayingCounselling.size} Counselling Bookings",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduBluePrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (displayingCounselling.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No $selectedCounsFilter counselling bookings found.",
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(displayingCounselling) { booking ->
                                    CounsellingAdminCard(booking = booking, onUpdateStatus = onUpdateCounsellingStatus)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Data Catalog (Careers, Courses, Colleges)
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        TabRow(
                            selectedTabIndex = catalogSubTab,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Tab(
                                selected = catalogSubTab == 0,
                                onClick = { catalogSubTab = 0 },
                                text = { Text("Careers (${careers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = catalogSubTab == 1,
                                onClick = { catalogSubTab = 1 },
                                text = { Text("Courses (${courses.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = catalogSubTab == 2,
                                onClick = { catalogSubTab = 2 },
                                text = { Text("Colleges (${colleges.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (catalogSubTab) {
                            0 -> { // Manage Careers
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = careerSearchQuery,
                                        onValueChange = { careerSearchQuery = it },
                                        placeholder = { Text("Search careers...") },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    )

                                    OutlinedButton(
                                        onClick = { onReseedCareers() },
                                        enabled = !isSyncing,
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reseed", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            editingCareer = null
                                            carTitle = ""
                                            carTitleKn = ""
                                            carCategory = "Engineering"
                                            carTagline = ""
                                            carDescription = ""
                                            carSalaryAvg = "₹8 - 15 LPA"
                                            carDuration = "4 Years"
                                            carImage = "https://images.pexels.com/photos/5452292/pexels-photo-5452292.jpeg?auto=compress&cs=tinysrgb&w=1200"
                                            carOverview = ""
                                            carEligibility = "12th PCM / PCB 60%"
                                            showCareerDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add", fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(filteredCareersAdmin) { car ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AsyncImage(
                                                    model = car.image,
                                                    contentDescription = car.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(car.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("${car.category} • Avg ${car.salaryAverage}", fontSize = 12.sp, color = EduBluePrimary)
                                                }

                                                IconButton(onClick = {
                                                    editingCareer = car
                                                    carTitle = car.title
                                                    carTitleKn = car.titleKn
                                                    carCategory = car.category
                                                    carTagline = car.tagline
                                                    carDescription = car.description
                                                    carSalaryAvg = car.salaryAverage
                                                    carDuration = car.duration
                                                    carImage = car.image
                                                    carOverview = car.overview
                                                    carEligibility = car.eligibility
                                                    showCareerDialog = true
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EduBluePrimary)
                                                }

                                                IconButton(onClick = { onDeleteCareer(car.id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ClosedRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            1 -> { // Manage Courses
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = courseSearchQuery,
                                        onValueChange = { courseSearchQuery = it },
                                        placeholder = { Text("Search courses...") },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = onReseedCourses,
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0284C7)),
                                            border = BorderStroke(1.dp, Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reseed 200", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                editingCourse = null
                                                courseCode = "BTECH-AI"
                                                courseName = ""
                                                courseCategory = "Engineering"
                                                courseDuration = "4 Years"
                                                courseEligibility = "12th PCM 60%"
                                                courseAvgFees = "4.5"
                                                courseAvgPackage = "10.0"
                                                courseCareerScope = "AI Engineer, ML Specialist"
                                                courseDescription = ""
                                                courseImageUrl = ""
                                                showCourseDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Add", fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(filteredCoursesAdmin) { course ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (course.imageUrl.isNotBlank()) {
                                                    AsyncImage(
                                                        model = course.imageUrl,
                                                        contentDescription = course.name,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(44.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(course.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("${course.code} • ${course.category}", fontSize = 12.sp, color = EduBluePrimary)
                                                }

                                                IconButton(onClick = {
                                                    editingCourse = course
                                                    courseCode = course.code
                                                    courseName = course.name
                                                    courseCategory = course.category
                                                    courseDuration = course.durationYears
                                                    courseEligibility = course.eligibility
                                                    courseAvgFees = course.avgFeesLakhs.toString()
                                                    courseAvgPackage = course.avgPackageLakhs.toString()
                                                    courseCareerScope = course.careerScope
                                                    courseDescription = course.description
                                                    courseImageUrl = course.imageUrl
                                                    showCourseDialog = true
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EduBluePrimary)
                                                }

                                                IconButton(onClick = { onDeleteCourse(course.id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ClosedRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            2 -> { // Manage Colleges
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = collegeSearchQuery,
                                        onValueChange = { collegeSearchQuery = it },
                                        placeholder = { Text("Search colleges...") },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    )

                                    OutlinedButton(
                                        onClick = { onReseedColleges() },
                                        enabled = !isSyncing,
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reseed", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            editingCollege = null
                                            colName = ""
                                            colLocation = "Bangalore, Karnataka"
                                            colCourses = "MBBS, B.Tech, MBA"
                                            colMinFees = "2.5"
                                            colMaxFees = "10.0"
                                            colRanking = "10"
                                            colImageUrl = "https://images.unsplash.com/photo-1562774053-701939374585?w=800"
                                            colHeroBannerUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=1200"
                                            colOverview = "Premier academic institution."
                                            showCollegeDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add", fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(filteredCollegesAdmin) { college ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AsyncImage(
                                                    model = college.imageUrl,
                                                    contentDescription = college.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(college.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("${college.location} • Rank #${college.ranking}", fontSize = 12.sp, color = EduBluePrimary)
                                                }

                                                IconButton(onClick = {
                                                    editingCollege = college
                                                    colName = college.name
                                                    colLocation = college.location
                                                    colCourses = college.coursesOffered
                                                    colMinFees = college.minFeesLakhs.toString()
                                                    colMaxFees = college.maxFeesLakhs.toString()
                                                    colRanking = college.ranking.toString()
                                                    colImageUrl = college.imageUrl
                                                    colHeroBannerUrl = college.heroBannerUrl
                                                    colOverview = college.overview
                                                    showCollegeDialog = true
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EduBluePrimary)
                                                }

                                                IconButton(onClick = { onDeleteCollege(college.id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ClosedRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // Registered Students Tab - Fully Firestore backed & device independent
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        // Header bar with Live Firestore sync indicator and manual Refresh button
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Text(
                                        text = "Cloud Firestore Live (${students.size})",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(
                                        onClick = {
                                            isDiagnosing = true
                                            onDiagnoseFirestore { _, report ->
                                                isDiagnosing = false
                                                diagReportText = report
                                                showFirestoreDiagDialog = true
                                            }
                                        },
                                        enabled = !isDiagnosing,
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CloudQueue,
                                            contentDescription = "Test Cloud Connection",
                                            modifier = Modifier.size(16.dp),
                                            tint = EduBluePrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isDiagnosing) "Testing..." else "Test Cloud", fontSize = 12.sp, color = EduBluePrimary)
                                    }

                                    TextButton(
                                        onClick = { onRefreshStudents() },
                                        enabled = !isRefreshingStudents,
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        if (isRefreshingStudents) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = EduBluePrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Syncing...", fontSize = 12.sp, color = EduBluePrimary)
                                        } else {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = "Refresh from Firestore",
                                                modifier = Modifier.size(16.dp),
                                                tint = EduBluePrimary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Sync Live", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EduBluePrimary)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = userSearchQuery,
                            onValueChange = { userSearchQuery = it },
                            placeholder = { Text("Search students by name, phone, class...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EduBluePrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (filteredStudents.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.CloudSync,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = EduBluePrimary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "No Registered Students Found",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "When students register or sign up on ANY device, they appear in Firestore and sync here automatically.",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { onRefreshStudents() },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Query Firestore Collection")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(filteredStudents) { std ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedStudentDetails = std }
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(std.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    if (std.uid.isNotBlank()) {
                                                        Text(
                                                            "UID: ${std.uid.take(12)}...",
                                                            fontSize = 10.sp,
                                                            color = Color.Gray,
                                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                        )
                                                    }
                                                }
                                                Surface(
                                                    color = Color(0xFFE0F2FE),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = std.studentClass.ifBlank { "12th Standard" },
                                                        color = Color(0xFF0369A1),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("📞 Phone: ${std.phone}", fontSize = 12.sp, color = Color.DarkGray)
                                            if (std.email.isNotBlank()) {
                                                Text("✉️ Email: ${std.email}", fontSize = 12.sp, color = Color.Gray)
                                            }
                                            Text("🏫 Institution: ${std.institution}", fontSize = 12.sp, color = Color.Gray)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Text("👤 Gender: ${std.gender}", fontSize = 12.sp, color = Color.Gray)
                                                if (std.percentageMark > 0.0) {
                                                    Text("📊 Score: ${std.percentageMark}%", fontSize = 12.sp, color = EduBluePrimary, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                            Text("📍 Location: ${std.city}, ${std.state} (${std.country})", fontSize = 12.sp, color = Color.Gray)
                                            if (std.createdAt > 0) {
                                                Text(
                                                    "🕒 Registered: ${formatAdminTimestamp(std.createdAt)}",
                                                    fontSize = 11.sp,
                                                    color = Color.LightGray
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = { selectedStudentDetails = std },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = EduBluePrimary)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Full Profile", fontSize = 11.sp, color = EduBluePrimary)
                                                }
                                                OutlinedButton(
                                                    onClick = {
                                                        try {
                                                            val cleanDigits = std.phone.filter { it.isDigit() }
                                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanDigits"))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {}
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF16A34A))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Call", fontSize = 11.sp, color = Color(0xFF16A34A))
                                                }
                                                OutlinedButton(
                                                    onClick = {
                                                        try {
                                                            val cleanDigits = std.phone.filter { it.isDigit() }
                                                            val waUrl = "https://api.whatsapp.com/send?phone=$cleanDigits&text=Hello%20${Uri.encode(std.name)}%2C%20CareerPath%20Counselling%20Support%20team%20here."
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {}
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0D9488))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("WhatsApp", fontSize = 11.sp, color = Color(0xFF0D9488))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // Broadcast Alert Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Broadcast Notification Alert", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Send real-time alerts to all student apps.", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = notifyTitle,
                            onValueChange = { notifyTitle = it },
                            label = { Text("Alert Title") },
                            placeholder = { Text("e.g. NEET UG Counselling Date Announced!") },
                            leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = EduBluePrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = notifyMessage,
                            onValueChange = { notifyMessage = it },
                            label = { Text("Notification Message") },
                            placeholder = { Text("e.g. Choice filling is now open until 10th August. Click to apply.") },
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (notifyTitle.isNotBlank() && notifyMessage.isNotBlank()) {
                                    onBroadcastNotification(notifyTitle.trim(), notifyMessage.trim(), notifyCategory)
                                    broadcastSuccessMessage = "Notification broadcasted successfully!"
                                    notifyTitle = ""
                                    notifyMessage = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Broadcast Now", fontWeight = FontWeight.Bold)
                        }

                        if (broadcastSuccessMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = broadcastSuccessMessage!!,
                                    color = Color(0xFF15803D),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Firestore Diagnostics Dialog
    if (showFirestoreDiagDialog) {
        AlertDialog(
            onDismissRequest = { showFirestoreDiagDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = EduBluePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cloud Diagnostics", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = diagReportText,
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFirestoreDiagDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary)
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Add / Edit Dialogs
    if (showCareerDialog) {
        AlertDialog(
            onDismissRequest = { showCareerDialog = false },
            title = { Text(if (editingCareer == null) "Add Career Path" else "Edit Career Path", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = carTitle, onValueChange = { carTitle = it }, label = { Text("Title (English) *") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = carCategory, onValueChange = { carCategory = it }, label = { Text("Category") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = carTagline, onValueChange = { carTagline = it }, label = { Text("Tagline") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = carSalaryAvg, onValueChange = { carSalaryAvg = it }, label = { Text("Avg Salary") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = carDuration, onValueChange = { carDuration = it }, label = { Text("Duration") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = carEligibility, onValueChange = { carEligibility = it }, label = { Text("Eligibility Criteria") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = carImage, onValueChange = { carImage = it }, label = { Text("Image / Banner URL") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = carOverview, onValueChange = { carOverview = it }, label = { Text("Career Overview") }, minLines = 2, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = carDescription, onValueChange = { carDescription = it }, label = { Text("Description & Scope") }, minLines = 2, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (carTitle.isNotBlank()) {
                            val entity = CareerEntity(
                                id = editingCareer?.id ?: 0,
                                title = carTitle.trim(),
                                titleKn = carTitleKn,
                                category = carCategory.trim().ifBlank { "General" },
                                tagline = carTagline.trim(),
                                description = carDescription.trim(),
                                salaryAverage = carSalaryAvg.trim(),
                                duration = carDuration.trim(),
                                image = carImage.ifBlank { "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800" },
                                overview = carOverview.trim(),
                                eligibility = carEligibility.trim()
                            )
                            onSaveCareer(entity)
                            showCareerDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Save Career") }
            },
            dismissButton = { OutlinedButton(onClick = { showCareerDialog = false }, shape = RoundedCornerShape(10.dp)) { Text("Cancel") } }
        )
    }

    if (showCourseDialog) {
        var categoryDropdownExpanded by remember { mutableStateOf(false) }
        val allAvailableCategories = (customCategoriesList + courses.map { it.category }).distinct()

        AlertDialog(
            onDismissRequest = { showCourseDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (editingCourse == null) "Add New Course" else "Edit Course Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = { showCourseDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Course Info & Category", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduBluePrimary)

                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("Course Code *") },
                        placeholder = { Text("e.g. BTECH-AI, MBBS, BHMS") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { courseName = it },
                        label = { Text("Course Full Name *") },
                        placeholder = { Text("e.g. Bachelor of Homeopathic Medicine & Surgery") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Selection Row (Dropdown + "+" New Category Button)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Category *", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = courseCategory,
                                    onValueChange = { courseCategory = it },
                                    label = { Text("Select or Type Category") },
                                    trailingIcon = {
                                        IconButton(onClick = { categoryDropdownExpanded = !categoryDropdownExpanded }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                DropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    allAvailableCategories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                courseCategory = cat
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledIconButton(
                                onClick = { showNewCategoryDialog = true },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = EduBluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Category", tint = Color.White)
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = courseDuration,
                            onValueChange = { courseDuration = it },
                            label = { Text("Duration *") },
                            placeholder = { Text("4 Years") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = courseEligibility,
                            onValueChange = { courseEligibility = it },
                            label = { Text("Eligibility *") },
                            placeholder = { Text("12th PCM 60%") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Fees & Salary Expectations", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduBluePrimary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = courseAvgFees,
                            onValueChange = { courseAvgFees = it },
                            label = { Text("Avg Fees (₹ L/yr)") },
                            placeholder = { Text("3.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = courseAvgPackage,
                            onValueChange = { courseAvgPackage = it },
                            label = { Text("Avg Salary (₹ L/yr)") },
                            placeholder = { Text("8.0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = courseCareerScope,
                        onValueChange = { courseCareerScope = it },
                        label = { Text("Career Opportunities & Benefits") },
                        placeholder = { Text("High Demand Scope, AI Engineer, ML Specialist...") },
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Course Description & Overview", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduBluePrimary)

                    OutlinedTextField(
                        value = courseDescription,
                        onValueChange = { courseDescription = it },
                        label = { Text("Overview & Detailed Curriculum") },
                        placeholder = { Text("Enter overview of course topics, core subjects, and scope...") },
                        minLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Course Image", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduBluePrimary)

                    OutlinedTextField(
                        value = courseImageUrl,
                        onValueChange = { courseImageUrl = it },
                        label = { Text("Course Image Link / URL") },
                        placeholder = { Text("https://images.unsplash.com/...") },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = EduBluePrimary) },
                        trailingIcon = {
                            if (courseImageUrl.isNotBlank()) {
                                IconButton(onClick = { courseImageUrl = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear URL")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Image Visual Preview
                    if (courseImageUrl.isNotBlank()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, EduBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            ) {
                                AsyncImage(
                                    model = courseImageUrl,
                                    contentDescription = "Course image live preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Image Preview",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Previewing link: ${courseImageUrl.take(60)}${if (courseImageUrl.length > 60) "..." else ""}",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ImageNotSupported,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "No image link set. Paste an image URL above to see live preview.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (courseCode.isNotBlank() && courseName.isNotBlank()) {
                            val c = CourseEntity(
                                id = editingCourse?.id ?: 0,
                                code = courseCode.trim(),
                                name = courseName.trim(),
                                category = courseCategory.trim().ifBlank { "General" },
                                durationYears = courseDuration.trim().ifBlank { "4 Years" },
                                eligibility = courseEligibility.trim().ifBlank { "12th Standard 50%" },
                                avgFeesLakhs = courseAvgFees.toDoubleOrNull() ?: 2.5,
                                avgPackageLakhs = courseAvgPackage.toDoubleOrNull() ?: 6.0,
                                careerScope = courseCareerScope.trim().ifBlank { "Professional Roles in Industry" },
                                description = courseDescription.trim().ifBlank { "Comprehensive course providing foundational and advanced industry knowledge." },
                                imageUrl = courseImageUrl.trim()
                            )
                            onSaveCourse(c)
                            showCourseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Course", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCourseDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            title = { Text("Add New Category", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = { newCategoryInput = it },
                    label = { Text("Category Name") },
                    placeholder = { Text("e.g. Paramedical, Cybersecurity") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            val trimmed = newCategoryInput.trim()
                            if (!customCategoriesList.contains(trimmed)) {
                                customCategoriesList = customCategoriesList + trimmed
                            }
                            courseCategory = trimmed
                            newCategoryInput = ""
                            showNewCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add & Select")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showNewCategoryDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCollegeDialog) {
        AlertDialog(
            onDismissRequest = { showCollegeDialog = false },
            title = { Text(if (editingCollege == null) "Add College" else "Edit College", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = colName, onValueChange = { colName = it }, label = { Text("College Name *") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = colLocation, onValueChange = { colLocation = it }, label = { Text("Location (City, State) *") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = colCourses, onValueChange = { colCourses = it }, label = { Text("Courses Offered (Comma separated)") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = colMinFees, onValueChange = { colMinFees = it }, label = { Text("Min Fees (₹ L)") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = colMaxFees, onValueChange = { colMaxFees = it }, label = { Text("Max Fees (₹ L)") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = colRanking, onValueChange = { colRanking = it }, label = { Text("NIRF Ranking") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = colOverview, onValueChange = { colOverview = it }, label = { Text("Overview & Description") }, minLines = 2, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = colImageUrl, onValueChange = { colImageUrl = it }, label = { Text("Logo / Image URL") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = colHeroBannerUrl, onValueChange = { colHeroBannerUrl = it }, label = { Text("Hero Banner URL") }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (colName.isNotBlank() && colLocation.isNotBlank()) {
                            val col = CollegeEntity(
                                id = editingCollege?.id ?: 0,
                                name = colName.trim(),
                                location = colLocation.trim(),
                                state = colLocation.split(",").lastOrNull()?.trim() ?: "Karnataka",
                                city = colLocation.split(",").firstOrNull()?.trim() ?: "Bangalore",
                                coursesOffered = colCourses.trim().ifBlank { "MBBS, B.Tech, MBA" },
                                minFeesLakhs = colMinFees.toDoubleOrNull() ?: 2.0,
                                maxFeesLakhs = colMaxFees.toDoubleOrNull() ?: 8.0,
                                hostelAvailable = true,
                                hostelFeesPerYear = 60000,
                                recognition = "UGC Approved",
                                ranking = colRanking.toIntOrNull() ?: 10,
                                eligibility = "12th Science 50%",
                                admissionStatus = "Open",
                                imageUrl = colImageUrl.ifBlank { "https://images.unsplash.com/photo-1562774053-701939374585?w=800" },
                                heroBannerUrl = colHeroBannerUrl.ifBlank { "https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=1200" },
                                overview = colOverview.ifBlank { "Premier academic institution." },
                                averagePackageLakhs = 8.5,
                                highestPackageLakhs = 22.0
                            )
                            onSaveCollege(col)
                            showCollegeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Save College") }
            },
            dismissButton = { OutlinedButton(onClick = { showCollegeDialog = false }, shape = RoundedCornerShape(10.dp)) { Text("Cancel") } }
        )
    }

    // Student Full Details Dialog
    selectedStudentDetails?.let { std ->
        AlertDialog(
            onDismissRequest = { selectedStudentDetails = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = EduBluePrimary.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EduBluePrimary)
                        }
                    }
                    Column {
                        Text(std.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            text = if (std.isAdmin) "Administrator Profile" else "Registered Student",
                            fontSize = 12.sp,
                            color = if (std.isAdmin) Color(0xFFDC2626) else EduBluePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Divider(color = Color.LightGray.copy(alpha = 0.4f))

                    AdminDetailRow(label = "Full Name", value = std.name)
                    AdminDetailRow(label = "Verified Phone", value = std.phone)
                    AdminDetailRow(label = "Email Address", value = std.email.ifBlank { "Not provided" })
                    AdminDetailRow(label = "School / College", value = std.institution.ifBlank { "Not specified" })
                    AdminDetailRow(label = "Class / Qualification", value = std.studentClass.ifBlank { "12th Standard" })
                    AdminDetailRow(label = "Gender", value = std.gender.ifBlank { "Not specified" })
                    AdminDetailRow(
                        label = "Academic Score",
                        value = if (std.percentageMark > 0.0) "${std.percentageMark}%" else "85.0%"
                    )
                    AdminDetailRow(label = "City / District", value = std.city.ifBlank { "Not specified" })
                    AdminDetailRow(label = "State", value = std.state.ifBlank { "Not specified" })
                    AdminDetailRow(label = "Country", value = std.country.ifBlank { "India" })

                    if (std.uid.isNotBlank()) {
                        AdminDetailRow(label = "Firebase UID", value = std.uid)
                    }

                    if (std.createdAt > 0) {
                        AdminDetailRow(label = "Registration Date", value = formatAdminTimestamp(std.createdAt))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val cleanDigits = std.phone.filter { it.isDigit() }
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanDigits"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call")
                        }

                        Button(
                            onClick = {
                                try {
                                    val cleanDigits = std.phone.filter { it.isDigit() }
                                    val waUrl = "https://api.whatsapp.com/send?phone=$cleanDigits&text=Hello%20${Uri.encode(std.name)}%2C%20CareerPath%20Support%20team%20here."
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedStudentDetails = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Close") }
            }
        )
    }
}

@Composable
private fun AdminDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            modifier = Modifier.weight(0.58f)
        )
    }
}

@Composable
private fun MetricPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ApplicationAdminCard(
    form: AdmissionFormEntity,
    onUpdateFormStatus: (formId: Int, newStatus: String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with App Ref & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#APP-2026-${form.id}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                val (badgeBg, badgeFg) = when (form.status) {
                    "Accepted" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                    "Rejected" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
                    "Shortlisted" -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
                    else -> Color(0xFFFEF3C7) to Color(0xFFB45309)
                }

                Surface(color = badgeBg, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = form.status,
                        color = badgeFg,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prominent Received Timestamp Badge
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = EduBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Received On: ${formatAdminTimestamp(form.timestamp)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Student Details
            Text(text = form.studentName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "📞 Phone: ${form.phone} • ✉️ ${form.email}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "🎓 Qualification: ${form.qualification} (${form.percentageMark}%)", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "🏫 College: ${form.preferredCollege}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EduBluePrimary)
            Text(text = "📚 Course: ${form.preferredCourse}", fontSize = 12.sp, color = Color.DarkGray)

            if (form.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "📝 Notes: ${form.notes}", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onUpdateFormStatus(form.id, "Accepted") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onUpdateFormStatus(form.id, "Shortlisted") },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Shortlist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onUpdateFormStatus(form.id, "Rejected") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ClosedRed),
                    border = BorderStroke(1.dp, ClosedRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CounsellingAdminCard(
    booking: CounsellingBookingEntity,
    onUpdateStatus: (bookingId: Int, newStatus: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "#COUNS-2026-${booking.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    val typeBadgeColor = when {
                        booking.bookingType.contains("career", ignoreCase = true) -> Color(0xFFEEF2FF) to Color(0xFF4F46E5)
                        booking.bookingType.contains("college", ignoreCase = true) -> Color(0xFFECFDF5) to Color(0xFF059669)
                        else -> Color(0xFFE0F2FE) to Color(0xFF0284C7)
                    }

                    Surface(
                        color = typeBadgeColor.first,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = booking.bookingType,
                            color = typeBadgeColor.second,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                val statusColors = when (booking.status) {
                    "Completed" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                    "Cancelled" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
                    "In Progress" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
                    else -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                }

                Surface(
                    color = statusColors.first,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status,
                        color = statusColors.second,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prominent Submitted Timestamp Badge
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = EduBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Booking Received On: ${formatAdminTimestamp(booking.timestamp)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = booking.studentName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "📞 Phone: ${booking.studentPhone}", fontSize = 12.sp, color = Color.Gray)
            if (booking.studentClass.isNotBlank()) {
                Text(text = "🎓 Class: ${booking.studentClass}", fontSize = 12.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "👨‍💼 Counsellor: ${booking.counsellorName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
            Text(text = "🎯 Target Topic: ${booking.targetCourse}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EduBluePrimary)
            Text(text = "📅 Scheduled Date: ${booking.bookingDate} (${booking.timeSlot})", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            val feeText = if (booking.feeAmount == 0.0) "FREE (₹0)" else "₹${booking.feeAmount.toInt()}"
            Text(text = "💳 Fee: $feeText (${booking.paymentStatus})", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Action Buttons: Call Student & Update Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (booking.studentPhone.isNotBlank()) {
                    FilledTonalButton(
                        onClick = {
                            try {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.studentPhone}"))
                                context.startActivity(dialIntent)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Could not open dialer", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFDCFCE7),
                            contentColor = Color(0xFF15803D)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (booking.status != "Completed") {
                    Button(
                        onClick = { onUpdateStatus(booking.id, "Completed") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Complete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (booking.status != "Cancelled") {
                    OutlinedButton(
                        onClick = { onUpdateStatus(booking.id, "Cancelled") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ClosedRed),
                        border = BorderStroke(1.dp, ClosedRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
