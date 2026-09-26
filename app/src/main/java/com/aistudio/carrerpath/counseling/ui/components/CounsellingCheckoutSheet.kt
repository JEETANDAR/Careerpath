package com.aistudio.carrerpath.counseling.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.data.model.CourseEntity
import com.aistudio.carrerpath.counseling.data.model.StudentProfile
import com.aistudio.carrerpath.counseling.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounsellingCheckoutSheet(
    studentProfile: StudentProfile,
    targetCourse: CourseEntity?,
    targetTitle: String? = null,
    bookingCategory: String = "Course Counselling",
    hasPreviousBookings: Boolean = false,
    currentLanguage: String = "en",
    onDismiss: () -> Unit,
    onConfirmBooking: (date: String, timeSlot: String, courseName: String, couponCode: String, feeAmount: Double, paymentMethod: String, counsellorName: String) -> Unit
) {
    val context = LocalContext.current
    val isKn = currentLanguage == "kn"

    val counsellors = remember { listOf("Nimi", "Janardhan", "Xavier", "Aishwarya") }
    var selectedCounsellor by remember { mutableStateOf("Nimi") }

    val defaultTomorrowDate = remember {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.time)
    }

    var customDateText by remember { mutableStateOf(defaultTomorrowDate) }
    var customTimeText by remember { mutableStateOf("10:30 AM - 11:00 AM") }

    val timeOptions = listOf("10:00 AM - 10:30 AM", "11:00 AM - 11:30 AM", "02:00 PM - 02:30 PM", "05:00 PM - 05:30 PM")

    val originalFee = 0.0
    val discount = 0.0
    val finalFee = 0.0

    val finalDateToUse = customDateText.ifBlank { defaultTomorrowDate }
    val finalTimeToUse = customTimeText.ifBlank { "10:30 AM - 11:00 AM" }

    val showDatePickerDialog = {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                customDateText = sdf.format(cal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val showTimePickerDialog = {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val startStr = sdf.format(cal.time)
                cal.add(Calendar.MINUTE, 30)
                val endStr = sdf.format(cal.time)
                customTimeText = "$startStr - $endStr"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
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
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isKn) "ಕೌನ್ಸೆಲಿಂಗ್ ತಪಾಸಣೆ" else "${bookingCategory.uppercase()} CHECKOUT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val headerTitle = when {
                        bookingCategory.contains("Career", ignoreCase = true) -> if (isKn) "1-ಆನ್-1 ವೃತ್ತಿ ಕೌನ್ಸೆಲಿಂಗ್ ಸೆಷನ್" else "1-on-1 Expert Career Counselling"
                        bookingCategory.contains("Course", ignoreCase = true) -> if (isKn) "1-ಆನ್-1 ಕೋರ್ಸ್ ಮಾರ್ಗದರ್ಶನ ಸೆಷನ್" else "1-on-1 Expert Course Guidance"
                        else -> if (isKn) "1-ಆನ್-1 ತಜ್ಞ ಪ್ರವೇಶ ಸೆಷನ್" else "1-on-1 Expert Admission Session"
                    }

                    Text(
                        text = headerTitle,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    val courseTitle = targetTitle ?: targetCourse?.let {
                        if (isKn && it.nameKn.isNotBlank()) it.nameKn else it.name
                    } ?: (if (isKn) "ಎಲ್ಲಾ ಕೋರ್ಸ್‌ಗಳು ಮತ್ತು ಕಟ್ ಆಫ್ ಸಲಹೆ" else "All Courses & Cutoff Advice")

                    Text(
                        text = "${if (isKn) "ಗುರಿ" else "Target"}: $courseTitle",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Student Summary
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, EduCardBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EduBlueContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = EduBluePrimary)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = studentProfile.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${studentProfile.qualification} • ${studentProfile.phone}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Choose Counsellor Section
                Text(
                    text = if (isKn) "ಕೌನ್ಸಿಲರ್ ಆಯ್ಕೆಮಾಡಿ (Select Counsellor) *" else "Choose Your Counsellor *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    counsellors.forEach { name ->
                        val isSelected = selectedCounsellor.equals(name, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EduBluePrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) EduBluePrimary else EduCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCounsellor = name }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = name,
                                    tint = if (isSelected) Color.White else EduBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Select Date (Type directly or tap calendar icon or select chip)
                Text(
                    text = if (isKn) "ದಿನಾಂಕ ನಮೂದಿಸಿ / ಆಯ್ಕೆಮಾಡಿ (DD-MM-YYYY)" else "Enter or Select Date (Format: DD-MM-YYYY)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customDateText,
                    onValueChange = { customDateText = it },
                    readOnly = false,
                    placeholder = { Text(if (isKn) "ಉದಾ. 15-08-2026" else "e.g. 15-08-2026", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = EduBluePrimary) },
                    trailingIcon = {
                        IconButton(onClick = showDatePickerDialog) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick Date from Calendar", tint = EduBluePrimary)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduBluePrimary,
                        unfocusedBorderColor = EduCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date Quick Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presetDates = if (isKn) listOf("ಇಂದು", "ನಾಳೆ", "ನಾಡಿದ್ದು") else listOf("Today", "Tomorrow", "Day After")
                    presetDates.forEach { label ->
                        val chipDate = when (label) {
                            "Today", "ಇಂದು" -> SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
                            "Tomorrow", "ನಾಳೆ" -> SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time)
                            else -> SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }.time)
                        }
                        val isSelected = customDateText == chipDate || (customDateText.isBlank() && label.contains("Tomorrow"))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EduBluePrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) EduBluePrimary else EduCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { customDateText = chipDate }
                        ) {
                            Text(
                                text = "$label ($chipDate)",
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Select Time Slot (Type directly or tap clock icon or select chip)
                Text(
                    text = if (isKn) "ಸಮಯ ನಮೂದಿಸಿ / ಆಯ್ಕೆಮಾಡಿ (HH:MM AM/PM)" else "Enter or Select Time (Format: HH:MM AM/PM)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customTimeText,
                    onValueChange = { customTimeText = it },
                    readOnly = false,
                    placeholder = { Text(if (isKn) "ಉದಾ. 10:30 AM" else "e.g. 10:30 AM", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = EduPurpleSecondary) },
                    trailingIcon = {
                        IconButton(onClick = showTimePickerDialog) {
                            Icon(Icons.Default.Schedule, contentDescription = "Pick Time from Clock", tint = EduPurpleSecondary)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduPurpleSecondary,
                        unfocusedBorderColor = EduCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Time Quick Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    timeOptions.chunked(2).forEach { rowSlots ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            rowSlots.forEach { slot ->
                                val isSelected = customTimeText == slot
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) EduPurpleSecondary else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, if (isSelected) EduPurpleSecondary else EduCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { customTimeText = slot }
                                ) {
                                    Text(
                                        text = slot,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Free Guidance Perks Banner
                Surface(
                    color = OpenGreenContainer,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SuccessGreen,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isKn) "100% ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್ ಸೇವೆ" else "100% Free Expert Counselling",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = if (isKn) "ಯಾವುದೇ ಶುಲ್ಕವಿಲ್ಲ, ಯಾವುದೇ ಪಾವತಿ ಅಗತ್ಯವಿಲ್ಲ" else "No consultation charges or hidden fees.",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fee Summary
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isKn) "ಕೌನ್ಸೆಲಿಂಗ್ ಶುಲ್ಕ:" else "Counselling Fee:", fontSize = 13.sp, color = Color.Gray)
                        Text(if (isKn) "ಉಚಿತ (₹0)" else "FREE (₹0)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isKn) "ಒಟ್ಟು ಪಾವತಿಸಬೇಕಾದದ್ದು:" else "Total Amount:", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = if (isKn) "ಉಚಿತ (₹0)" else "FREE (₹0)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EduBluePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val courseNameForBooking = targetTitle ?: targetCourse?.let {
                            if (isKn && it.nameKn.isNotBlank()) it.nameKn else it.name
                        } ?: "General Career Counselling"
                        val paymentStatus = "Free (100% Sponsored)"
                        onConfirmBooking(
                            finalDateToUse,
                            finalTimeToUse,
                            courseNameForBooking,
                            "",
                            0.0,
                            paymentStatus,
                            selectedCounsellor
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = if (isKn) "ಬುಕಿಂಗ್ ಖಚಿತಪಡಿಸಿ (ಉಚಿತ)" else "Confirm Booking (FREE)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
