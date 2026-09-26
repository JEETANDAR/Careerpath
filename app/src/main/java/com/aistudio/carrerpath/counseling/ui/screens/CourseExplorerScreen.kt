package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aistudio.carrerpath.counseling.data.model.CourseEntity
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseExplorerScreen(
    courses: List<CourseEntity>,
    searchQuery: String,
    selectedCategory: String,
    currentLanguage: String = "en",
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSelectCourse: (CourseEntity) -> Unit
) {
    val isKn = currentLanguage == "kn"

    val categories = listOf(
        "All" to if (isKn) "ಎಲ್ಲವೂ" else "All",
        "Engineering" to if (isKn) "ಎಂಜಿನಿಯರಿಂಗ್" else "Engineering",
        "Medical" to if (isKn) "ವೈದ್ಯಕೀಯ" else "Medical",
        "Health Sciences" to if (isKn) "ಆರೋಗ್ಯ ವಿಜ್ಞಾನ" else "Health Sciences",
        "Management" to if (isKn) "ಮ್ಯಾನೇಜ್‌ಮೆಂಟ್" else "Management",
        "IT" to if (isKn) "ಐಟಿ & ಕಂಪ್ಯೂಟರ್" else "IT",
        "Science" to if (isKn) "ವಿಜ್ಞಾನ" else "Science",
        "Commerce" to if (isKn) "ವಾಣಿಜ್ಯ" else "Commerce",
        "Design" to if (isKn) "ವಿನ್ಯಾಸ & ಕಲೆ" else "Design",
        "Aviation" to if (isKn) "ಏವಿಯೇಷನ್" else "Aviation",
        "Law" to if (isKn) "ಕಾನೂನು" else "Law",
        "Arts" to if (isKn) "ಮಾನವಿಕ / ಆರ್ಟ್ಸ್" else "Arts",
        "Education" to if (isKn) "ಶಿಕ್ಷಣ" else "Education",
        "Government" to if (isKn) "ಸರ್ಕಾರಿ ಸೇವೆಗಳು" else "Government"
    )

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Category Header Card
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
                        placeholder = { Text(if (isKn) "ಕೋರ್ಸ್ ಹೆಸರು ಹುಡುಕಿ (MBBS, B.Tech, Nursing...)" else "Search courses (MBBS, B.Tech, Nursing, MBA...)", fontSize = 13.sp) },
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

                    // Categories Horizontal Scroll Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { (key, label) ->
                            val isSelected = selectedCategory.equals(key, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategoryChange(key) },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }
        }

        // Course List Results
        if (courses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isKn) "ಆಯ್ಕೆಮಾಡಿದ ವಿಭಾಗದಲ್ಲಿ ಯಾವುದೇ ಕೋರ್ಸ್‌ಗಳು ಲಭ್ಯವಿಲ್ಲ" else "No courses found for this category / query",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(courses, key = { it.id }) { course ->
                val cName = if (isKn && course.nameKn.isNotBlank()) course.nameKn else course.name
                val cElig = if (isKn && course.eligibilityKn.isNotBlank()) course.eligibilityKn else course.eligibility

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, EduCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectCourse(course) }
                ) {
                    Column {
                        // Course Image Banner
                        if (course.imageUrl.isNotBlank()) {
                            val context = LocalContext.current
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
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
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Black.copy(alpha = 0.2f),
                                                    Color.Black.copy(alpha = 0.65f)
                                                )
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
                                            color = EduPurpleContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = course.code,
                                                color = EduPurpleSecondary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Surface(
                                            color = RankBadgeGoldContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (isKn) "1ನೇ ಕೌನ್ಸೆಲಿಂಗ್ ಉಚಿತ" else "1st Counselling FREE",
                                                color = RankBadgeGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.align(Alignment.BottomStart)
                                    ) {
                                        Text(
                                            text = course.category,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Course Card Details
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (course.imageUrl.isBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = EduPurpleContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = course.code,
                                            color = EduPurpleSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Surface(
                                        color = RankBadgeGoldContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isKn) "1ನೇ ಕೌನ್ಸೆಲಿಂಗ್ ಉಚಿತ" else "1st Counselling FREE",
                                            color = RankBadgeGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text(
                                text = cName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isKn) "ಅರ್ಹತೆ: $cElig" else "Eligibility: $cElig",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(if (isKn) "ಅವಧಿ" else "Duration", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(course.durationYears, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(if (isKn) "ಸರಾಸರಿ ಶುಲ್ಕ" else "Avg Fees", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(if (isKn) "₹${course.avgFeesLakhs} L/yr" else "₹${course.avgFeesLakhs} L/yr", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(if (isKn) "ವೇತನ ಪ್ಯಾಕೇಜ್" else "Avg Package", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("₹${course.avgPackageLakhs} LPA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onSelectCourse(course) },
                                colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isKn) "ವಿವರಗಳು ಮತ್ತು ಕೌನ್ಸೆಲಿಂಗ್ ನೋಡಿ" else "View Details & Counselling",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
