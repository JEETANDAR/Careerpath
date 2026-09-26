package com.aistudio.carrerpath.counseling.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "careers")
data class CareerEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    val title: String, // e.g., "Doctor"
    val titleKn: String = "", // e.g., "ವೈದ್ಯರು"
    val image: String,
    val tagline: String,
    val taglineKn: String = "",
    val description: String,
    val descriptionKn: String = "",
    val overview: String,
    val overviewKn: String = "",
    val whyChoose: String = "",
    val whyChooseKn: String = "",
    val benefits: String = "",
    val benefitsKn: String = "",
    val eligibility: String = "",
    val eligibilityKn: String = "",
    val subjects: String = "",
    val subjectsKn: String = "",
    val exams: String = "",
    val examsKn: String = "",
    val pathway: String = "",
    val pathwayKn: String = "",
    val skills: String = "",
    val skillsKn: String = "",
    val jobRoles: String = "",
    val jobRolesKn: String = "",
    val opportunities: String = "",
    val opportunitiesKn: String = "",
    val topColleges: String = "",
    val topCollegesKn: String = "",
    val topRecruiters: String = "",
    val topRecruitersKn: String = "",
    val salaryAverage: String = "₹ 8-15 LPA",
    val salaryHighest: String = "₹ 35+ LPA",
    val feesAverage: String = "₹ 2-5 Lakhs/year",
    val duration: String = "4-5 Years",
    val scholarships: String = "",
    val scholarshipsKn: String = "",
    val futureScope: String = "",
    val futureScopeKn: String = "",
    val certifications: String = "",
    val certificationsKn: String = "",
    val faqs: String = "",
    val faqsKn: String = "",
    val category: String = "General",
    val pathwayDetailed: String = "",
    val pathwayDetailedKn: String = ""
)

@Entity(tableName = "colleges")
data class CollegeEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    val name: String,
    val nameKn: String = "",
    val location: String,
    val locationKn: String = "",
    val state: String,
    val city: String,
    val coursesOffered: String,
    val coursesOfferedKn: String = "",
    val minFeesLakhs: Double,
    val maxFeesLakhs: Double,
    val hostelAvailable: Boolean,
    val hostelFeesPerYear: Int,
    val recognition: String,
    val recognitionKn: String = "",
    val ranking: Int,
    val eligibility: String,
    val eligibilityKn: String = "",
    val admissionStatus: String,
    val imageUrl: String,
    val heroBannerUrl: String,
    val overview: String,
    val overviewKn: String = "",
    val averagePackageLakhs: Double,
    val highestPackageLakhs: Double,
    val websiteUrl: String = "https://careerpath.example.com",
    val isFeatured: Boolean = false
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    val code: String,
    val name: String,
    val nameKn: String = "",
    val category: String,
    val durationYears: String,
    val eligibility: String,
    val eligibilityKn: String = "",
    val avgFeesLakhs: Double,
    val avgPackageLakhs: Double,
    val careerScope: String,
    val careerScopeKn: String = "",
    val description: String,
    val descriptionKn: String = "",
    val topCollegesCount: Int = 15,
    val imageUrl: String = "",
    val tagline: String = "",
    val taglineKn: String = "",
    val overview: String = "",
    val overviewKn: String = "",
    val whyChoose: String = "",
    val whyChooseKn: String = "",
    val benefits: String = "",
    val benefitsKn: String = "",
    val subjects: String = "",
    val subjectsKn: String = "",
    val exams: String = "",
    val examsKn: String = "",
    val pathway: String = "",
    val pathwayKn: String = "",
    val skills: String = "",
    val skillsKn: String = "",
    val jobRoles: String = "",
    val jobRolesKn: String = "",
    val opportunities: String = "",
    val opportunitiesKn: String = "",
    val topColleges: String = "",
    val topCollegesKn: String = "",
    val topRecruiters: String = "",
    val topRecruitersKn: String = "",
    val salaryAverage: String = "",
    val salaryHighest: String = "",
    val feesAverage: String = "",
    val scholarships: String = "",
    val scholarshipsKn: String = "",
    val futureScope: String = "",
    val futureScopeKn: String = "",
    val certifications: String = "",
    val certificationsKn: String = "",
    val faqs: String = "",
    val faqsKn: String = "",
    val detailedPathway: String = "",
    val detailedPathwayKn: String = "",
    val officialExamLinks: String = "",
    val cutoffGuidance: String = ""
)

@Entity(tableName = "admission_forms")
data class AdmissionFormEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String = "",
    val phone: String = "",
    val email: String = "",
    val qualification: String = "",
    val percentageMark: Double = 85.0,
    val preferredCourse: String = "",
    val preferredCollege: String = "",
    val city: String = "",
    val notes: String = "",
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val category: String,
    val userPhone: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionUrl: String = ""
)

@Entity(tableName = "scholarships")
data class ScholarshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val provider: String,
    val amountText: String,
    val eligibility: String,
    val deadline: String,
    val category: String
)

