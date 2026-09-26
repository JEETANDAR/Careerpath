package com.aistudio.carrerpath.counseling.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ")
}

object AppStrings {

    // Bottom Navigation
    val navHome = mapOf("en" to "Home", "kn" to "ಮುಖಪುಟ")
    val navCareers = mapOf("en" to "Careers", "kn" to "ವೃತ್ತಿಪಥಗಳು")
    val navColleges = mapOf("en" to "Colleges", "kn" to "ಕಾಲೇಜುಗಳು")
    val navCourses = mapOf("en" to "Courses", "kn" to "ಕೋರ್ಸ್‌ಗಳು")
    val navDashboard = mapOf("en" to "Dashboard", "kn" to "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್")

    // Authentication / OTP
    val authTitle = mapOf("en" to "EduVerse Career Path", "kn" to "ಎಡು ವರ್ಸ್ ವೃತ್ತಿ ಮಾರ್ಗ")
    val authSubtitle = mapOf("en" to "Enter your mobile phone number to receive a 6-digit OTP", "kn" to "6-ಅಂಕಿಯ OTP ಸ್ವೀಕರಿಸಲು ನಿಮ್ಮ ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯನ್ನು ನಮೂದಿಸಿ")
    val enterPhone = mapOf("en" to "Mobile Phone Number (+91)", "kn" to "ಮೊಬೈಲ್ ಫೋನ್ ಸಂಖ್ಯೆ (+91)")
    val sendOtp = mapOf("en" to "Send Real SMS OTP", "kn" to "SMS OTP ಕಳುಹಿಸಿ")
    val sendingOtp = mapOf("en" to "Sending OTP...", "kn" to "OTP ಕಳುಹಿಸಲಾಗುತ್ತಿದೆ...")
    val enterOtp = mapOf("en" to "Enter 6-Digit OTP Code", "kn" to "6-ಅಂಕಿಯ OTP ಕೋಡ್ ನಮೂದಿಸಿ")
    val verifyOtp = mapOf("en" to "Verify OTP & Continue", "kn" to "OTP ಪರಿಶೀಲಿಸಿ ಮತ್ತು ಮುಂದುವರಿಯಿರಿ")
    val verifying = mapOf("en" to "Verifying OTP...", "kn" to "OTP ಪರಿಶೀಲಿಸಲಾಗುತ್ತಿದೆ...")
    val resendOtp = mapOf("en" to "Resend SMS OTP", "kn" to "ಮತ್ತೊಮ್ಮೆ OTP ಕಳುಹಿಸಿ")
    val otpSentSuccess = mapOf("en" to "OTP code sent to your mobile phone!", "kn" to "ನಿಮ್ಮ ಮೊಬೈಲ್‌ಗೆ OTP ಕೋಡ್ ಕಳುಹಿಸಲಾಗಿದೆ!")
    val invalidPhone = mapOf("en" to "Please enter a valid 10-digit mobile phone number.", "kn" to "ದಯವಿಟ್ಟು ಮಾನ್ಯವಾದ 10-ಅಂಕಿಯ ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯನ್ನು ನಮೂದಿಸಿ.")

    // Signup Page
    val signupTitle = mapOf("en" to "Complete Student Profile", "kn" to "ವಿದ್ಯಾರ್ಥಿ ವಿವರಗಳನ್ನು ಪೂರ್ಣಗೊಳಿಸಿ")
    val signupSubtitle = mapOf("en" to "Please fill in your profile information to personalize guidance", "kn" to "ವೈಯಕ್ತಿಕ ಮಾರ್ಗದರ್ಶನಕ್ಕಾಗಿ ನಿಮ್ಮ ವಿವರಗಳನ್ನು ಭರ್ತಿ ಮಾಡಿ")
    val fullName = mapOf("en" to "Full Name", "kn" to "ಪೂರ್ಣ ಹೆಸರು")
    val phoneNumber = mapOf("en" to "Phone Number", "kn" to "ಫೋನ್ ಸಂಖ್ಯೆ")
    val institutionName = mapOf("en" to "Education Institution Name", "kn" to "ಶಿಕ್ಷಣ ಸಂಸ್ಥೆಯ ಹೆಸರು")
    val gender = mapOf("en" to "Gender", "kn" to "ಲಿಂಗ")
    val city = mapOf("en" to "City", "kn" to "ನಗರ")
    val state = mapOf("en" to "State", "kn" to "ರಾಜ್ಯ")
    val country = mapOf("en" to "Country", "kn" to "ದೇಶ")
    val completeSignup = mapOf("en" to "Save & Continue to Home", "kn" to "ಉಳಿಸಿ ಮತ್ತು ಮುಖಪುಟಕ್ಕೆ ಹೋಗಿ")

