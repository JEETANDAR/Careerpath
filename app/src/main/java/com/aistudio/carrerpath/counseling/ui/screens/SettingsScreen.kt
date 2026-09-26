package com.aistudio.carrerpath.counseling.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.data.model.PsychometricAssessmentEntity
import com.aistudio.carrerpath.counseling.data.model.StudentProfile
import com.aistudio.carrerpath.counseling.data.model.capitalizeWords
import com.aistudio.carrerpath.counseling.ui.theme.*

@Composable
fun SettingsScreen(
    profile: StudentProfile,
    assessment: PsychometricAssessmentEntity? = null,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onOpenAssessmentReport: () -> Unit = {},
    onTakeAssessment: () -> Unit = {},
    onSaveProfile: (name: String, phone: String, city: String) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit = {},
    isDeletingAccount: Boolean = false
) {
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var editName by remember(profile) { mutableStateOf(profile.name) }
    var editPhone by remember(profile) { mutableStateOf(profile.phone) }
    var editCity by remember(profile) { mutableStateOf(if (profile.city.isNotBlank()) profile.city else profile.preferredCity) }

    val displayCity = (if (profile.city.isNotBlank()) profile.city else profile.preferredCity).ifBlank { "Bangalore" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Text(
                    text = "STUDENT PROFILE & PREFERENCES",
                    color = EduBluePrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Profile Settings",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "View & update your profile details and career assessment",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // Profile Card (strictly: Full Name, Phone Number, City)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = EduBluePrimary,
                            shape = CircleShape,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = profile.name.ifBlank { "S" }.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile.name.ifBlank { "Student" }, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(profile.phone.ifBlank { "Phone not set" }, fontSize = 13.sp, color = Color.Gray)
                            if (profile.isAdmin) {
                                Surface(
                                    color = EduPurpleContainer,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "ADMINISTRATOR",
                                        color = EduPurpleSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(if (isEditing) Icons.Default.Close else Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }

                    if (isEditing) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Edit Student Details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it.capitalizeWords() },
                            label = { Text("Full Name") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editCity,
                            onValueChange = { editCity = it.capitalizeWords() },
                            label = { Text("City") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                onSaveProfile(editName, editPhone, editCity)
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Changes", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Full Name", color = Color.Gray, fontSize = 13.sp)
                            Text(profile.name.ifBlank { "Student" }, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Phone Number", color = Color.Gray, fontSize = 13.sp)
                            Text(profile.phone.ifBlank { "N/A" }, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("City", color = Color.Gray, fontSize = 13.sp)
                            Text(displayCity, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PSYCHOMETRIC CAREER ASSESSMENT CARD
            Text("Career Aptitude Assessment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

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
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Psychometric Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                if (assessment != null) "Archetype: ${assessment.archetype}" else "Take a 3-minute test to find your career match",
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
                                Text("View Assessment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = onTakeAssessment,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retake Test", fontSize = 13.sp)
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
                            Text("Take Career Assessment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("App Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // Dark Mode Switch
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = EduBluePrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dark Theme Mode", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("Reduce eye strain in low-light", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("App Info & Support", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Application", fontSize = 13.sp, color = Color.Gray)
                        Text("CareerPath Education (2026)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Database", fontSize = 13.sp, color = Color.Gray)
                        Text("500+ Verified Programs", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SuccessGreen)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = EduBluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Direct Counselling Support", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Email: info@careerpath.app", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = EduBluePrimary)
                    Text("Helpline: +91 89044 74264", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sign Out Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EduBluePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("sign_out_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delete Account Button (Styled similarly to the 'Sign Out' button with a destructive red color)
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
                    .testTag("delete_account_button")
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
                    Text("Delete Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            // Generous bottom spacer so both buttons scroll cleanly above the bottom navigation bar
            Spacer(modifier = Modifier.height(140.dp))
        }
    }

    // Confirmation Dialog for Permanent Account Deletion
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeletingAccount) showDeleteConfirmation = false
            },
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
                    text = "Delete Account?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to permanently delete your CareerPath account?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "This action is permanent and cannot be undone. All your personal data—including your profile, counselling bookings, admission applications, psychometric test results, and saved colleges—will be permanently erased.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isDeletingAccount
                ) {
                    Text("Delete Account", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !isDeletingAccount
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