@Entity(tableName = "saved_colleges", primaryKeys = ["userId", "collegeId"])
data class SavedCollegeEntity(
    val userId: String,
    val collegeId: Int,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class StudentProfileEntity(
    @PrimaryKey val phone: String = "", // Phone number as primary identifier
    val name: String = "",
    val email: String = "",
    val institution: String = "",
    val gender: String = "Male",
    val city: String = "",
    val state: String = "",
    val country: String = "India",
    val studentClass: String = "High School",
    val percentageMark: Double = 85.0,
    val isAdmin: Boolean = false,
    val uid: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "counselling_bookings")
data class CounsellingBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String = "",
    val studentPhone: String = "",
    val studentEmail: String = "",
    val studentClass: String = "",
    val targetCourse: String = "General Career Counselling",
    val counsellorName: String = "Nimi",
    val bookingDate: String = "",
    val timeSlot: String = "",
    val couponCode: String = "",
    val feeAmount: Double = 0.0,
    val paymentStatus: String = "Confirmed",
    val razorpayPaymentId: String = "free_booking",
    val status: String = "Confirmed",
    val bookingType: String = "Course Counselling",
    val timestamp: Long = System.currentTimeMillis()
)

data class StudentProfile(
    val phone: String = "+91 98765 43210",
    val name: String = "Student User",
    val email: String = "student@careerpath.org",
    val institution: String = "National Public School",
    val gender: String = "Male",
    val city: String = "Bangalore",
    val state: String = "Karnataka",
    val country: String = "India",
    val qualification: String = "12th Standard",
    val percentage: Double = 88.5,
    val preferredCourse: String = "General",
    val preferredCity: String = "Bangalore",
    val budgetLakhs: Double = 15.0,
    val isAdmin: Boolean = false
)

@Entity(tableName = "psychometric_assessments")
data class PsychometricAssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userPhone: String,
    val archetype: String,
    val archetypeDescription: String,
    val topStrengths: String,
    val recommendedCareers: String,
    val recommendedCourses: String,
    val realisticScore: Int = 0,
    val investigativeScore: Int = 0,
    val artisticScore: Int = 0,
    val socialScore: Int = 0,
    val enterprisingScore: Int = 0,
    val conventionalScore: Int = 0,
    val analyticalScore: Int = 0,
    val creativeScore: Int = 0,
    val healthcareScore: Int = 0,
    val businessScore: Int = 0,
    val leadershipScore: Int = 0,
    val technicalScore: Int = 0,
    val totalScore: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class PsychometricOption(
    val text: String,
    val score: Int // 0 (Dislike), 1 (Slightly Dislike), 2 (Neither), 3 (Slightly Enjoy), 4 (Enjoy)
)

data class PsychometricQuestion(
    val id: Int,
    val code: String, // "R1", "I1", "A1", "S1", "E1", "C1", etc.
    val category: String, // "Realistic", "Investigative", "Artistic", "Social", "Enterprising", "Conventional"
    val activity: String,
    val activityKn: String = ""
)

data class PsychometricAssessmentResult(
    val archetype: String,
    val tag: String,
    val riasecCode: String, // e.g. "I", "R", "A", "S", "E", "C" or "IR"
    val archetypeDescription: String,
    val topStrengths: List<String>,
    val recommendedCareers: List<String>,
    val recommendedCourses: List<String>,
    val dimensionScores: Map<String, Int>, // Dimension Name -> Percentage (0-100)
    val rawScores: Map<String, Int> = emptyMap(), // Dimension Name -> Raw Score (0-20)
    val totalRiasecScore: Int = 0 // Total score out of 120
)

data class AICounselingQuery(
    val qualification: String = "12th Science (PCM)",
    val percentage: Double = 85.0,
    val state: String = "Karnataka",
    val budgetLakhs: Double = 10.0,
    val preferredCourse: String = "Engineering",
    val preferredCity: String = "Bangalore",
    val additionalGoals: String = "Interested in AI and robotics"
)

data class AICounselingResult(
    val matchScore: Int,
    val recommendedCollegeName: String,
    val recommendedCourse: String,
    val reasoning: String,
    val eligibilityCheck: String,
    val estimatedFeeRange: String,
    val careerOpportunities: String,
    val alternateColleges: List<String>
)

data class SourceLink(
    val title: String,
    val url: String
)

data class ChatResponse(
    val replyText: String,
    val sources: List<SourceLink> = emptyList()
)

/**
 * Utility extension to automatically capitalize the first letter of each word in a string
 * while preserving spacing.
 */
fun String.capitalizeWords(): String {
    if (this.isEmpty()) return this
    val sb = StringBuilder()
    var capitalizeNext = true
    for (ch in this) {
        if (ch.isWhitespace()) {
            capitalizeNext = true
            sb.append(ch)
        } else if (capitalizeNext) {
            sb.append(ch.uppercaseChar())
            capitalizeNext = false
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}
