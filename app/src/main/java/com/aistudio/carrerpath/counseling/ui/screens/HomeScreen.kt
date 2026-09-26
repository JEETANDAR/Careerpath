package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aistudio.carrerpath.counseling.R
import com.aistudio.carrerpath.counseling.data.model.CareerEntity
import com.aistudio.carrerpath.counseling.data.model.CollegeEntity
import com.aistudio.carrerpath.counseling.data.model.CourseEntity
import com.aistudio.carrerpath.counseling.data.model.PsychometricAssessmentEntity
import com.aistudio.carrerpath.counseling.data.model.ScholarshipEntity
import com.aistudio.carrerpath.counseling.ui.components.VidyabotFloatingCompanion
import com.aistudio.carrerpath.counseling.ui.theme.*
import com.aistudio.carrerpath.counseling.ui.viewmodel.AppNavTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    studentName: String,
    studentQualification: String = "12th Pass",
    careers: List<CareerEntity>,
    featuredColleges: List<CollegeEntity>,
    allCourses: List<CourseEntity>,
    scholarships: List<ScholarshipEntity>,
    assessment: PsychometricAssessmentEntity? = null,
    unreadNotificationCount: Int,
    currentLanguage: String,
    onNavigateTo: (AppNavTab) -> Unit,
    onSelectCareer: (CareerEntity) -> Unit,
    onSelectCollege: (CollegeEntity) -> Unit,
    onSelectCourse: (CourseEntity) -> Unit,
    onOpenAssessmentReport: () -> Unit = {},
    onTakeAssessment: () -> Unit = {},
    onOpenAdmissionForm: (collegeName: String) -> Unit,
    onOpenAiChatbot: () -> Unit,
    onSearchColleges: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val isKn = currentLanguage == "kn"

    val categoryOptions = listOf(
        "All" to if (isKn) "ಎಲ್ಲವೂ" else "All",
        "Healthcare" to if (isKn) "ವೈದ್ಯಕೀಯ & ಆರೋಗ್ಯ" else "Healthcare / Medical",
        "Engineering" to if (isKn) "ವಿಜ್ಞಾನ & ಎಂಜಿನಿಯರಿಂಗ್" else "Science / Engineering",
        "Commerce" to if (isKn) "ವಾಣಿಜ್ಯ & ನಿರ್ವಹಣೆ" else "Commerce / Management",
        "Arts" to if (isKn) "ಕಲೆ & ಹ್ಯೂಮಾನಿಟೀಸ್" else "Arts / Humanities"
    )

    val filteredCareers = remember(careers, selectedCategoryFilter, searchQuery) {
        careers.filter { c ->
            val matchesCategory = when (selectedCategoryFilter) {
                "Healthcare" -> c.category.contains("Medical", ignoreCase = true) || c.category.contains("Health", ignoreCase = true) || c.title.contains("Doctor", ignoreCase = true)
                "Engineering" -> c.category.contains("Engg", ignoreCase = true) || c.category.contains("Tech", ignoreCase = true) || c.category.contains("Science", ignoreCase = true) || c.title.contains("Software", ignoreCase = true)
                "Commerce" -> c.category.contains("Commerce", ignoreCase = true) || c.category.contains("Management", ignoreCase = true) || c.title.contains("CA", ignoreCase = true)
                "Arts" -> c.category.contains("Arts", ignoreCase = true) || c.category.contains("Humanities", ignoreCase = true) || c.category.contains("Law", ignoreCase = true) || c.title.contains("Civil", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || c.title.contains(searchQuery, ignoreCase = true) || c.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val filteredCourses = remember(allCourses, selectedCategoryFilter, searchQuery) {
        allCourses.filter { course ->
            val matchesCategory = when (selectedCategoryFilter) {
                "Healthcare" -> course.category.contains("Medical", ignoreCase = true) || course.category.contains("Health", ignoreCase = true) || course.code.contains("MBBS", ignoreCase = true)
                "Engineering" -> course.category.contains("Engg", ignoreCase = true) || course.category.contains("Tech", ignoreCase = true) || course.code.contains("BE", ignoreCase = true) || course.code.contains("BTech", ignoreCase = true)
                "Commerce" -> course.category.contains("Commerce", ignoreCase = true) || course.category.contains("Management", ignoreCase = true) || course.code.contains("BCom", ignoreCase = true) || course.code.contains("MBA", ignoreCase = true)
                "Arts" -> course.category.contains("Arts", ignoreCase = true) || course.category.contains("Law", ignoreCase = true) || course.code.contains("BA", ignoreCase = true) || course.code.contains("LLB", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || course.name.contains(searchQuery, ignoreCase = true) || course.code.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val filteredColleges = remember(featuredColleges, selectedCategoryFilter, searchQuery) {
        featuredColleges.filter { col ->
            val matchesCategory = when (selectedCategoryFilter) {
                "Healthcare" -> col.coursesOffered.contains("MBBS", ignoreCase = true) || col.coursesOffered.contains("Medical", ignoreCase = true) || col.name.contains("Medical", ignoreCase = true)
                "Engineering" -> col.coursesOffered.contains("B.Tech", ignoreCase = true) || col.coursesOffered.contains("Engg", ignoreCase = true) || col.name.contains("IIT", ignoreCase = true) || col.name.contains("NIT", ignoreCase = true)
                "Commerce" -> col.coursesOffered.contains("B.Com", ignoreCase = true) || col.coursesOffered.contains("MBA", ignoreCase = true) || col.name.contains("Commerce", ignoreCase = true)
                "Arts" -> col.coursesOffered.contains("B.A", ignoreCase = true) || col.coursesOffered.contains("Law", ignoreCase = true) || col.name.contains("Arts", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || col.name.contains(searchQuery, ignoreCase = true) || col.location.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Top Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isKn) "ಸ್ವಾಗತ • CareerPath" else "WELCOME • CAREERPATH",
                                color = EduBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = studentName.ifBlank { "Student" },
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Notification Bell Button
                            BadgedBox(
                                badge = {
                                    if (unreadNotificationCount > 0) {
                                        Badge(containerColor = ClosedRed) {
                                            Text(text = "$unreadNotificationCount", color = Color.White)
                                        }
                                    }
                                },
                                modifier = Modifier.clickable { onNavigateTo(AppNavTab.NOTIFICATIONS) }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, EduCardBorder),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            // Profile Avatar
                            Surface(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { onNavigateTo(AppNavTab.DASHBOARD) },
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(2.dp, Brush.linearGradient(listOf(GeoGradientStart, GeoGradientEnd)))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.linearGradient(listOf(GeoGradientStart, GeoGradientEnd))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = studentName.ifBlank { "S" }.take(1).uppercase(),
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            onSearchColleges(it)
                        },
                        placeholder = { Text(if (isKn) "ವೃತ್ತಿ, ಕಾಲೇಜು ಅಥವಾ ಕೋರ್ಸ್ ಹುಡುಕಿ..." else "Search Careers, Colleges, Courses...", color = Color.Gray, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    onSearchColleges("")
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = EduBluePrimary,
                            unfocusedBorderColor = EduCardBorder
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // STREAM / CATEGORY FILTER CHIPS
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categoryOptions) { (key, label) ->
                            val isSelected = selectedCategoryFilter == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = key },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EduBluePrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) EduBluePrimary else EduCardBorder,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // PSYCHOMETRIC CAREER APTITUDE BANNER
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, EduBluePrimary.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EduBlueContainer,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = EduBluePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (assessment != null) (if (isKn) "ನಿಮ್ಮ ಸೈಕೋಮೆಟ್ರಿಕ್ ಪರೀಕ್ಷಾ ವರದಿ" else "Your Psychometric Test Report") else (if (isKn) "ಸೈಕೋಮೆಟ್ರಿಕ್ ಪರೀಕ್ಷೆ ತೆಗೆದುಕೊಳ್ಳಿ" else "Take a psychometric test"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (assessment != null) (if (isKn) "ಆರ್ಕಿಟೈಪ್: ${assessment.archetype} • ವರದಿ ವೀಕ್ಷಿಸಿ" else "Archetype: ${assessment.archetype} • View Report") else (if (isKn) "ನಿಮ್ಮ ಆದರ್ಶ ವೃತ್ತಿ & ಕಾಲೇಜು ಆಯ್ಕೆಗೆ 3 ನಿಮಿಷದ ಪರೀಕ್ಷೆ" else "Discover top matching careers & courses in 3 mins"),
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = if (assessment != null) onOpenAssessmentReport else onTakeAssessment,
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (assessment != null) (if (isKn) "ವರದಿ" else "View Report") else (if (isKn) "ಪರೀಕ್ಷೆ ತೆಗೆದುಕೊಳ್ಳಿ" else "Take Test"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // SECTION 1: TOP CAREERS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isKn) "ಉನ್ನತ ವೃತ್ತಿ ಮಾರ್ಗಗಳು" else "Top Careers",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isKn) "ನಿಮ್ಮ ಭವಿಷ್ಯದ ವೃತ್ತಿಯನ್ನು ಆಯ್ಕೆ ಮಾಡಿ" else "Explore top rewarding career pathways",
                            fontSize = 11.sp,
                            color = EduBluePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(onClick = { onNavigateTo(AppNavTab.CAREERS) }) {
                        Text(if (isKn) "ಎಲ್ಲಾ ನೋಡಿ >" else "View All >", color = EduBluePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredCareers.isEmpty()) {
                    Text(
                        if (isKn) "ಈ ವರ್ಗಕ್ಕೆ ವೃತ್ತಿಗಳು ಸಿಗಲಿಲ್ಲ." else "No careers found for selected category.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(filteredCareers) { career ->
                            val titleText = if (isKn && career.titleKn.isNotBlank()) career.titleKn else career.title
                            val taglineText = if (isKn && career.taglineKn.isNotBlank()) career.taglineKn else career.tagline

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, EduCardBorder),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable { onSelectCareer(career) }
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(GeoGradientStart, GeoGradientEnd)
                                                )
                                            )
                                    ) {
                                        val imgUrl = career.image.ifBlank {
                                            when {
                                                career.category.contains("Medical", true) || career.category.contains("Health", true) -> "https://images.unsplash.com/photo-1576091160399-112ba8d25d1f?w=600"
                                                career.category.contains("Tech", true) || career.category.contains("Engineering", true) -> "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600"
                                                career.category.contains("Design", true) || career.category.contains("Art", true) -> "https://images.unsplash.com/photo-1542744094-3a31f272c490?w=600"
                                                career.category.contains("Aviation", true) || career.category.contains("Pilot", true) -> "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=600"
                                                career.category.contains("Law", true) || career.category.contains("Legal", true) -> "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=600"
                                                career.category.contains("Commerce", true) || career.category.contains("Management", true) -> "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=600"
                                                else -> "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=600"
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
                                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                                    )
                                                )
                                                .padding(12.dp)
                                        ) {
                                            Surface(
                                                color = EduBluePrimary,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.align(Alignment.TopStart)
                                            ) {
                                                Text(
                                                    text = career.category,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }

                                            Text(
                                                text = titleText,
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.align(Alignment.BottomStart)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(14.dp)) {
                                        if (taglineText.isNotBlank()) {
                                            Text(
                                                text = taglineText,
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (isKn) "ಸರಾಸರಿ ವೇತನ" else "Avg Salary",
                                                    fontSize = 10.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = career.salaryAverage.ifBlank { "₹8-15 LPA" },
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = SuccessGreen
                                                )
                                            }

                                            Button(
                                                onClick = { onSelectCareer(career) },
                                                colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                            ) {
                                                Text(if (isKn) "ವಿವರಗಳು" else "Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SECTION 2: TOP COURSES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isKn) "ಉನ್ನತ ಕೋರ್ಸ್‌ಗಳು" else "Top Courses",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isKn) "ಉನ್ನತ ಪದವಿ ಮತ್ತು ಡಿಪ್ಲೊಮಾ ಕಾರ್ಯಕ್ರಮಗಳನ್ನು ಅನ್ವೇಷಿಸಿ" else "Explore popular degree & diploma programs",
                            fontSize = 11.sp,
                            color = EduBluePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(onClick = { onNavigateTo(AppNavTab.COURSES) }) {
                        Text(if (isKn) "ಎಲ್ಲಾ ನೋಡಿ >" else "View All >", color = EduBluePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredCourses.isEmpty()) {
                    Text(
                        if (isKn) "ಈ ಕೋರ್ಸ್‌ಗಳು ಲಭ್ಯವಿಲ್ಲ." else "No courses found for selected category.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(filteredCourses) { course ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, EduCardBorder),
                                modifier = Modifier
                                    .width(230.dp)
                                    .clickable { onSelectCourse(course) }
                            ) {
                                Column {
                                    if (course.imageUrl.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFF1E293B), Color(0xFF334155))
                                                    )
                                                )
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(course.imageUrl)
                                                    .crossfade(300)
                                                    .build(),
                                                contentDescription = course.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                                                        )
                                                    )
                                                    .padding(8.dp)
                                            ) {
                                                Surface(
                                                    color = EduBluePrimary,
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.align(Alignment.TopStart)
                                                ) {
                                                    Text(
                                                        text = course.code,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 11.sp,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(14.dp)) {
                                        if (course.imageUrl.isBlank()) {
                                            Surface(
                                                color = EduBlueContainer,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = course.code,
                                                    color = EduBluePrimary,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        Text(
                                            text = course.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "${if (isKn) "ಅವಧಿ" else "Duration"}: ${course.durationYears} • ${if (isKn) "ಶುಲ್ಕ" else "Fee"}: ₹${course.avgFeesLakhs}L/yr",
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = { onSelectCourse(course) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(if (isKn) "ವಿವರಣೆ ನೋಡಿ" else "View Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SECTION 3: TOP ACCREDITED COLLEGES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isKn) "ಉನ್ನತ ಮಾನ್ಯತೆ ಪಡೆದ ಕಾಲೇಜುಗಳು" else "Top Accredited Colleges",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(onClick = { onNavigateTo(AppNavTab.COLLEGES) }) {
                        Text(if (isKn) "ಎಲ್ಲಾ ನೋಡಿ >" else "View All >", color = EduBluePrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredColleges.isEmpty()) {
                    Text(
                        if (isKn) "ಕಾಲೇಜುಗಳು ಲಭ್ಯವಿಲ್ಲ." else "No colleges found for selected category.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(filteredColleges) { college ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, EduCardBorder),
                                modifier = Modifier
                                    .width(280.dp)
                                    .clickable { onSelectCollege(college) }
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(115.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                                                )
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Surface(
                                            color = Color.White.copy(alpha = 0.95f),
                                            shape = RoundedCornerShape(20.dp),
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                text = "NIRF RANK #${college.ranking}",
                                                color = EduPurpleSecondary,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        val imgUrl = college.imageUrl.ifBlank { college.heroBannerUrl }
                                        if (imgUrl.isNotBlank()) {
                                            Surface(
                                                color = Color.White,
                                                shape = RoundedCornerShape(12.dp),
                                                shadowElevation = 3.dp,
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .align(Alignment.Center)
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
                                                        .padding(6.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = college.location,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.align(Alignment.BottomStart)
                                        )
                                    }

                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = college.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "${college.location} • ${college.coursesOffered}",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "₹${college.minFeesLakhs} - ${college.maxFeesLakhs}L/yr",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Button(
                                                onClick = { onOpenAdmissionForm(college.name) },
                                                colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                            ) {
                                                Text(if (isKn) "ಅರ್ಜಿ ಸಲ್ಲಿಸಿ" else "Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SECTION 4: SCHOLARSHIPS
                Text(
                    text = if (isKn) "ವಿದ್ಯಾರ್ಥಿವೇತನಗಳು & ಧನಸಹಾಯ" else "Open Scholarships & Grants",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                scholarships.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = EduPurpleContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = EduPurpleSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "${if (isKn) "ಮೊತ್ತ" else "Amount"}: ${item.amountText}",
                                    color = EduBluePrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Animated Mascot Vidyabot Companion with Speech Bubble
        VidyabotFloatingCompanion(
            isKannada = isKn,
            onOpenChatbot = onOpenAiChatbot,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
