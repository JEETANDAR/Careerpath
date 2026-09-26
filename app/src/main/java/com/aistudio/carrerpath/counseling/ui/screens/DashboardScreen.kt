package com.aistudio.carrerpath.counseling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.data.model.StudentProfile
import com.aistudio.carrerpath.counseling.ui.theme.*

@Composable
fun DashboardScreen(
    userProfile: StudentProfile,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit = {}
) {
    val isKn = currentLanguage == "kn"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Header Profile Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                    )
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(3.dp, EduBlueContainer),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userProfile.name.ifBlank { "Student" }.take(1).uppercase(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduBluePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile.name.ifBlank { "Student User" },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = userProfile.phone,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                if (userProfile.institution.isNotBlank()) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = userProfile.institution,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // Profile Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isKn) "ಪ್ರೊಫೈಲ್ ಮಾಹಿತಿ" else "PROFILE DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EduBluePrimary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileRowItem(Icons.Default.Person, if (isKn) "ಹೆಸರು" else "Full Name", userProfile.name)
                    ProfileRowItem(Icons.Default.Phone, if (isKn) "ಫೋನ್ ಸಂಖ್ಯೆ" else "Phone Number", userProfile.phone)
                    ProfileRowItem(Icons.Default.School, if (isKn) "ಸಂಸ್ಥೆ" else "Institution", userProfile.institution.ifBlank { "Not specified" })
                    ProfileRowItem(Icons.Default.Wc, if (isKn) "ಲಿಂಗ" else "Gender", userProfile.gender)
                    ProfileRowItem(Icons.Default.LocationCity, if (isKn) "ನಗರ" else "City", userProfile.city.ifBlank { "India" })
                    ProfileRowItem(Icons.Default.Map, if (isKn) "ರಾಜ್ಯ" else "State", userProfile.state.ifBlank { "Karnataka" })
                    ProfileRowItem(Icons.Default.Public, if (isKn) "ದೇಶ" else "Country", userProfile.country.ifBlank { "India" })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Settings & Language Selector
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isKn) "ಅಪ್ಲಿಕೇಶನ್ ಸೆಟ್ಟಿಂಗ್‌ಗಳು" else "APP SETTINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EduBluePrimary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Language Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(if (isKn) "ಭಾಷೆ / Language" else "App Language", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (!isKn) EduBluePrimary else Color(0xFFE0E0E0),
                                modifier = Modifier.clickable { onLanguageChange("en") }
                            ) {
                                Text(
                                    "English",
                                    color = if (!isKn) Color.White else Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isKn) EduBluePrimary else Color(0xFFE0E0E0),
                                modifier = Modifier.clickable { onLanguageChange("kn") }
                            ) {
                                Text(
                                    "ಕನ್ನಡ",
                                    color = if (isKn) Color.White else Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout Button
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = ClosedRed),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isKn) "ಲಾಗಿನ್‌ಯಿಂದ ನಿರ್ಗಮಿಸಿ (Logout)" else "Logout", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileRowItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(label, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.width(100.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f))
    }
}
