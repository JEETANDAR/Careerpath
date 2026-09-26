package com.aistudio.carrerpath.counseling.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.data.model.capitalizeWords
import com.aistudio.carrerpath.counseling.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    currentLanguage: String = "en",
    onToggleLanguage: (String) -> Unit = {},
    onCheckUserRegistration: (phone: String, callback: (Boolean) -> Unit) -> Unit = { _, _ -> },
    onSendPhoneOtp: (activity: Activity, phoneInput: String, onCodeSent: (verificationId: String) -> Unit, onError: (String) -> Unit) -> Unit,
    onResendPhoneOtp: (activity: Activity, phoneInput: String, onCodeSent: (verificationId: String) -> Unit, onError: (String) -> Unit) -> Unit = { act, ph, cb, err -> onSendPhoneOtp(act, ph, cb, err) },
    onVerifyPhoneOtp: (verificationId: String, otpCode: String, phoneNumber: String, onResult: (Boolean, String) -> Unit) -> Unit,
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
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onRegisterWithOtp: (
        verificationId: String,
        otpCode: String,
        name: String,
        phone: String,
        institution: String,
        studentClass: String,
        gender: String,
        city: String,
        state: String,
        country: String,
        onResult: (Boolean, String) -> Unit
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _ -> },
    onAdminLogin: (adminKey: String, onResult: (Boolean, String) -> Unit) -> Unit = { _, _ -> },
    onPrepareSignup: (
        name: String,
        phone: String,
        institution: String,
        studentClass: String,
        gender: String,
        city: String,
        state: String,
        country: String
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onClearPendingSignup: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current

    var isKn by remember(currentLanguage) { mutableStateOf(currentLanguage == "kn") }

    // 0: Sign In, 1: Sign Up, 2: Admin Login
    var selectedAuthTab by remember { mutableIntStateOf(0) }

    // Common OTP State
    var phoneInput by remember { mutableStateOf("") }
    var otpCodeInput by remember { mutableStateOf("") }
    var verificationIdState by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }

    // Sign Up Fields
    var signUpName by remember { mutableStateOf("") }
    var signUpPhone by remember { mutableStateOf("") }
    var signUpInstitution by remember { mutableStateOf("") }
    var signUpStream by remember { mutableStateOf("") }
    var signUpState by remember { mutableStateOf("") }
    var signUpCity by remember { mutableStateOf("") }
    var signUpGender by remember { mutableStateOf("Male") }
    var hasAcceptedTerms by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Admin Fields
    var adminKeyInput by remember { mutableStateOf("") }

    var authError by remember { mutableStateOf<String?>(null) }
    var authSuccessMessage by remember { mutableStateOf<String?>(null) }

    var resendTimer by remember { mutableIntStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, resendTimer) {
        if (isTimerRunning && resendTimer > 0) {
            delay(1000L)
            resendTimer--
        } else if (resendTimer == 0) {
            isTimerRunning = false
        }
    }

    val streamOptions = listOf(
        "10th Pass / High School",
        "12th PCB (Medical)",
        "12th PCM (Engineering)",
        "Commerce & Management",
        "Arts & Humanities",
        "Undergraduate / Degree"
    )

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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Language Switcher Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable {
                        val newLang = if (isKn) "en" else "kn"
                        isKn = !isKn
                        onToggleLanguage(newLang)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = "Language",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isKn) "English 🇬🇧" else "ಕನ್ನಡ 🇮🇳",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // App Branding Header
            Surface(
                color = Color.White.copy(alpha = 0.20f),
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = "CareerPath Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "CareerPath",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (isKn) "ವೃತ್ತಿ ಮತ್ತು ಕಾಲೇಜು ಮಾರ್ಗದರ್ಶನ ಪೋರ್ಟಲ್" else "Career & College Guidance Portal",
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
            )

            // Auth Panel Container Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Tab Selector: Sign Up | Sign In | Admin
                    TabRow(
                        selectedTabIndex = selectedAuthTab,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = EduBluePrimary,
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        Tab(
                            selected = selectedAuthTab == 0,
                            onClick = {
                                selectedAuthTab = 0
                                isOtpSent = false
                                otpCodeInput = ""
                                authError = null
                                authSuccessMessage = null
                            },
                            text = {
                                Text(
                                    if (isKn) "ನೋಂದಣಿ" else "Sign Up",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedAuthTab == 1,
                            onClick = {
                                selectedAuthTab = 1
                                isOtpSent = false
                                otpCodeInput = ""
                                authError = null
                                authSuccessMessage = null
                                onClearPendingSignup()
                            },
                            text = {
                                Text(
                                    if (isKn) "ಲಾಗಿನ್" else "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedAuthTab == 2,
                            onClick = {
                                selectedAuthTab = 2
                                isOtpSent = false
                                otpCodeInput = ""
                                authError = null
                                authSuccessMessage = null
                            },
                            text = {
                                Text(
                                    if (isKn) "ಅಡ್ಮಿನ್" else "Admin",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error Banner
                    authError?.let { err ->
                        val isAppVerification = err.contains("App Verification", ignoreCase = true) || err.contains("Play Integrity", ignoreCase = true)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isAppVerification) Color(0xFFFFF7ED) else Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isAppVerification) Color(0xFFF97316) else Color(0xFFEF5350)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        if (isAppVerification) Icons.Default.WarningAmber else Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = if (isAppVerification) Color(0xFFC2410C) else Color(0xFFD32F2F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        err,
                                        color = if (isAppVerification) Color(0xFF9A3412) else Color(0xFFC62828),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { authError = null },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Dismiss",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Success Banner
                    authSuccessMessage?.let { msg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(msg, color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Panel 1: SIGN IN
                    if (selectedAuthTab == 1) {
                        Text(
                            text = if (isKn) "ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯೊಂದಿಗೆ ಲಾಗಿನ್ ಮಾಡಿ" else "SIGN IN WITH MOBILE PHONE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EduBluePrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isKn) "ನಿಮ್ಮ 10-ಅಂಕೆಗಳ ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯನ್ನು ನಮೂದಿಸಿ." else "Enter your mobile number to receive a 6-digit OTP code.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                        )

                        if (!isOtpSent) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = {
                                    if (it.length <= 10) phoneInput = it.filter { char -> char.isDigit() }
                                    authError = null
                                },
                                label = { Text(if (isKn) "ಮೊಬೈಲ್ ಸಂಖ್ಯೆ" else "Phone Number") },
                                prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = EduBluePrimary) },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EduBluePrimary) },
                                placeholder = { Text("9876543210") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    val cleanPhone = phoneInput.trim()
                                    if (cleanPhone.length < 10) {
                                        authError = if (isKn) "ದಯವಿಟ್ಟು ಮಾನ್ಯವಾದ 10-ಅಂಕೆಗಳ ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯನ್ನು ನಮೂದಿಸಿ." else "Please enter a valid 10-digit mobile phone number."
                                        return@Button
                                    }
                                    val activity = context as? Activity
                                    if (activity == null) {
                                        authError = "Activity context unavailable."
                                        return@Button
                                    }
                                    isSendingOtp = true
                                    authError = null
                                    onClearPendingSignup()

                                    onSendPhoneOtp(
                                        activity,
                                        cleanPhone,
                                        { verId ->
                                            isSendingOtp = false
                                            isOtpSent = true
                                            verificationIdState = verId
                                            resendTimer = 60
                                            isTimerRunning = true
                                            authSuccessMessage = if (isKn) "+91 $cleanPhone ಗೆ SMS ಮೂಲಕ OTP ಕಳುಹಿಸಲಾಗಿದೆ!" else "SMS verification OTP sent to +91 $cleanPhone via Firebase SMS!"
                                        },
                                        { err ->
                                            isSendingOtp = false
                                            authError = err
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                shape = RoundedCornerShape(14.dp),
                                enabled = !isSendingOtp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                if (isSendingOtp) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isKn) "ಒಟಿಪಿ ಕಳುಹಿಸಲಾಗುತ್ತಿದೆ..." else "Sending OTP...", fontSize = 14.sp)
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isKn) "ಒಟಿಪಿ ಪಡೆಯಿರಿ" else "Send OTP", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            // OTP Verification Box
                            OtpVerificationBox(
                                isKn = isKn,
                                phone = phoneInput,
                                otpCodeInput = otpCodeInput,
                                onOtpChange = { otpCodeInput = it },
                                isVerifyingOtp = isVerifyingOtp,
                                onVerifyClick = {
                                    focusManager.clearFocus()
                                    if (otpCodeInput.trim().length != 6) {
                                        authError = if (isKn) "ದಯವಿಟ್ಟು ಸಂಪೂರ್ಣ 6-ಅಂಕೆಗಳ ಒಟಿಪಿಯನ್ನು ನಮೂದಿಸಿ." else "Please enter complete 6-digit OTP code."
                                        return@OtpVerificationBox
                                    }
                                    isVerifyingOtp = true
                                    authError = null
                                    onVerifyPhoneOtp(verificationIdState, otpCodeInput.trim(), phoneInput.trim()) { success, msg ->
                                        isVerifyingOtp = false
                                        if (!success) authError = msg
                                    }
                                },
                                isTimerRunning = isTimerRunning,
                                resendTimer = resendTimer,
                                onResendClick = {
                                    val activity = context as? Activity ?: return@OtpVerificationBox
                                    isSendingOtp = true
                                    onResendPhoneOtp(activity, phoneInput.trim(), { verId ->
                                        isSendingOtp = false
                                        verificationIdState = verId
                                        resendTimer = 60
                                        isTimerRunning = true
                                        authSuccessMessage = if (isKn) "ಹೊಸ OTP SMS ಮೂಲಕ ಕಳುಹಿಸಲಾಗಿದೆ!" else "New verification OTP sent via Firebase SMS!"
                                    }, { err ->
                                        isSendingOtp = false
                                        authError = err
                                    })
                                },
                                onChangePhoneClick = {
                                    isOtpSent = false
                                    otpCodeInput = ""
                                }
                            )
                        }
                    }

                    // Panel 0: SIGN UP (Enter details -> Register & Send OTP -> Verify -> Home)
                    if (selectedAuthTab == 0) {
                        Text(
                            text = if (isKn) "ಹೊಸ ವಿದ್ಯಾರ್ಥಿ ನೋಂದಣಿ" else "NEW STUDENT REGISTRATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EduBluePrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isKn) "ನಿಮ್ಮ ಪೂರ್ಣ ವಿವರಗಳನ್ನು ನಮೂದಿಸಿ ಮತ್ತು ಒಟಿಪಿ ಪಡೆಯಿರಿ." else "Fill in your details first, then verify mobile OTP to register.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                        )

                        if (!isOtpSent) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = signUpName,
                                    onValueChange = { signUpName = it.capitalizeWords(); authError = null },
                                    label = { Text(if (isKn) "ಪೂರ್ಣ ಹೆಸರು *" else "Full Name *") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EduBluePrimary) },
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = signUpPhone,
                                    onValueChange = {
                                        if (it.length <= 10) signUpPhone = it.filter { char -> char.isDigit() }
                                        authError = null
                                    },
                                    label = { Text(if (isKn) "ಮೊಬೈಲ್ ಸಂಖ್ಯೆ *" else "Mobile Phone *") },
                                    prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = EduBluePrimary) },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EduBluePrimary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = signUpInstitution,
                                    onValueChange = { signUpInstitution = it.capitalizeWords() },
                                    label = { Text(if (isKn) "ಶಾಲಾ / ಕಾಲೇಜು ಸಂಸ್ಥೆ *" else "School / College Name *") },
                                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = EduBluePrimary) },
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = signUpStream,
                                    onValueChange = { signUpStream = it.capitalizeWords() },
                                    label = { Text(if (isKn) "ತರಗತಿ / ಅರ್ಹತೆ *" else "Class / Standard / Qualification *") },
                                    leadingIcon = { Icon(Icons.Default.Class, contentDescription = null, tint = EduBluePrimary) },
                                    placeholder = { Text("e.g. 10th Standard, 12th Standard, B.Tech") },
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = signUpState,
                                    onValueChange = { signUpState = it.capitalizeWords() },
                                    label = { Text(if (isKn) "ರಾಜ್ಯ *" else "State *") },
                                    leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = EduBluePrimary) },
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = signUpCity,
                                    onValueChange = { signUpCity = it.capitalizeWords() },
                                    label = { Text(if (isKn) "ನಗರ / ಜಿಲ್ಲೆ *" else "City / District *") },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EduBluePrimary) },
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                // Terms & Conditions / Privacy Policy Acceptance Box
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, if (authError?.contains("Terms") == true || authError?.contains("ನಿಯಮಗಳು") == true) Color.Red.copy(alpha = 0.5f) else Color.Transparent),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Checkbox(
                                            checked = hasAcceptedTerms,
                                            onCheckedChange = {
                                                hasAcceptedTerms = it
                                                if (it) authError = null
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = EduBluePrimary,
                                                uncheckedColor = EduBluePrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = if (isKn) "ನಾನು " else "I accept the ",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (isKn) "ನಿಯಮಗಳು & ಷರತ್ತುಗಳು" else "Terms & Conditions",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EduBluePrimary,
                                                modifier = Modifier.clickable { showTermsDialog = true }
                                            )
                                            Text(
                                                text = if (isKn) " ಮತ್ತು " else " & ",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (isKn) "ಗೌಪ್ಯತಾ ನೀತಿ" else "Privacy Policy",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EduBluePrimary,
                                                modifier = Modifier.clickable { showTermsDialog = true }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (signUpName.isBlank() || signUpPhone.isBlank() || signUpInstitution.isBlank() || signUpStream.isBlank() || signUpState.isBlank() || signUpCity.isBlank()) {
                                            authError = if (isKn) "ದಯವಿಟ್ಟು ಎಲ್ಲಾ ಅಗತ್ಯ ವಿವರಗಳನ್ನು ಭರ್ತಿ ಮಾಡಿ." else "Please fill in all mandatory fields (Name, Phone, School, Class, State, City)."
                                            return@Button
                                        }
                                        if (!hasAcceptedTerms) {
                                            authError = if (isKn) "ದಯವಿಟ್ಟು ಮುಂದುವರಿಯಲು ನಿಯಮಗಳು ಮತ್ತು ಗೌಪ್ಯತಾ ನೀತಿಯನ್ನು ಒಪ್ಪಿಕೊಳ್ಳಿ." else "Please accept the Terms & Conditions and Privacy Policy to continue."
                                            return@Button
                                        }
                                        if (signUpPhone.length < 10) {
                                            authError = if (isKn) "ಮಾನ್ಯವಾದ 10-ಅಂಕೆಗಳ ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯನ್ನು ನಮೂದಿಸಿ." else "Please enter a valid 10-digit mobile number."
                                            return@Button
                                        }

                                        val activity = context as? Activity
                                        if (activity == null) {
                                            authError = "Activity context unavailable."
                                            return@Button
                                        }

                                        isSendingOtp = true
                                        authError = null

                                        onPrepareSignup(
                                            signUpName.trim(),
                                            signUpPhone.trim(),
                                            signUpInstitution.trim(),
                                            signUpStream.trim().ifBlank { "12th Standard" },
                                            signUpGender,
                                            signUpCity.trim(),
                                            signUpState.trim().ifBlank { "Karnataka" },
                                            "India"
                                        )

                                        phoneInput = signUpPhone
                                        onSendPhoneOtp(
                                            activity,
                                            signUpPhone,
                                            { verId ->
                                                isSendingOtp = false
                                                isOtpSent = true
                                                verificationIdState = verId
                                                resendTimer = 60
                                                isTimerRunning = true
                                                authSuccessMessage = if (isKn) "+91 $signUpPhone ಗೆ SMS ಮೂಲಕ OTP ಕಳುಹಿಸಲಾಗಿದೆ!" else "SMS verification OTP sent to +91 $signUpPhone via Firebase SMS!"
                                            },
                                            { err ->
                                                isSendingOtp = false
                                                authError = err
                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    enabled = !isSendingOtp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    if (isSendingOtp) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isKn) "ಒಟಿಪಿ ಕಳುಹಿಸಲಾಗುತ್ತಿದೆ..." else "Sending OTP...", fontSize = 14.sp)
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AppRegistration, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(if (isKn) "ನೋಂದಾಯಿಸಿ ಒಟಿಪಿ ಪಡೆಯಿರಿ" else "Register & Send OTP", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Verify OTP and Save Complete Profile
                            OtpVerificationBox(
                                isKn = isKn,
                                phone = signUpPhone,
                                otpCodeInput = otpCodeInput,
                                onOtpChange = { otpCodeInput = it },
                                isVerifyingOtp = isVerifyingOtp,
                                onVerifyClick = {
                                    focusManager.clearFocus()
                                    if (otpCodeInput.trim().length != 6) {
                                        authError = if (isKn) "6-ಅಂಕೆಗಳ ಒಟಿಪಿ ನಮೂದಿಸಿ." else "Enter complete 6-digit OTP code."
                                        return@OtpVerificationBox
                                    }
                                    isVerifyingOtp = true
                                    authError = null

                                    onRegisterWithOtp(
                                        verificationIdState,
                                        otpCodeInput.trim(),
                                        signUpName.trim(),
                                        signUpPhone.trim(),
                                        signUpInstitution.trim(),
                                        signUpStream.trim().ifBlank { "12th Standard" },
                                        signUpGender,
                                        signUpCity.trim(),
                                        signUpState.trim().ifBlank { "Karnataka" },
                                        "India"
                                    ) { success, msg ->
                                        isVerifyingOtp = false
                                        if (!success) {
                                            authError = msg
                                        }
                                    }
                                },
                                isTimerRunning = isTimerRunning,
                                resendTimer = resendTimer,
                                onResendClick = {
                                    val activity = context as? Activity ?: return@OtpVerificationBox
                                    isSendingOtp = true
                                    onResendPhoneOtp(activity, signUpPhone.trim(), { verId ->
                                        isSendingOtp = false
                                        verificationIdState = verId
                                        resendTimer = 60
                                        isTimerRunning = true
                                        authSuccessMessage = if (isKn) "ಹೊಸ OTP SMS ಮೂಲಕ ಕಳುಹಿಸಲಾಗಿದೆ!" else "New verification OTP sent via Firebase SMS!"
                                    }, { err ->
                                        isSendingOtp = false
                                        authError = err
                                    })
                                },
                                onChangePhoneClick = {
                                    isOtpSent = false
                                    otpCodeInput = ""
                                }
                            )
                        }
                    }

                    // Panel 2: ADMIN LOGIN
                    if (selectedAuthTab == 2) {
                        Text(
                            text = if (isKn) "ಅಡ್ಮಿನ್ ಕಾನ್ಸೋಲ್ ಲಾಗಿನ್" else "ADMINISTRATOR CONSOLE ACCESS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE11D48),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isKn) "ನಿರ್ವಹಣಾ ಫಲಕವನ್ನು ಪ್ರವೇಶಿಸಲು ಅಡ್ಮಿನ್ ರುಜುವಾತುಗಳನ್ನು ನಮೂದಿಸಿ." else "Enter the admin credentials to open management panel.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = adminKeyInput,
                            onValueChange = { adminKeyInput = it; authError = null },
                            label = { Text(if (isKn) "ಅಡ್ಮಿನ್ ರುಜುವಾತುಗಳು" else "Admin Credentials") },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFE11D48)) },
                            placeholder = { Text(if (isKn) "ಅಡ್ಮಿನ್ ರುಜುವಾತುಗಳನ್ನು ನಮೂದಿಸಿ" else "Enter the admin credentials") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onAdminLogin(adminKeyInput) { ok, msg ->
                                    if (!ok) authError = msg
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isKn) "ಅಡ್ಮಿನ್ ಕಾನ್ಸೋಲ್‌ಗೆ ಲಾಗಿನ್ ಮಾಡಿ" else "Access Admin Executive Panel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        if (showTermsDialog) {
            TermsAndPrivacyDialog(
                isKn = isKn,
                onAccept = {
                    hasAcceptedTerms = true
                    authError = null
                    showTermsDialog = false
                },
                onDismiss = { showTermsDialog = false }
            )
        }
    }
}

