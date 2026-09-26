package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.data.model.AICounselingQuery
import com.aistudio.carrerpath.counseling.data.model.AICounselingResult
import com.aistudio.carrerpath.counseling.data.model.capitalizeWords
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AICounselingScreen(
    currentQuery: AICounselingQuery,
    aiResult: AICounselingResult?,
    isLoading: Boolean,
    onRunCounseling: (
        qualification: String,
        marks: Double,
        state: String,
        budget: Double,
        course: String,
        city: String,
        additional: String
    ) -> Unit,
    onApplyToRecommended: (collegeName: String, courseName: String) -> Unit
) {
    var qualification by remember { mutableStateOf(currentQuery.qualification) }
    var percentageText by remember { mutableStateOf(currentQuery.percentage.toString()) }
    var state by remember { mutableStateOf(currentQuery.state) }
    var budgetText by remember { mutableStateOf(currentQuery.budgetLakhs.toString()) }
    var preferredCourse by remember { mutableStateOf(currentQuery.preferredCourse) }
    var preferredCity by remember { mutableStateOf(currentQuery.preferredCity) }
    var additionalGoals by remember { mutableStateOf(currentQuery.additionalGoals) }

    val qualificationOptions = listOf(
        "12th Science (PCB)", "12th Science (PCM)", "12th Commerce", "12th Arts / Humanities", "10th Standard", "Graduate Degree"
    )

    val courseOptions = listOf(
        "MBBS", "BDS", "BAMS", "BHMS", "BNYS", "Nursing", "Pharmacy",
        "Engineering (B.Tech)", "MBA", "MCA", "BCA", "Aviation (CPL)",
        "Allied Health Sciences", "Physiotherapy (BPT)", "Law (BA LLB)", "Commerce (B.Com)"
    )

    var qualExpanded by remember { mutableStateOf(false) }
    var courseExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                    )
                )
                .statusBarsPadding()
                .padding(22.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "AI-Powered Student Career and Guidance",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Meet expert counsellors in your region and get personalized guidance for your education and career.",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // Question Form Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, EduCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Student Profile & Goals",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Qualification Dropdown
                    ExposedDropdownMenuBox(
                        expanded = qualExpanded,
                        onExpandedChange = { qualExpanded = !qualExpanded }
                    ) {
                        OutlinedTextField(
                            value = qualification,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Current Qualification") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = qualExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = qualExpanded,
                            onDismissRequest = { qualExpanded = false }
                        ) {
                            qualificationOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        qualification = option
                                        qualExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Marks Percentage
                    OutlinedTextField(
                        value = percentageText,
                        onValueChange = { percentageText = it },
                        label = { Text("Marks / Percentage (%)") },
                        leadingIcon = { Icon(Icons.Default.Grade, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Preferred Course Dropdown
                    ExposedDropdownMenuBox(
                        expanded = courseExpanded,
                        onExpandedChange = { courseExpanded = !courseExpanded }
                    ) {
                        OutlinedTextField(
                            value = preferredCourse,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred Course / Major") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = courseExpanded,
                            onDismissRequest = { courseExpanded = false }
                        ) {
                            courseOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        preferredCourse = option
                                        courseExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. Annual Budget
                    OutlinedTextField(
                        value = budgetText,
                        onValueChange = { budgetText = it },
                        label = { Text("Annual Budget (₹ Lakhs / Year)") },
                        leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. State & City
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it.capitalizeWords() },
                            label = { Text("Home State") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = preferredCity,
                            onValueChange = { preferredCity = it.capitalizeWords() },
                            label = { Text("Preferred City") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 6. Additional Goals
                    OutlinedTextField(
                        value = additionalGoals,
                        onValueChange = { additionalGoals = it },
                        label = { Text("Additional Preferences / Notes") },
                        placeholder = { Text("e.g. Research facilities, hostel quality, placements") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val marks = percentageText.toDoubleOrNull() ?: 80.0
                            val budget = budgetText.toDoubleOrNull() ?: 10.0
                            onRunCounseling(qualification, marks, state, budget, preferredCourse, preferredCity, additionalGoals)
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Analyzing Matches...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Get AI Recommendations", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Result Section
            aiResult?.let { result ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = OpenGreenContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${result.matchScore}% Match Score",
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Text(
                                text = "TOP RECOMMENDATION",
                                color = EduPurpleSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = result.recommendedCollegeName.replace("**", "").replace("*", ""),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Course: ${result.recommendedCourse.replace("**", "").replace("*", "")}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EduBluePrimary,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Reasoning & Match Analysis", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = result.reasoning.replace("**", "").replace("*", ""),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Eligibility: ${result.eligibilityCheck.replace("**", "").replace("*", "")}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Estimated Fees: ${result.estimatedFeeRange.replace("**", "").replace("*", "")}", fontSize = 12.sp)
                                Text("Career Scope: ${result.careerOpportunities.replace("**", "").replace("*", "")}", fontSize = 12.sp, color = SuccessGreen)
                            }
                        }

                        if (result.alternateColleges.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Other Recommended Options", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            result.alternateColleges.forEach { alt ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(alt.replace("**", "").replace("*", ""), fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                onApplyToRecommended(result.recommendedCollegeName, result.recommendedCourse)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Apply to ${result.recommendedCollegeName.take(22)}...", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
