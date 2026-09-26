package com.aistudio.carrerpath.counseling.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactCounsellorScreen(
    appointmentSuccessMessage: String?,
    currentLanguage: String = "en",
    onBookAppointment: (date: String, time: String, counsellorType: String) -> Unit,
    onClearAppointmentMessage: () -> Unit
) {
    val context = LocalContext.current
    val isKn = currentLanguage == "kn"

    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(if (isKn) "10 ಆಗಸ್ಟ್ 2026" else "10th August 2026") }
    var selectedTime by remember { mutableStateOf("03:00 PM") }
    var selectedType by remember { mutableStateOf(if (isKn) "ವೈದ್ಯಕೀಯ ತಜ್ಞ ಕೌನ್ಸಿಲರ್" else "Medical Specialist Counsellor") }

    val times = listOf("10:00 AM", "12:30 PM", "03:00 PM", "05:30 PM")
    val counsellorTypes = if (isKn) listOf("ವೈದ್ಯಕೀಯ ತಜ್ಞ ಕೌನ್ಸಿಲರ್", "ಎಂಜಿನಿಯರಿಂಗ್ ಮತ್ತು ಟೆಕ್ ಕೌನ್ಸಿಲರ್", "ಖಾಸಗಿ & ವಿಶ್ವವಿದ್ಯಾಲಯ ತಜ್ಞ") else listOf("Medical Specialist Counsellor", "Engineering & Tech Counsellor", "Abroad & Deemed Specialist")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Banner
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
                    text = if (isKn) "ನೇರ ಕೌನ್ಸೆಲಿಂಗ್" else "DIRECT COUNSELLING",
                    color = EduBluePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = if (isKn) "ತಜ್ಞರಿಂದ ನೇರ ಕೌನ್ಸೆಲಿಂಗ್" else "Direct Counselling by Experts",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (isKn) "ನಿಮ್ಮ ಪ್ರದೇಶದ ಪರಿಣಿತ ಕೌನ್ಸಿಲರ್‌ಗಳೊಂದಿಗೆ 1-ಆನ್-1 ವೈಯಕ್ತಿಕಗೊಳಿಸಿದ ಪ್ರವೇಶ ಮತ್ತು ವೃತ್ತಿ ಮಾರ್ಗದರ್ಶನ ಪಡೆಯಿರಿ" else "Meet expert counsellors in your region and get personalized 1-on-1 guidance for your education and career.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // Instant Support Options
            Text(if (isKn) "ತಕ್ಷಣದ ಬೆಂಬಲ ಚಾನೆಲ್‌ಗಳು" else "Instant Support Channels", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // WhatsApp Card
            Card(
                colors = CardDefaults.cardColors(containerColor = OpenGreenContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = SuccessGreen,
                        shape = CircleShape,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isKn) "ವಾಟ್ಸಾಪ್ ಕೌನ್ಸಿಲರ್" else "WhatsApp Counsellor", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SuccessGreen)
                        Text(if (isKn) "ಕಟ್ ಆಫ್ ಮತ್ತು ಶುಲ್ಕ ಸಹಾಯಕ್ಕಾಗಿ ಹಿರಿಯ ಸಲಹೆಗಾರರೊಂದಿಗೆ ಲೈವ್ ಚಾಟ್ ಮಾಡಿ" else "Chat live with senior advisors for cutoffs & fee help", fontSize = 12.sp, color = Color.DarkGray)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919876543210?text=Hello%20EduVerse%20Counsellor"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isKn) "ಚಾಟ್ ಮಾಡಿ" else "Chat")
                    }
                }
            }

            // Direct Call Card
            Card(
                colors = CardDefaults.cardColors(containerColor = EduBlueContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EduBluePrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isKn) "ಟೋಲ್-ಫ್ರೀ ಸಹಾಯವಾಣಿ" else "Toll-Free Helpline", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EduBluePrimary)
                        Text("+91 1800-233-4567 • Mon-Sat 9 AM - 8 PM", fontSize = 12.sp, color = Color.DarkGray)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18002334567"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isKn) "ಕರೆ ಮಾಡಿ" else "Call")
                    }
                }
            }

            // Email Support Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EduPurpleSecondary,
                        shape = CircleShape,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isKn) "ಇಮೇಲ್ ಡೆಸ್ಕ್" else "Email Desk", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EduPurpleSecondary)
                        Text("counselor@eduverse.org", fontSize = 12.sp, color = Color.DarkGray)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:counselor@eduverse.org"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EduPurpleSecondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isKn) "ಇಮೇಲ್" else "Email")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Book Appointment Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(if (isKn) "1-ಆನ್-1 ಕೌನ್ಸೆಲಿಂಗ್ ಸೆಷನ್ ಬುಕ್ ಮಾಡಿ" else "Book 1-on-1 Counselling Session", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(if (isKn) "ನಮ್ಮ ಹಿರಿಯ ತಜ್ಞರೊಂದಿಗೆ 30 ನಿಮಿಷಗಳ ಖಾಸಗಿ ಸಮಾಲೋಚನೆ ಸ್ಲಾಟ್ ಕಾಯ್ದಿರಿಸಿ." else "Reserve a 30-minute private consultation slot with our senior experts.", fontSize = 13.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showBookingDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isKn) "ಉಚಿತ ಅಪಾಯಿಂಟ್‌ಮೆಂಟ್ ಸ್ಲಾಟ್ ಬುಕ್ ಮಾಡಿ" else "Book Free Appointment Slot", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    // Appointment Booking Dialog
    if (showBookingDialog) {
        AlertDialog(
            onDismissRequest = { showBookingDialog = false },
            title = { Text(if (isKn) "ಅಪಾಯಿಂಟ್‌ಮೆಂಟ್ ಸ್ಲಾಟ್ ಆಯ್ಕೆಮಾಡಿ" else "Select Appointment Slot", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(if (isKn) "ಕೌನ್ಸಿಲರ್ ವಿಭಾಗ" else "Counsellor Track", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    counsellorTypes.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (selectedType == type),
                                onClick = { selectedType = type }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(type, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(if (isKn) "ಸಮಯದ ಸ್ಲಾಟ್" else "Time Slot", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        times.forEach { slot ->
                            FilterChip(
                                selected = (selectedTime == slot),
                                onClick = { selectedTime = slot },
                                label = { Text(slot, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onBookAppointment(selectedDate, selectedTime, selectedType)
                    showBookingDialog = false
                }) {
                    Text(if (isKn) "ಖಚಿತಪಡಿಸಿ" else "Confirm Slot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookingDialog = false }) {
                    Text(if (isKn) "ರದ್ದುಗೊಳಿಸಿ" else "Cancel")
                }
            }
        )
    }

    // Success Confirmation Dialog
    appointmentSuccessMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = onClearAppointmentMessage,
            title = { Text(if (isKn) "ಅಪಾಯಿಂಟ್‌ಮೆಂಟ್ ಖಚಿತವಾಗಿದೆ! 🎉" else "Appointment Confirmed! 🎉", fontWeight = FontWeight.Bold) },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = onClearAppointmentMessage) {
                    Text(if (isKn) "ಆಯಿತು" else "Done")
                }
            }
        )
    }
}
