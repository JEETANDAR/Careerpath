package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aistudio.carrerpath.counseling.data.model.CollegeEntity
import com.aistudio.carrerpath.counseling.ui.components.CollegeDetailDialog
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeExplorerScreen(
    colleges: List<CollegeEntity>,
    savedCollegeIds: List<Int>,
    searchQuery: String,
    currentLanguage: String = "en",
    onSearchChange: (String) -> Unit,
    onToggleSave: (collegeId: Int) -> Unit,
    onOpenAdmissionForm: (collegeName: String) -> Unit
) {
    val isKn = currentLanguage == "kn"
    var selectedCollegeDetail by remember { mutableStateOf<CollegeEntity?>(null) }
    var hostelOnlyFilter by remember { mutableStateOf(false) }
    var openAdmissionOnlyFilter by remember { mutableStateOf(false) }

    val filteredList = remember(colleges, hostelOnlyFilter, openAdmissionOnlyFilter) {
        colleges.filter { college ->
            (!hostelOnlyFilter || college.hostelAvailable) &&
            (!openAdmissionOnlyFilter || college.admissionStatus == "Open")
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Filter Header Card (Compact & Clean - No excess padding)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, EduCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text(if (isKn) "ಕಾಲೇಜು, ನಗರ, ಕೋರ್ಸ್ ಹುಡುಕಿ..." else "Search colleges, city, state, courses...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = EduBluePrimary,
                            unfocusedBorderColor = EduCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Filter Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = hostelOnlyFilter,
                            onClick = { hostelOnlyFilter = !hostelOnlyFilter },
                            label = { Text(if (isKn) "ವಸತಿ ನಿಲಯ ಲಭ್ಯ" else "Hostel Available", fontSize = 12.sp, fontWeight = if (hostelOnlyFilter) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = if (hostelOnlyFilter) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )

                        FilterChip(
                            selected = openAdmissionOnlyFilter,
                            onClick = { openAdmissionOnlyFilter = !openAdmissionOnlyFilter },
                            label = { Text(if (isKn) "ಪ್ರವೇಶ ತೆರೆದಿದೆ" else "Admissions Open", fontSize = 12.sp, fontWeight = if (openAdmissionOnlyFilter) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = if (openAdmissionOnlyFilter) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }
        }

        // College List Results
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isKn) "ಯಾವುದೇ ಕಾಲೇಜುಗಳು ಕಂಡುಬಂದಿಲ್ಲ" else "No colleges matched your search query",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { college ->
                val isSaved = savedCollegeIds.contains(college.id)
                val cName = if (isKn && college.nameKn.isNotBlank()) college.nameKn else college.name
                val cLoc = if (isKn && college.locationKn.isNotBlank()) college.locationKn else college.location
                val cCourses = if (isKn && college.coursesOfferedKn.isNotBlank()) college.coursesOfferedKn else college.coursesOffered
                val cRec = if (isKn && college.recognitionKn.isNotBlank()) college.recognitionKn else college.recognition

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EduCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCollegeDetail = college }
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Card Header Banner
                        val imgUrl = college.imageUrl.ifBlank { college.heroBannerUrl }
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    color = RankBadgeGoldContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isKn) "#${college.ranking} ರ‍್ಯಾಂಕ್" else "#${college.ranking} NIRF Rank",
                                        color = RankBadgeGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                // Save Bookmark Icon
                                IconButton(
                                    onClick = { onToggleSave(college.id) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.4f))
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Save College",
                                        tint = if (isSaved) Color.Yellow else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // College Logo in the Bluish Banner Center
                            if (imgUrl.isNotBlank()) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                        contentDescription = cName,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.align(Alignment.BottomStart),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cLoc,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // College Details Row with Emblem Logo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            val imgUrl = college.imageUrl.ifBlank { college.heroBannerUrl }
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shadowElevation = 2.dp,
                                modifier = Modifier.size(54.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imgUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = cName,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = "${if (isKn) "ಕೋರ್ಸ್‌ಗಳು" else "Courses"}: $cCourses",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Card Footer Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Badges Row: Recognition & Hostel
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (cRec.isNotBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Text(
                                            text = cRec,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                if (college.hostelAvailable) {
                                    Surface(
                                        color = EduBlueContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isKn) "ವಸತಿ ನಿಲಯ" else "Hostel",
                                            color = EduBlueOnContainer,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(if (isKn) "ಶುಲ್ಕದ ಶ್ರೇಣಿ" else "Fees Range", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "₹${college.minFeesLakhs} - ${college.maxFeesLakhs} L / yr",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EduBluePrimary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { selectedCollegeDetail = college },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (isKn) "ವಿವರಗಳು" else "Details", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = { onOpenAdmissionForm(cName) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (isKn) "ಅರ್ಜಿ ಸಲ್ಲಿಸಿ" else "Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // College Detail Dialog / Modal
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