@Composable
fun TermsAndPrivacyDialog(
    isKn: Boolean,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isKn) "ನಿಯಮಗಳು & ಗೌಪ್ಯತಾ ನೀತಿ" else "Terms & Privacy Policy",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Tab Row: Terms & Conditions vs Privacy Policy
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = EduBluePrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                if (isKn) "ನಿಯಮಗಳು & ಷರತ್ತುಗಳು" else "Terms & Conditions",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                if (isKn) "ಗೌಪ್ಯತಾ ನೀತಿ" else "Privacy Policy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    if (selectedTab == 0) {
                        // TERMS & CONDITIONS
                        Text(
                            text = if (isKn) "೧. ಶೈಕ್ಷಣಿಕ & ವೃತ್ತಿ ಕೌನ್ಸೆಲಿಂಗ್ ಉದ್ದೇಶ" else "1. Educational & Career Counseling Purpose",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EduBluePrimary
                        )
                        Text(
                            text = if (isKn)
                                "CareerPath ಅಪ್ಲಿಕೇಶನ್ ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ ಉತ್ತಮ ವೃತ್ತಿ ಮಾರ್ಗಗಳು, ಕಾಲೇಜು ಪ್ರವೇಶ ಕಟ್‌ಆಫ್‌ಗಳು, ಪರೀಕ್ಷೆಗಳು ಮತ್ತು ಸ್ಕಾಲರ್‌ಶಿಪ್ ಮಾರ್ಗದರ್ಶನ ನೀಡುವ ಶೈಕ್ಷಣಿಕ ವೇದಿಕೆಯಾಗಿದೆ."
                            else
                                "CareerPath is designed exclusively to provide students and parents with academic guidance, college admission cutoffs, entrance exam timelines, psychometric evaluations, and expert counseling.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text(
                            text = if (isKn) "೨. ಬಳಕೆದಾರರ ವಿವರಗಳ ನಿಖರತೆ" else "2. User Information Accuracy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EduBluePrimary
                        )
                        Text(
                            text = if (isKn)
                                "ವೈಯಕ್ತಿಕೃತ ಫಲಿತಾಂಶ ಮತ್ತು ಕೌನ್ಸೆಲಿಂಗ್ ಸೇವೆ ಪಡೆಯಲು ನಿಖರವಾದ ಶಾಲಾ/ಕಾಲೇಜು, ತರಗತಿ ಹಾಗೂ ಸಂಪರ್ಕ ವಿವರಗಳನ್ನು ನಮೂದಿಸಬೇಕಾಗುತ್ತದೆ."
                            else
                                "To generate accurate cutoff recommendations, personalized roadmaps, and verified counseling sessions, students agree to provide genuine academic details.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text(
                            text = if (isKn) "೩. ಗೌಪ್ಯತೆ ಮತ್ತು ಭದ್ರತೆ" else "3. Student Trust & Security",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EduBluePrimary
                        )
                        Text(
                            text = if (isKn)
                                "ನಿಮ್ಮ ನೋಂದಾಯಿತ ಮಾಹಿತಿಯನ್ನು ಯಾವುದೇ ವಾಣಿಜ್ಯ ಸಂಸ್ಥೆಗಳಿಗೆ ಮಾರಾಟ ಮಾಡುವುದಿಲ್ಲ. ಇದು ಸಂಪೂರ್ಣವಾಗಿ ಕೌನ್ಸೆಲಿಂಗ್ ಮತ್ತು ಶೈಕ್ಷಣಿಕ ಉದ್ದೇಶಕ್ಕೆ ಸೀಮಿತವಾಗಿದೆ."
                            else
                                "All registered profile details are securely protected and strictly restricted to in-app career guidance. We maintain a zero-tolerance policy against sharing student data with unauthorized 3rd parties.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                    } else {
                        // PRIVACY POLICY
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isKn) "೧೦೦% ಕೌನ್ಸೆಲಿಂಗ್ ಉದ್ದೇಶಕ್ಕೆ ಮಾತ್ರ ಸೀಮಿತ ದತ್ತಾಂಶ ನೀತಿ" else "100% Dedicated Counseling Data Protection Guarantee",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }

                        Text(
                            text = if (isKn) "೧. ನಾವು ಯಾವ ಡೇಟಾ ಸಂಗ್ರಹಿಸುತ್ತೇವೆ?" else "1. Data We Collect",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EduBluePrimary
                        )
                        Text(
                            text = if (isKn)
                                "ವಿದ್ಯಾರ್ಥಿಯ ಹೆಸರು, ಮೊಬೈಲ್ ಸಂಖ್ಯೆ, ಶಾಲಾ/ಕಾಲೇಜು ಹೆಸರು, ತರಗತಿ, ರಾಜ್ಯ/ಜಿಲ್ಲೆ ಮತ್ತು ಸೈಕೋಮೆಟ್ರಿಕ್ ಪರೀಕ್ಷಾ ಪ್ರತಿಕ್ರಿಯೆಗಳು."
                            else
                                "Full Name, Mobile Phone Number, School/College Name, Academic Qualification, State/City, and Psychometric Assessment Responses.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text(
                            text = if (isKn) "೨. ಡೇಟಾ ಬಳಕೆಯ ಉದ್ದೇಶ (ಕೌನ್ಸೆಲಿಂಗ್ ಉದ್ದೇಶಕ್ಕೆ ಮಾತ್ರ)" else "2. Purpose of Data Usage (Counseling Only)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EduBluePrimary
                        )
                        Text(
                            text = if (isKn)
                                "ಸಂಗ್ರಹಿಸಿದ ಎಲ್ಲಾ ವಿವರಗಳನ್ನು ಕೇವಲ ಈ ಕೆಳಗಿನ ಶೈಕ್ಷಣಿಕ & ಕೌನ್ಸೆಲಿಂಗ್ ಉದ್ದೇಶಗಳಿಗೆ ಮಾತ್ರ ಬಳಸಲಾಗುತ್ತದೆ:\n• ನಿಮ್ಮ ಸಾಮರ್ಥ್ಯಕ್ಕೆ ತಕ್ಕಂತೆ ಸೂಕ್ತ ವೃತ್ತಿ ಮತ್ತು ಕೋರ್ಸ್‌ಗಳನ್ನು ಶಿಫಾರಸು ಮಾಡಲು\n• ಕಾಲೇಜು ಕಟ್‌ಆಫ್‌ಗಳು ಮತ್ತು ಶುಲ್ಕ ವಿವರಗಳನ್ನು ಹೊಂದಿಸಲು\n• ೧-ಆನ್-೧ ತಜ್ಞ ಕೌನ್ಸೆಲಿಂಗ್ ಸೆಷನ್‌ಗಳನ್ನು ನಿಗದಿಪಡಿಸಲು\n• ಅರ್ಹ ಸ್ಕಾಲರ್‌ಶಿಪ್ ಹಾಗೂ ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳ ಮಾಹಿತಿ ನೀಡಲು\n• ಮೋಜಿ (Moji) AI ಅಸಿಸ್ಟೆಂಟ್ ಮೂಲಕ ವೈಯಕ್ತಿಕ ಉತ್ತರಗಳನ್ನು ನೀಡಲು."
                            else
                                "All collected student data is used STRICTLY and EXCLUSIVELY within this application for educational counseling purposes:\n• Generating personalized career and degree course recommendations\n• Matching colleges, cutoffs, ranks, and quota options\n• Scheduling 1-on-1 expert counseling appointments\n• Alerting about relevant government and private scholarships\n• Powering personalized guidance with Moji AI Companion.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text(
                            text = if (isKn) "೩. ಮೂರನೇ ವ್ಯಕ್ತಿಗಳಿಗೆ ಮಾರಾಟವಿಲ್ಲ" else "3. Zero Commercial Selling / Sharing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EduBluePrimary
                        )
                        Text(
                            text = if (isKn)
                                "ನಿಮ್ಮ ಯಾವುದೇ ವೈಯಕ್ತಿಕ ವಿವರಗಳನ್ನು ಜಾಹೀರಾತುದಾರರಿಗೆ ಅಥವಾ ಮೂರನೇ ವ್ಯಕ್ತಿಗಳಿಗೆ ಮಾರಾಟ ಮಾಡಲಾಗುವುದಿಲ್ಲ."
                            else
                                "We DO NOT sell, rent, lease, or distribute your personal data to commercial advertisers or unrelated marketing companies.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                    }
                }

                // Accept & Close Button Footer
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isKn) "ಮುಚ್ಚಿ" else "Close")
                        }

                        Button(
                            onClick = onAccept,
                            modifier = Modifier
                                .weight(1.4f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isKn) "ನಾನು ಒಪ್ಪುತ್ತೇನೆ" else "I Accept", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OtpVerificationBox(
    isKn: Boolean,
    phone: String,
    otpCodeInput: String,
    onOtpChange: (String) -> Unit,
    isVerifyingOtp: Boolean,
    onVerifyClick: () -> Unit,
    isTimerRunning: Boolean,
    resendTimer: Int,
    onResendClick: () -> Unit,
    onChangePhoneClick: () -> Unit
) {
    Surface(
        color = Color(0xFFF0F7FF),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, EduBlueContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isKn) "6-ಅಂಕೆಗಳ ಒಟಿಪಿ ನಮೂದಿಸಿ (+91 $phone)" else "VERIFY 6-DIGIT OTP (+91 $phone)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EduBluePrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic SMS / Test OTP Info Banner
            val isTestPhone = phone.filter { it.isDigit() }.let { d ->
                d == "9876543210" || d == "919876543210" || d == "09876543210"
            }
            Surface(
                color = if (isTestPhone) Color(0xFFFEF3C7) else Color(0xFFE0F2FE),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, if (isTestPhone) Color(0xFFD97706) else Color(0xFF0284C7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isTestPhone) Icons.Default.Science else Icons.Default.Sms,
                            contentDescription = null,
                            tint = if (isTestPhone) Color(0xFFB45309) else EduBluePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isTestPhone) {
                                    if (isKn) "🧪 ಫೈರ್‌ಬೇಸ್ ಪರೀಕ್ಷಾ ಸಂಖ್ಯೆ" else "🧪 FIREBASE TEST NUMBER"
                                } else {
                                    if (isKn) "📲 ನೈಜ-ಸಮಯದ SMS OTP ರವಾನಿಸಲಾಗಿದೆ" else "📲 REAL-TIME SMS OTP DISPATCHED"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTestPhone) Color(0xFFB45309) else EduBluePrimary
                            )
                            Text(
                                text = if (isTestPhone) {
                                    if (isKn) "+91 $phone ಪರೀಕ್ಷಾ ಸಂಖ್ಯೆಯಾಗಿದೆ. ಪರೀಕ್ಷಾ ಒಟಿಪಿ 123456 ನಮೂದಿಸಿ." else "+91 $phone is the test number. Enter test OTP 123456."
                                } else {
                                    if (isKn) "+91 $phone ಗೆ SMS ಮೂಲಕ ಬಂದಿರುವ 6-ಅಂಕೆಗಳ ನೈಜ OTP ಕೋಡ್ ನಮೂದಿಸಿ." else "Enter the 6-digit real-time OTP code received via SMS on +91 $phone."
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = otpCodeInput,
                onValueChange = { if (it.length <= 6) onOtpChange(it.filter { char -> char.isDigit() }) },
                label = { Text("6-Digit OTP") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = EduBluePrimary) },
                placeholder = { Text("• • • • • •") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onVerifyClick,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = !isVerifyingOtp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isVerifyingOtp) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isKn) "ಪರಿಶೀಲಿಸಲಾಗುತ್ತಿದೆ..." else "Verifying...", fontSize = 14.sp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isKn) "ಒಟಿಪಿ ಪರಿಶೀಲಿಸಿ ಲಾಗಿನ್ ಮಾಡಿ" else "Verify OTP & Continue", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isTimerRunning) {
                    Text(if (isKn) "${resendTimer}s ಬಳಿಕ ಒಟಿಪಿ ಮರುಕಳುಹಿಸಿ" else "Resend in ${resendTimer}s", fontSize = 11.sp, color = Color.Gray)
                } else {
                    TextButton(onClick = onResendClick) {
                        Text(if (isKn) "ಒಟಿಪಿ ಮರುಕಳುಹಿಸಿ" else "Resend OTP", fontSize = 11.sp, color = EduBluePrimary, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onChangePhoneClick) {
                    Text(if (isKn) "ಮೊಬೈಲ್ ಸಂಖ್ಯೆ ಬದಲಾಯಿಸಿ" else "Change Phone", fontSize = 11.sp, color = Color.DarkGray)
                }
            }
        }
    }
}