    // Home Section Titles
    val topCareersHeader = mapOf("en" to "TOP CAREERS", "kn" to "ಉನ್ನತ ವೃತ್ತಿಪಥಗಳು")
    val topCareersSubtitle = mapOf("en" to "Explore top-rated professional career options and pathways", "kn" to "ಉನ್ನತ ಶ್ರೇಣಿಯ ವೃತ್ತಿ ಅವಕಾಶಗಳು ಮತ್ತು ಮಾರ್ಗಗಳನ್ನು ಅನ್ವೇಷಿಸಿ")
    val viewAllCareers = mapOf("en" to "View All Careers", "kn" to "ಎಲ್ಲಾ ವೃತ್ತಿಗಳನ್ನು ವೀಕ್ಷಿಸಿ")
    val topCollegesHeader = mapOf("en" to "FEATURED COLLEGES", "kn" to "ಪ್ರಮುಖ ಕಾಲೇಜುಗಳು")
    val popularCoursesHeader = mapOf("en" to "POPULAR COURSES", "kn" to "ಜನಪ್ರಿಯ ಕೋರ್ಸ್‌ಗಳು")
    val bookCounsellingBanner = mapOf("en" to "Book Free 1-on-1 Expert Counselling", "kn" to "ಉಚಿತ 1-ಆನ್-1 ವೃತ್ತಿ ಮಾರ್ಗದರ್ಶನ ಕಾಯ್ದಿರಿಸಿ")

