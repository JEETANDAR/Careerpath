package com.aistudio.carrerpath.counseling

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.carrerpath.counseling.ui.components.*
import com.aistudio.carrerpath.counseling.ui.screens.*
import com.aistudio.carrerpath.counseling.ui.theme.*
import com.aistudio.carrerpath.counseling.ui.viewmodel.AppNavTab
import com.aistudio.carrerpath.counseling.ui.viewmodel.EduViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EduViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
            val profile by viewModel.userProfile.collectAsStateWithLifecycle()
            val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
            val verifiedPhone by viewModel.verifiedPhone.collectAsStateWithLifecycle()

            val allCareers by viewModel.allCareers.collectAsStateWithLifecycle()
            val selectedCareer by viewModel.selectedCareer.collectAsStateWithLifecycle()

            val featuredColleges by viewModel.featuredColleges.collectAsStateWithLifecycle()
            val filteredColleges by viewModel.filteredColleges.collectAsStateWithLifecycle()
            val filteredCourses by viewModel.filteredCourses.collectAsStateWithLifecycle()
            val allCourses by viewModel.allCourses.collectAsStateWithLifecycle()
            val scholarships by viewModel.allScholarships.collectAsStateWithLifecycle()
            val notifications by viewModel.studentNotifications.collectAsStateWithLifecycle()

            val savedCollegeIds by viewModel.savedCollegeIds.collectAsStateWithLifecycle()
            val savedColleges by viewModel.savedColleges.collectAsStateWithLifecycle()

            val collegeSearchQuery by viewModel.collegeSearchQuery.collectAsStateWithLifecycle()
            val courseSearchQuery by viewModel.courseSearchQuery.collectAsStateWithLifecycle()
            val selectedCategory by viewModel.selectedCourseCategory.collectAsStateWithLifecycle()

            val targetCollegeForAdmission by viewModel.targetCollegeForAdmission.collectAsStateWithLifecycle()
            val targetCourseForAdmission by viewModel.targetCourseForAdmission.collectAsStateWithLifecycle()
            val formSuccessMessage by viewModel.formSubmissionSuccess.collectAsStateWithLifecycle()
            val studentForms by viewModel.studentAdmissionForms.collectAsStateWithLifecycle()

            val allStudentsAdmin by viewModel.allStudents.collectAsStateWithLifecycle()
            val isRefreshingStudents by viewModel.isRefreshingStudents.collectAsStateWithLifecycle()
            val allCounsellingBookingsAdmin by viewModel.allCounsellingBookings.collectAsStateWithLifecycle()
            val studentBookings by viewModel.studentBookings.collectAsStateWithLifecycle()

            val allAdmissionFormsAdmin by viewModel.allAdmissionForms.collectAsStateWithLifecycle()
            val allCollegesAdmin by viewModel.allColleges.collectAsStateWithLifecycle()

            // Modals & Sheets
            val selectedCourseSheet by viewModel.selectedCourse.collectAsStateWithLifecycle()
            val counsellingCheckoutCourse by viewModel.counsellingCheckoutCourse.collectAsStateWithLifecycle()
            val counsellingCheckoutTargetTitle by viewModel.counsellingCheckoutTargetTitle.collectAsStateWithLifecycle()
            val counsellingCheckoutCategory by viewModel.counsellingCheckoutCategory.collectAsStateWithLifecycle()
            val isCounsellingCheckoutOpen by viewModel.isCounsellingCheckoutOpen.collectAsStateWithLifecycle()
            val showAiChatbot by viewModel.showAiChatbot.collectAsStateWithLifecycle()
            val chatInitialPrompt by viewModel.chatInitialPrompt.collectAsStateWithLifecycle()
            val bookingSuccessMessage by viewModel.bookingSuccessMessage.collectAsStateWithLifecycle()

            val studentAssessment by viewModel.studentAssessment.collectAsStateWithLifecycle()
            val showPsychometricTestDialog by viewModel.showPsychometricTestDialog.collectAsStateWithLifecycle()
            val showPsychometricResultSheet by viewModel.showPsychometricResultSheet.collectAsStateWithLifecycle()
            val showPostLoginAssessmentPrompt by viewModel.showPostLoginAssessmentPrompt.collectAsStateWithLifecycle()
            val activeAssessmentResult by viewModel.activeAssessmentResult.collectAsStateWithLifecycle()

            val syncResultMessage by viewModel.syncResultMessage.collectAsStateWithLifecycle()
            val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

            val isDeletingAccount by viewModel.isDeletingAccount.collectAsStateWithLifecycle()
            val accountDeletionMessage by viewModel.accountDeletionMessage.collectAsStateWithLifecycle()

            val unreadCount = remember(notifications) { notifications.count { !it.isRead } }

            // Display Toast on Counselling Booking Confirmation
            LaunchedEffect(bookingSuccessMessage) {
                bookingSuccessMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    viewModel.clearBookingSuccessMessage()
                }
            }

            // Display Toast on Cloud Sync Result
            LaunchedEffect(syncResultMessage) {
                syncResultMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    viewModel.clearSyncResultMessage()
                }
            }

            // Display Toast on Account Deletion Result
            LaunchedEffect(accountDeletionMessage) {
                accountDeletionMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    viewModel.clearAccountDeletionMessage()
                }
            }

            var hasShownLaunchOffer by remember { mutableStateOf(false) }

            if (isLoggedIn && !profile.isAdmin && !hasShownLaunchOffer) {
                AlertDialog(
                    onDismissRequest = { hasShownLaunchOffer = true },
                    icon = {
                        Icon(
                            Icons.Default.Celebration,
                            contentDescription = null,
                            tint = EduBluePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    title = {
                        Text(
                            text = if (currentLanguage == "kn") "ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್ ಪಡೆಯಿರಿ!" else "Get Free Counselling Now!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentLanguage == "kn") "ನಿಮ್ಮ ಗುರಿಯ ಕಾಲೇಜುಗಳು ಮತ್ತು ಕೋರ್ಸ್‌ಗಳಿಗೆ 1-ಆನ್-1 ತಜ್ಞರ ಶೈಕ್ಷಣಿಕ ಮತ್ತು ವೃತ್ತಿ ಮಾರ್ಗದರ್ಶನವನ್ನು ಅನ್‌ಲಾಕ್ ಮಾಡಿ." else "Unlock 1-on-1 expert academic & career guidance tailored for your target colleges and courses.",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                hasShownLaunchOffer = true
                                viewModel.openCounsellingCheckout(targetTitle = "1-on-1 Expert Guidance", category = "General Counselling")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (currentLanguage == "kn") "ಈಗ ಉಚಿತ ಕೌನ್ಸೆಲಿಂಗ್ ಪಡೆಯಿರಿ" else "Get Free Counselling Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { hasShownLaunchOffer = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (currentLanguage == "kn") "ರದ್ದುಗೊಳಿಸಿ" else "Cancel", fontSize = 13.sp)
                        }
                    }
                )
            }

            EduVerseTheme(darkTheme = isDarkMode) {
                BackHandler(enabled = isLoggedIn && (currentTab != AppNavTab.HOME && currentTab != AppNavTab.ADMIN)) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    topBar = {
                        if (isLoggedIn && currentTab != AppNavTab.HOME && currentTab != AppNavTab.ADMIN && currentTab != AppNavTab.ONBOARDING && currentTab != AppNavTab.AUTH && currentTab != AppNavTab.SIGNUP && currentTab != AppNavTab.CAREER_DETAIL) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = when (currentTab) {
                                            AppNavTab.CAREERS -> if (currentLanguage == "kn") "ಉದ್ಯೋಗಗಳು & ವೃತ್ತಿ" else "Top Careers"
                                            AppNavTab.COLLEGES -> if (currentLanguage == "kn") "ಕಾಲೇಜುಗಳು" else "College Explorer"
                                            AppNavTab.COURSES -> if (currentLanguage == "kn") "ಕೋರ್ಸ್‌ಗಳು" else "Course Finder"
                                            AppNavTab.ADMISSION_FORM -> if (currentLanguage == "kn") "ಅರ್ಜಿ ನಮೂನೆ" else "Admission Application"
                                            AppNavTab.DASHBOARD -> if (currentLanguage == "kn") "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್" else "Student Dashboard"
                                            AppNavTab.ADMIN -> "Admin Dashboard"
                                            AppNavTab.NOTIFICATIONS -> "Notifications"
                                            AppNavTab.SETTINGS -> "Profile & Settings"
                                            else -> "EduVerse"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { viewModel.navigateBack() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { viewModel.navigateTo(AppNavTab.SETTINGS) }) {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (isLoggedIn && currentTab != AppNavTab.ONBOARDING && currentTab != AppNavTab.AUTH && currentTab != AppNavTab.SIGNUP) {
                            EduNavBar(
                                currentTab = currentTab,
                                isAdmin = profile.isAdmin,
                                currentLanguage = currentLanguage,
                                onTabSelected = { viewModel.navigateTo(it) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(
                            targetState = when {
                                isLoggedIn && (currentTab == AppNavTab.ONBOARDING || currentTab == AppNavTab.AUTH) -> if (profile.isAdmin) AppNavTab.ADMIN else AppNavTab.HOME
                                else -> currentTab
                            },
                            label = "ScreenTransition"
                        ) { tab ->
                            when (tab) {
                                AppNavTab.ONBOARDING -> {
                                    OnboardingScreen(
                                        onFinish = { viewModel.completeOnboarding() }
                                    )
                                }

                                AppNavTab.AUTH -> {
                                    AuthScreen(
                                        currentLanguage = currentLanguage,
                                        onToggleLanguage = { lang -> viewModel.setLanguage(lang) },
                                        onCheckUserRegistration = { phone, callback ->
                                            viewModel.checkUserRegistration(phone, callback)
                                        },
                                        onSendPhoneOtp = { activity, phoneInput, onCodeSent, onError ->
                                            viewModel.sendPhoneOtp(
                                                activity = activity,
                                                phoneInput = phoneInput,
                                                onCodeSent = onCodeSent,
                                                onAutoVerified = {
                                                    // Instant auto-verification succeeded
                                                },
                                                onError = onError
                                            )
                                        },
                                        onResendPhoneOtp = { activity, phoneInput, onCodeSent, onError ->
                                            viewModel.resendPhoneOtp(
                                                activity = activity,
                                                phoneInput = phoneInput,
                                                onCodeSent = onCodeSent,
                                                onAutoVerified = {
                                                    // Instant auto-verification succeeded
                                                },
                                                onError = onError
                                            )
                                        },
                                        onVerifyPhoneOtp = { verificationId, code, phone, callback ->
                                            viewModel.verifyPhoneOtp(verificationId, code, phone, callback)
                                        },
                                        onCompleteSignup = { name, phone, inst, studentClass, gender, city, state, country, callback ->
                                            viewModel.completeSignupProfile(name, phone, inst, studentClass, gender, city, state, country, 85.0, callback)
                                        },
                                        onRegisterWithOtp = { verificationId, code, name, phone, inst, studentClass, gender, city, state, country, callback ->
                                            viewModel.registerStudentWithOtp(verificationId, code, name, phone, inst, studentClass, gender, city, state, country, callback)
                                        },
                                        onAdminLogin = { adminKey, callback ->
                                            viewModel.loginAsAdmin(adminKey, callback)
                                        },
                                        onPrepareSignup = { name, phone, inst, studentClass, gender, city, state, country ->
                                            viewModel.prepareSignup(name, phone, inst, studentClass, gender, city, state, country)
                                        },
                                        onClearPendingSignup = {
                                            viewModel.clearPendingSignup()
                                        }
                                    )
                                }

                                AppNavTab.SIGNUP -> {
                                    SignupScreen(
                                        prefilledPhone = verifiedPhone,
                                        onCompleteSignup = { name, phone, inst, studentClass, gender, city, state, country, callback ->
                                            viewModel.completeSignupProfile(name, phone, inst, studentClass, gender, city, state, country, 85.0, callback)
                                        }
                                    )
                                }

                                AppNavTab.HOME -> {
                                    HomeScreen(
                                        studentName = profile.name,
                                        studentQualification = profile.qualification,
                                        careers = allCareers,
                                        featuredColleges = featuredColleges,
                                        allCourses = allCourses,
                                        scholarships = scholarships,
                                        assessment = studentAssessment,
                                        unreadNotificationCount = unreadCount,
                                        currentLanguage = currentLanguage,
                                        onNavigateTo = { viewModel.navigateTo(it) },
                                        onSelectCareer = { career ->
                                            viewModel.openCareerDetail(career)
                                        },
                                        onSelectCollege = { college ->
                                            viewModel.selectCollege(college)
                                            viewModel.navigateTo(AppNavTab.COLLEGES)
                                        },
                                        onSelectCourse = { course ->
                                            viewModel.openCourseDetailsSheet(course)
                                        },
                                        onOpenAssessmentReport = {
                                            viewModel.openSavedAssessmentReport()
                                        },
                                        onTakeAssessment = {
                                            viewModel.openPsychometricTest()
                                        },
                                        onOpenAdmissionForm = { collegeName ->
                                            viewModel.openAdmissionForm(collegeName = collegeName)
                                        },
                                        onOpenAiChatbot = {
                                            viewModel.openAiChatbot(true)
                                        },
                                        onSearchColleges = { query ->
                                            viewModel.setCollegeSearchQuery(query)
                                            if (query.isNotBlank()) viewModel.navigateTo(AppNavTab.COLLEGES)
                                        }
                                    )
                                }

                                AppNavTab.CAREERS -> {
                                    CareersListScreen(
                                        careers = allCareers,
                                        currentLanguage = currentLanguage,
                                        onCareerClick = { career ->
                                            viewModel.openCareerDetail(career)
                                        },
                                        onBookCounsellingClick = { career ->
                                            viewModel.openCounsellingCheckout(targetTitle = career.title, category = "Career Counselling")
                                        }
                                    )
                                }

                                AppNavTab.CAREER_DETAIL -> {
                                    selectedCareer?.let { career ->
                                        CareerDetailScreen(
                                            career = career,
                                            currentLanguage = currentLanguage,
                                            onBackClick = { viewModel.navigateBack() },
                                            onBookCounsellingClick = {
                                                viewModel.openCounsellingCheckout(targetTitle = career.title, category = "Career Counselling")
                                            }
                                        )
                                    } ?: run {
                                        viewModel.navigateBack()
                                    }
                                }

                                AppNavTab.COLLEGES -> {
                                    CollegeExplorerScreen(
                                        colleges = filteredColleges,
                                        savedCollegeIds = savedCollegeIds,
                                        searchQuery = collegeSearchQuery,
                                        currentLanguage = currentLanguage,
                                        onSearchChange = { viewModel.setCollegeSearchQuery(it) },
                                        onToggleSave = { id -> viewModel.toggleSaveCollege(id) },
                                        onOpenAdmissionForm = { collegeName ->
                                            viewModel.openAdmissionForm(collegeName = collegeName)
                                        }
                                    )
                                }

                                AppNavTab.COURSES -> {
                                    CourseExplorerScreen(
                                        courses = filteredCourses,
                                        searchQuery = courseSearchQuery,
                                        selectedCategory = selectedCategory,
                                        currentLanguage = currentLanguage,
                                        onSearchChange = { viewModel.setCourseSearchQuery(it) },
                                        onCategoryChange = { viewModel.setCourseCategory(it) },
                                        onSelectCourse = { course ->
                                            viewModel.openCourseDetailsSheet(course)
                                        }
                                    )
                                }

                                AppNavTab.ADMISSION_FORM -> {
                                    AdmissionFormScreen(
                                        studentProfile = profile,
                                        initialCollegeName = targetCollegeForAdmission,
                                        initialCourseName = targetCourseForAdmission,
                                        submissionSuccessMessage = formSuccessMessage,
                                        onSubmitForm = { name, phone, email, qual, percentage, course, college, city, notes ->
                                            viewModel.submitAdmissionForm(name, phone, email, qual, percentage, course, college, city, notes)
                                        },
                                        onClearSuccess = { viewModel.clearFormSuccess() },
                                        onNavigateHome = { viewModel.navigateBack() },
                                        onViewDashboard = { viewModel.navigateTo(AppNavTab.DASHBOARD) }
                                    )
                                }

                                AppNavTab.DASHBOARD -> {
                                    StudentDashboardScreen(
                                        profile = profile,
                                        appliedForms = studentForms,
                                        counsellingBookings = studentBookings,
                                        savedColleges = savedColleges,
                                        assessment = studentAssessment,
                                        onSelectCollege = { college -> viewModel.selectCollege(college) },
                                        onNavigateTo = { tab -> viewModel.navigateTo(tab) },
                                        onOpenAssessmentReport = { viewModel.openSavedAssessmentReport() },
                                        onTakeAssessment = { viewModel.openPsychometricTest() },
                                        currentLanguage = currentLanguage,
                                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                                        onUpdateProfile = { name, phone, city ->
                                            viewModel.updateStudentProfile(name, phone, city)
                                        },
                                        onOpenAdmissionForm = { collegeName ->
                                            viewModel.openAdmissionForm(collegeName = collegeName)
                                        },
                                        onLogoutClick = { viewModel.logout() },
                                        onDeleteAccountClick = { viewModel.deleteAccount() },
                                        isDeletingAccount = isDeletingAccount
                                    )
                                }

                                AppNavTab.ADMIN -> {
                                    AdminPanelScreen(
                                        careers = allCareers,
                                        colleges = allCollegesAdmin,
                                        courses = allCourses,
                                        students = allStudentsAdmin,
                                        counsellingBookings = allCounsellingBookingsAdmin,
                                        admissionForms = allAdmissionFormsAdmin,
                                        onUpdateFormStatus = { formId, status -> viewModel.updateAdmissionStatus(formId, status) },
                                        onUpdateCounsellingStatus = { bookingId, status -> viewModel.updateCounsellingBookingStatus(bookingId, status) },
                                        onSaveCareer = { career -> viewModel.saveCareerAdmin(career) },
                                        onDeleteCareer = { id -> viewModel.deleteCareerAdmin(id) },
                                        onSaveCollege = { college -> viewModel.saveCollegeAdmin(college) },
                                        onDeleteCollege = { id -> viewModel.deleteCollegeAdmin(id) },
                                        onSaveCourse = { course -> viewModel.saveCourseAdmin(course) },
                                        onDeleteCourse = { id -> viewModel.deleteCourseAdmin(id) },
                                        onBroadcastNotification = { title, msg, cat -> viewModel.broadcastNotification(title, msg, cat) },
                                        onSyncCloud = { viewModel.syncCloudData() },
                                        onReseedCareers = { viewModel.reseedCareers() },
                                        onReseedCourses = { viewModel.reseedCourses() },
                                        onReseedColleges = { viewModel.reseedColleges() },
                                        isSyncing = isSyncing,
                                        onRefreshStudents = { viewModel.refreshStudentsFromFirestore() },
                                        isRefreshingStudents = isRefreshingStudents,
                                        onDiagnoseFirestore = { callback -> viewModel.diagnoseFirestore(callback) },
                                        onLogout = { viewModel.logout() }
                                    )
                                }

                                AppNavTab.NOTIFICATIONS -> {
                                    NotificationsScreen(
                                        notifications = notifications,
                                        onMarkRead = { id -> viewModel.markNotificationRead(id) },
                                        onNavigateTo = { viewModel.navigateTo(it) }
                                    )
                                }

                                AppNavTab.SETTINGS -> {
                                    SettingsScreen(
                                        profile = profile,
                                        isDarkMode = isDarkMode,
                                        assessment = studentAssessment,
                                        onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                        onOpenAssessmentReport = { viewModel.openSavedAssessmentReport() },
                                        onTakeAssessment = { viewModel.openPsychometricTest() },
                                        onSaveProfile = { name, phone, city ->
                                            viewModel.updateStudentProfile(name, phone, city)
                                        },
                                        onLogout = { viewModel.logout() },
                                        onDeleteAccount = { viewModel.deleteAccount() },
                                        isDeletingAccount = isDeletingAccount
                                    )
                                }

                                else -> {
                                    HomeScreen(
                                        studentName = profile.name,
                                        studentQualification = profile.qualification,
                                        careers = allCareers,
                                        featuredColleges = featuredColleges,
                                        allCourses = allCourses,
                                        scholarships = scholarships,
                                        assessment = studentAssessment,
                                        unreadNotificationCount = unreadCount,
                                        currentLanguage = currentLanguage,
                                        onNavigateTo = { viewModel.navigateTo(it) },
                                        onSelectCareer = { career ->
                                            viewModel.openCareerDetail(career)
                                        },
                                        onSelectCollege = { college ->
                                            viewModel.selectCollege(college)
                                            viewModel.navigateTo(AppNavTab.COLLEGES)
                                        },
                                        onSelectCourse = { course ->
                                            viewModel.openCourseDetailsSheet(course)
                                        },
                                        onOpenAssessmentReport = {
                                            viewModel.openSavedAssessmentReport()
                                        },
                                        onTakeAssessment = {
                                            viewModel.openPsychometricTest()
                                        },
                                        onOpenAdmissionForm = { collegeName ->
                                            viewModel.openAdmissionForm(collegeName = collegeName)
                                        },
                                        onOpenAiChatbot = {
                                            viewModel.openAiChatbot(true)
                                        },
                                        onSearchColleges = { query ->
                                            viewModel.setCollegeSearchQuery(query)
                                            if (query.isNotBlank()) viewModel.navigateTo(AppNavTab.COLLEGES)
                                        }
                                    )
                                }
                            }
                        }

                        // Bottom Sheets & Dialog Modals
                        selectedCourseSheet?.let { courseItem ->
                            CourseDetailSheet(
                                course = courseItem,
                                currentLanguage = currentLanguage,
                                onDismiss = { viewModel.openCourseDetailsSheet(null) },
                                onBookCounsellingSwiped = { selected ->
                                    viewModel.openCourseDetailsSheet(null)
                                    viewModel.openCounsellingCheckout(course = selected, targetTitle = selected.name, category = "Course Counselling")
                                }
                            )
                        }

                        if (isCounsellingCheckoutOpen) {
                            CounsellingCheckoutSheet(
                                studentProfile = profile,
                                targetCourse = counsellingCheckoutCourse,
                                targetTitle = counsellingCheckoutTargetTitle,
                                bookingCategory = counsellingCheckoutCategory,
                                hasPreviousBookings = studentBookings.isNotEmpty(),
                                currentLanguage = currentLanguage,
                                onDismiss = { viewModel.closeCounsellingCheckout() },
                                onConfirmBooking = { date, timeSlot, courseName, couponCode, feeAmount, paymentMethod, counsellorName ->
                                    viewModel.submitCounsellingBooking(
                                        date = date,
                                        timeSlot = timeSlot,
                                        targetCourseName = courseName,
                                        couponCode = couponCode,
                                        feeAmount = feeAmount,
                                        paymentStatus = paymentMethod,
                                        counsellorName = counsellorName,
                                        bookingType = counsellingCheckoutCategory
                                    )
                                }
                            )
                        }

                        if (showAiChatbot) {
                            AiChatbotSheet(
                                studentName = profile.name,
                                studentStream = profile.qualification,
                                initialPrompt = chatInitialPrompt,
                                isInitialKannada = currentLanguage == "kn",
                                onDismiss = { viewModel.openAiChatbot(false) },
                                onNavigateToCourses = {
                                    viewModel.openAiChatbot(false)
                                    viewModel.navigateTo(AppNavTab.COURSES)
                                }
                            )
                        }

                        // Psychometric Career Assessment Modals
                        if (showPostLoginAssessmentPrompt) {
                            PostLoginAssessmentPromptDialog(
                                studentName = profile.name,
                                currentLanguage = currentLanguage,
                                onStartTest = { viewModel.openPsychometricTest() },
                                onDismiss = { viewModel.dismissPostLoginPrompt() }
                            )
                        }

                        if (showPsychometricTestDialog) {
                            PsychometricTestDialog(
                                currentLanguage = currentLanguage,
                                onDismiss = { viewModel.dismissPsychometricTest() },
                                onComplete = { answers ->
                                    viewModel.submitPsychometricAnswers(answers)
                                }
                            )
                        }

                        if (showPsychometricResultSheet && activeAssessmentResult != null) {
                            CareerProfileAssessmentSheet(
                                result = activeAssessmentResult!!,
                                studentName = profile.name,
                                currentLanguage = currentLanguage,
                                onDismiss = { viewModel.dismissAssessmentResult() },
                                onRetakeTest = {
                                    viewModel.dismissAssessmentResult()
                                    viewModel.openPsychometricTest()
                                },
                                onBookGuidance = {
                                    viewModel.dismissAssessmentResult()
                                    viewModel.openCounsellingCheckout(
                                        targetTitle = "Psychometric ${activeAssessmentResult!!.archetype}",
                                        category = "Career Counselling"
                                    )
                                },
                                onNavigateToCourses = {
                                    viewModel.dismissAssessmentResult()
                                    viewModel.navigateTo(AppNavTab.COURSES)
                                },
                                onNavigateToCareers = {
                                    viewModel.dismissAssessmentResult()
                                    viewModel.navigateTo(AppNavTab.CAREERS)
                                },
                                onChatWithMoji = { prompt ->
                                    viewModel.openAiChatbotWithPrompt(prompt)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
