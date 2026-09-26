package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.aistudio.carrerpath.counseling.ui.theme.*

@Composable
fun CollegeDetailDialog(
    college: CollegeEntity,
    currentLanguage: String = "en",
    onDismiss: () -> Unit,
    onOpenAdmissionForm: (collegeName: String) -> Unit
) {
    val isKn = currentLanguage == "kn"
    val itemCName = if (isKn && college.nameKn.isNotBlank()) college.nameKn else college.name
    val itemCLoc = if (isKn && college.locationKn.isNotBlank()) college.locationKn else college.location
    val itemCRec = if (isKn && college.recognitionKn.isNotBlank()) college.recognitionKn else college.recognition
    val itemCElig = if (isKn && college.eligibilityKn.isNotBlank()) college.eligibilityKn else college.eligibility
    val itemCOverview = if (isKn && college.overviewKn.isNotBlank()) college.overviewKn else college.overview
    val itemCCourses = if (isKn && college.coursesOfferedKn.isNotBlank()) college.coursesOfferedKn else college.coursesOffered
    val itemImgUrl = college.imageUrl.ifBlank { college.heroBannerUrl }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 520.dp)
                .wrapContentHeight()
                .heightIn(max = 640.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Header Banner with College Logo and Ranking
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Surface(
                        color = RankBadgeGoldContainer,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = if (isKn) "#${college.ranking} NIRF ರ‍್ಯಾಂಕ್" else "#${college.ranking} NIRF Rank",
                            color = RankBadgeGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (itemImgUrl.isNotBlank()) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.Center)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(itemImgUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = itemCName,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Row(
                        modifier = Modifier.align(Alignment.BottomStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = itemCLoc,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = itemCName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )

                    // Scrollable Info Section
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Stat Cards Grid
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(if (isKn) "ಸರಾಸರಿ ವೇತನ" else "Avg Package", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${college.averagePackageLakhs} LPA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EduBluePrimary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(if (isKn) "ಗರಿಷ್ಠ ವೇತನ" else "Highest Package", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${college.highestPackageLakhs} LPA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(if (isKn) "ವಾರ್ಷಿಕ ಶುಲ್ಕ" else "Annual Fees", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${college.minFeesLakhs}-${college.maxFeesLakhs} L", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        // Badges Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (itemCRec.isNotBlank()) {
                                Surface(
                                    color = EduBlueContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = itemCRec,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = EduBlueOnContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            if (college.hostelAvailable) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isKn) "ವಸತಿ ನಿಲಯ ಲಭ್ಯ (₹${college.hostelFeesPerYear}/ವರ್ಷ)" else "Hostel Available (₹${college.hostelFeesPerYear}/yr)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Eligibility
                        if (itemCElig.isNotBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = if (isKn) "ಪ್ರವೇಶ ಅರ್ಹತೆ" else "Eligibility Criteria",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = itemCElig,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        // Overview
                        if (itemCOverview.isNotBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = if (isKn) "ಕಾಲೇಜು ಅವಲೋಕನ" else "Institution Overview",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = itemCOverview,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        // Courses Offered
                        if (itemCCourses.isNotBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = if (isKn) "ಲಭ್ಯವಿರುವ ಕೋರ್ಸ್‌ಗಳು" else "Offered Courses",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = itemCCourses,
                                    fontSize = 12.sp,
                                    color = EduBluePrimary,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text(if (isKn) "ಮುಚ್ಚಿ" else "Close", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onOpenAdmissionForm(itemCName)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(46.dp)
                        ) {
                            Text(if (isKn) "ಈಗ ಅರ್ಜಿ ಸಲ್ಲಿಸಿ" else "Apply Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