    // Career Detail Screen Labels
    val careerOverview = mapOf("en" to "Career Overview", "kn" to "ವೃತ್ತಿ ವಿವರಣೆ")
    val whyChoose = mapOf("en" to "Why Choose This Career", "kn" to "ಈ ವೃತ್ತಿಯನ್ನು ಏಕೆ ಆಯ್ಕೆ ಮಾಡಬೇಕು")
    val benefits = mapOf("en" to "Key Benefits & Advantages", "kn" to "ಮುಖ್ಯ ಪ್ರಯೋಜನಗಳು")
    val eligibility = mapOf("en" to "Eligibility Criteria", "kn" to "ಅರ್ಹತೆಯ ಮಾನದಂಡಗಳು")
    val requiredSubjects = mapOf("en" to "Required Subjects", "kn" to "ಅಗತ್ಯವಿರುವ ವಿಷಯಗಳು")
    val entranceExams = mapOf("en" to "Entrance Exams", "kn" to "ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳು")
    val educationalPathway = mapOf("en" to "Educational Pathway", "kn" to "ಶೈಕ್ಷಣಿಕ ಹಂತಗಳು")
    val skillsRequired = mapOf("en" to "Key Skills Required", "kn" to "ಅಗತ್ಯವಿರುವ ಕೌಶಲ್ಯಗಳು")
    val jobRoles = mapOf("en" to "Job Roles & Designations", "kn" to "ಉದ್ಯೋಗ ಪಾತ್ರಗಳು")
    val careerOpportunities = mapOf("en" to "Career Opportunities & Scope", "kn" to "ಉದ್ಯೋಗಾವಕಾಶಗಳು")
    val topColleges = mapOf("en" to "Top Colleges & Universities", "kn" to "ಉನ್ನತ ಕಾಲೇಜುಗಳು")
    val topRecruiters = mapOf("en" to "Top Hiring Recruiters", "kn" to "ಉನ್ನತ ನೇಮಕಾತಿದಾರರು")
    val avgSalary = mapOf("en" to "Average Starting Salary", "kn" to "ಸರಾಸರಿ ಆರಂಭಿಕ ವೇತನ")
    val highestSalary = mapOf("en" to "Highest Package Potential", "kn" to "ಗರಿಷ್ಠ ವೇತನ ಸಾಮರ್ಥ್ಯ")
    val avgFees = mapOf("en" to "Average Course Fees", "kn" to "ಸರಾಸರಿ ಕೋರ್ಸ್ ಶುಲ್ಕ")
    val courseDuration = mapOf("en" to "Duration", "kn" to "ಅವಧಿ")
    val scholarships = mapOf("en" to "Scholarships Available", "kn" to "ಲಭ್ಯವಿರುವ ವಿದ್ಯಾರ್ಥಿವೇತನಗಳು")
    val futureScope = mapOf("en" to "Future Scope & Growth", "kn" to "ಭವಿಷ್ಯದ ವ್ಯಾಪ್ತಿ ಮತ್ತು ಬೆಳವಣಿಗೆ")
    val certifications = mapOf("en" to "Recommended Certifications", "kn" to "ಶಿಫಾರಸು ಮಾಡಲಾದ ಪ್ರಮಾಣಪತ್ರಗಳು")
    val faqs = mapOf("en" to "Frequently Asked Questions", "kn" to "ಪದೇ ಪದೇ ಕೇಳಲಾಗುವ ಪ್ರಶ್ನೆಗಳು")
    val bookFreeCounselling = mapOf("en" to "BOOK FREE COUNSELLING", "kn" to "ಉಚಿತ ಮಾರ್ಗದರ್ಶನ ಕಾಯ್ದಿರಿಸಿ")

    // Dashboard
    val dashboardTitle = mapOf("en" to "Student Profile & Dashboard", "kn" to "ವಿದ್ಯಾರ್ಥಿ ಪ್ರೊಫೈಲ್ ಮತ್ತು ಡ್ಯಾಶ್‌ಬೋರ್ಡ್")
    val editProfile = mapOf("en" to "Edit Profile Information", "kn" to "ಪ್ರೊಫೈಲ್ ಸಂಪಾದಿಸಿ")
    val selectLanguage = mapOf("en" to "Select Language", "kn" to "ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ")
    val aboutApp = mapOf("en" to "About EduVerse", "kn" to "ಎಡು ವರ್ಸ್ ಕುರಿತು")
    val privacyPolicy = mapOf("en" to "Privacy Policy", "kn" to "ಗೌಪ್ಯತಾ ನೀತಿ")
    val termsConditions = mapOf("en" to "Terms & Conditions", "kn" to "ನಿಯಮಗಳು ಮತ್ತು ಷರತ್ತುಗಳು")
    val contactUs = mapOf("en" to "Contact Support", "kn" to "ಸಂಪರ್ಕಿಸಿ")
    val logout = mapOf("en" to "Log Out", "kn" to "ನಿರ್ಗಮಿಸಿ (Logout)")

    // Admin Panel
    val adminTitle = mapOf("en" to "Admin Management Console", "kn" to "ಅಡ್ಮಿನ್ ನಿರ್ವಹಣಾ ಕನ್ಸೋಲ್")
    val addCareer = mapOf("en" to "Add New Career", "kn" to "ಹೊಸ ವೃತ್ತಿಯನ್ನು ಸೇರಿಸಿ")
    val editCareer = mapOf("en" to "Edit Career", "kn" to "ವೃತ್ತಿಯನ್ನು ಸಂಪಾದಿಸಿ")
    val deleteCareer = mapOf("en" to "Delete Career", "kn" to "ವೃತ್ತಿಯನ್ನು ಅಳಿಸಿ")

    fun get(map: Map<String, String>, lang: AppLanguage): String {
        return map[lang.code] ?: map["en"] ?: ""
    }
}
