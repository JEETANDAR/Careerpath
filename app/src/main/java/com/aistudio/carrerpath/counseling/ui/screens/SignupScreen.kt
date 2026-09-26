package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.data.model.capitalizeWords
import com.aistudio.carrerpath.counseling.ui.theme.*

@Composable
fun SignupScreen(
    prefilledPhone: String,
    onCompleteSignup: (
        name: String,
        phone: String,
        institution: String,
        studentClass: String,
        gender: String,
        city: String,
        state: String,
        country: String,
        onResult: (Boolean, String) -> Unit
    ) -> Unit
) {
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var studentClass by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("India") }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female", "Other")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                )
            )
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Branding Header
            Surface(
                color = Color.White.copy(alpha = 0.20f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Complete Your Profile", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                text = "Welcome to EduVerse! Tell us a bit about yourself.",
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Form Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "STUDENT PROFILE DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EduBluePrimary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    errorMsg?.let { err ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(err, color = Color(0xFFC62828), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // 1. Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it.capitalizeWords(); errorMsg = null },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EduBluePrimary) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Phone Number (Prefilled & Disabled)
                    OutlinedTextField(
                        value = prefilledPhone,
                        onValueChange = {},
                        enabled = false,
                        readOnly = true,
                        label = { Text("Phone Number (Verified)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray) },
                        trailingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Institution
                    OutlinedTextField(
                        value = institution,
                        onValueChange = { institution = it.capitalizeWords(); errorMsg = null },
                        label = { Text("Institution / School / College") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = EduBluePrimary) },
                        placeholder = { Text("e.g. St. Joseph's College, Bangalore") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3b. Class / Qualification
                    OutlinedTextField(
                        value = studentClass,
                        onValueChange = { studentClass = it.capitalizeWords(); errorMsg = null },
                        label = { Text("Class / Standard / Qualification *") },
                        leadingIcon = { Icon(Icons.Default.Class, contentDescription = null, tint = EduBluePrimary) },
                        placeholder = { Text("e.g. 10th Standard, 12th Standard, B.Tech") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Gender
                    Text("Gender", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genderOptions.forEach { option ->
                            val isSelected = gender == option
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) EduBluePrimary else Color(0xFFF0F4F8),
                                border = BorderStroke(1.dp, if (isSelected) EduBluePrimary else Color(0xFFE0E0E0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { gender = option }
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSelected) Color.White else Color.DarkGray,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. State *
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it.capitalizeWords(); errorMsg = null },
                        label = { Text("State *") },
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = EduBluePrimary) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. City *
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it.capitalizeWords(); errorMsg = null },
                        label = { Text("City / District *") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = EduBluePrimary) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 7. Country
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it.capitalizeWords(); errorMsg = null },
                        label = { Text("Country") },
                        leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = EduBluePrimary) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (fullName.isBlank() || institution.isBlank() || studentClass.isBlank() || state.isBlank() || city.isBlank() || country.isBlank()) {
                                errorMsg = "Please complete all mandatory fields (Name, Institution, Class, State, City) to finish your signup."
                                return@Button
                            }

                            isLoading = true
                            errorMsg = null

                            onCompleteSignup(
                                fullName.trim(),
                                prefilledPhone,
                                institution.trim(),
                                studentClass.trim().ifBlank { "12th Standard" },
                                gender,
                                city.trim(),
                                state.trim(),
                                country.trim()
                            ) { success, msg ->
                                isLoading = false
                                if (!success) {
                                    errorMsg = msg
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saving Profile...", fontSize = 14.sp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Complete Signup & Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
