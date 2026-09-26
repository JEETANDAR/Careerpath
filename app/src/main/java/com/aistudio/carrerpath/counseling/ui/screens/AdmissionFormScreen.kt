package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.data.model.StudentProfile
import com.aistudio.carrerpath.counseling.data.model.capitalizeWords
import com.aistudio.carrerpath.counseling.ui.theme.EduBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmissionFormScreen(
    studentProfile: StudentProfile,
    initialCollegeName: String,
    initialCourseName: String,
    submissionSuccessMessage: String?,
    onSubmitForm: (
        name: String,
        phone: String,
        email: String,
        qualification: String,
        percentage: Double,
        course: String,
        college: String,
        city: String,
        notes: String
    ) -> Unit,
    onClearSuccess: () -> Unit,
    onNavigateHome: () -> Unit = {},
    onViewDashboard: () -> Unit
) {
    var name by remember { mutableStateOf(studentProfile.name) }
    var phone by remember { mutableStateOf(studentProfile.phone) }
    var email by remember { mutableStateOf(studentProfile.email) }
    var qualification by remember { mutableStateOf(studentProfile.qualification) }
    var percentageText by remember { mutableStateOf(studentProfile.percentage.toString()) }
    var preferredCourse by remember { mutableStateOf(initialCourseName.ifEmpty { studentProfile.preferredCourse }) }
    var preferredCollege by remember { mutableStateOf(initialCollegeName) }
    var city by remember { mutableStateOf(studentProfile.preferredCity) }
    var notes by remember { mutableStateOf("") }

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
                    text = "DIRECT APPLICATION",
                    color = EduBluePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Admission Form",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Submit your profile directly to top partner institutions",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Personal & Academic Profile",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it.capitalizeWords() },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = qualification,
                            onValueChange = { qualification = it.capitalizeWords() },
                            label = { Text("Qualification") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = percentageText,
                            onValueChange = { percentageText = it },
                            label = { Text("Marks (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Admission Preferences",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = preferredCollege,
                        onValueChange = { preferredCollege = it.capitalizeWords() },
                        label = { Text("Preferred College") },
                        placeholder = { Text("e.g. AIIMS New Delhi, CMC Vellore, MAHE") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = preferredCourse,
                        onValueChange = { preferredCourse = it.capitalizeWords() },
                        label = { Text("Preferred Course") },
                        placeholder = { Text("e.g. MBBS, BAMS, B.Tech, MBA, Aviation") },
                        leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it.capitalizeWords() },
                        label = { Text("City") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Additional Remarks") },
                        placeholder = { Text("Mention hostel requirement or scholarship request") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val marks = percentageText.toDoubleOrNull() ?: 80.0
                            onSubmitForm(
                                name, phone, email, qualification, marks, preferredCourse, preferredCollege, city, notes
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Admission Application", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Success Confirmation Dialog
    submissionSuccessMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = {
                onClearSuccess()
                onNavigateHome()
            },
            title = { Text("Application Received", fontWeight = FontWeight.Bold) },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = {
                    onClearSuccess()
                    onViewDashboard()
                }) {
                    Text("View Application Tracking")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onClearSuccess()
                    onNavigateHome()
                }) {
                    Text("Close")
                }
            }
        )
    }
}
