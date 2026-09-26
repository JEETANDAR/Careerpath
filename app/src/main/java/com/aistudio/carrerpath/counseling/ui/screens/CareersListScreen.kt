package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aistudio.carrerpath.counseling.data.model.CareerEntity
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareersListScreen(
    careers: List<CareerEntity>,
    currentLanguage: String,
    onCareerClick: (CareerEntity) -> Unit,
    onBookCounsellingClick: (CareerEntity) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val isKn = currentLanguage == "kn"

    val filtered = careers.filter { c ->
        val title = if (isKn && c.titleKn.isNotBlank()) c.titleKn else c.title
        val desc = if (isKn && c.descriptionKn.isNotBlank()) c.descriptionKn else c.description
        searchQuery.isBlank() || title.contains(searchQuery, ignoreCase = true) || desc.contains(searchQuery, ignoreCase = true) || c.category.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable Header Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isKn) "ಉನ್ನತ ಉದ್ಯೋಗಗಳು & ವೃತ್ತಿ ಮಾರ್ಗಗಳು" else "Top Careers & Career Pathways",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EduBluePrimary
                    )
                    Text(
                        text = if (isKn) "ನಿಮ್ಮ ಆಸಕ್ತಿಗಳಿಗೆ ಹೊಂದುವ ಸೂಕ್ತ ವೃತ್ತಿಯನ್ನು ಆಯ್ಕೆ ಮಾಡಿ" else "Discover career choices, salaries, pathways, and top colleges",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Search Box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(if (isKn) "ವೃತ್ತಿ ಹುಡುಕಿ..." else "Search career (e.g., Engineering, Medical, IAS)...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EduBluePrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EduBluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        items(filtered) { career ->
            val titleText = if (isKn && career.titleKn.isNotBlank()) career.titleKn else career.title
            val taglineText = if (isKn && career.taglineKn.isNotBlank()) career.taglineKn else career.tagline

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCareerClick(career) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val imgUrl = career.image.ifBlank {
                            when {
                                career.category.contains("Medical", true) || career.category.contains("Health", true) -> "https://images.pexels.com/photos/5452292/pexels-photo-5452292.jpeg?auto=compress&cs=tinysrgb&w=800"
                                career.category.contains("Tech", true) || career.category.contains("Engineering", true) -> "https://images.pexels.com/photos/3861958/pexels-photo-3861958.jpeg?auto=compress&cs=tinysrgb&w=800"
                                career.category.contains("Design", true) || career.category.contains("Art", true) -> "https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=800"
                                career.category.contains("Aviation", true) || career.category.contains("Pilot", true) -> "https://images.pexels.com/photos/2026324/pexels-photo-2026324.jpeg?auto=compress&cs=tinysrgb&w=800"
                                career.category.contains("Law", true) || career.category.contains("Legal", true) -> "https://images.pexels.com/photos/5668772/pexels-photo-5668772.jpeg?auto=compress&cs=tinysrgb&w=800"
                                career.category.contains("Commerce", true) || career.category.contains("Management", true) -> "https://images.pexels.com/photos/3184292/pexels-photo-3184292.jpeg?auto=compress&cs=tinysrgb&w=800"
                                else -> "https://images.pexels.com/photos/3184418/pexels-photo-3184418.jpeg?auto=compress&cs=tinysrgb&w=800"
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(EduBlueContainer, Color(0xFFE2E8F0))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imgUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = titleText,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = EduBlueContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    career.category,
                                    color = EduBluePrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                titleText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (taglineText.isNotBlank()) {
                                Text(
                                    taglineText,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                career.salaryAverage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        Button(
                            onClick = { onBookCounsellingClick(career) },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isKn) "ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್" else "Book Counselling",
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
