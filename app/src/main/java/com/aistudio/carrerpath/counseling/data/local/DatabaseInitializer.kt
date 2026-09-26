package com.aistudio.carrerpath.counseling.data.local

import android.content.Context
import com.aistudio.carrerpath.counseling.data.model.AdmissionFormEntity
import com.aistudio.carrerpath.counseling.data.model.CareerEntity
import com.aistudio.carrerpath.counseling.data.model.CollegeEntity
import com.aistudio.carrerpath.counseling.data.model.CounsellingBookingEntity
import com.aistudio.carrerpath.counseling.data.model.CourseEntity
import com.aistudio.carrerpath.counseling.data.model.NotificationEntity
import com.aistudio.carrerpath.counseling.data.model.ScholarshipEntity
import com.aistudio.carrerpath.counseling.data.model.StudentProfileEntity
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

object DatabaseInitializer {

    @Volatile private var cachedColleges: List<CollegeEntity>? = null
    @Volatile private var cachedRawColleges: List<Map<String, Any>>? = null
    @Volatile private var cachedCareers: List<CareerEntity>? = null
    @Volatile private var cachedRawCareers: List<Map<String, Any>>? = null
    @Volatile private var cachedCourses: List<CourseEntity>? = null
    @Volatile private var cachedRawCourses: List<Map<String, Any>>? = null

    fun loadCollegesFromAssets(context: Context): List<CollegeEntity> {
        cachedColleges?.let { return it }
        return synchronized(this) {
            cachedColleges?.let { return it }
            try {
                val jsonStr = context.assets.open("colleges.json").bufferedReader().use { it.readText() }
                val array = JSONArray(jsonStr)
                val list = mutableListOf<CollegeEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val rawId = obj.optInt("id", -1)
                    val id = if (rawId >= 0) rawId else i
                    val img = obj.optString("imageUrl", obj.optString("image", obj.optString("logoImageUrl", ""))).ifBlank { "https://img.logo.dev/iitb.ac.in?token=pk_Og3jJE1jTrO0d9980fA5_Q&size=256&format=png" }
                    val hero = obj.optString("heroBannerUrl", img).ifBlank { img }
                    val rawRank = obj.optInt("ranking", -1)
                    val rank = if (rawRank > 0) rawRank else (id + 1)
                    list.add(
                        CollegeEntity(
                            id = id,
                            name = obj.optString("name", ""),
                            nameKn = obj.optString("nameKn", ""),
                            location = obj.optString("location", ""),
                            locationKn = obj.optString("locationKn", ""),
                            state = obj.optString("state", ""),
                            city = obj.optString("city", ""),
                            coursesOffered = obj.optString("coursesOffered", ""),
                            coursesOfferedKn = obj.optString("coursesOfferedKn", ""),
                            minFeesLakhs = obj.optDouble("minFeesLakhs", 1.0),
                            maxFeesLakhs = obj.optDouble("maxFeesLakhs", 5.0),
                            hostelAvailable = obj.optBoolean("hostelAvailable", true),
                            hostelFeesPerYear = obj.optInt("hostelFeesPerYear", 50000),
                            recognition = obj.optString("recognition", ""),
                            recognitionKn = obj.optString("recognitionKn", ""),
                            ranking = rank,
                            eligibility = obj.optString("eligibility", ""),
                            eligibilityKn = obj.optString("eligibilityKn", ""),
                            admissionStatus = obj.optString("admissionStatus", "Open"),
                            imageUrl = img,
                            heroBannerUrl = hero,
                            overview = obj.optString("overview", ""),
                            overviewKn = obj.optString("overviewKn", ""),
                            averagePackageLakhs = obj.optDouble("averagePackageLakhs", 8.0),
                            highestPackageLakhs = obj.optDouble("highestPackageLakhs", 25.0),
                            websiteUrl = obj.optString("websiteUrl", "https://careerpath.example.com"),
                            isFeatured = obj.optBoolean("isFeatured", obj.optBoolean("featured", false))
                        )
                    )
                }
                val result = if (list.isNotEmpty()) list else initialColleges
                cachedColleges = result
                result
            } catch (e: Exception) {
                initialColleges
            }
        }
    }

    fun loadRawCollegesJson(context: Context): List<Map<String, Any>> {
        cachedRawColleges?.let { return it }
        return synchronized(this) {
            cachedRawColleges?.let { return it }
            try {
                val jsonStr = context.assets.open("colleges.json").bufferedReader().use { it.readText() }
                val array = JSONArray(jsonStr)
                val list = mutableListOf<Map<String, Any>>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val map = mutableMapOf<String, Any>()
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = obj.get(key)
                        if (value != JSONObject.NULL) {
                            map[key] = value
                        }
                    }
                    val rawId = obj.optInt("id", -1)
                    val id = if (rawId >= 0) rawId else i
                    val img = obj.optString("imageUrl", obj.optString("image", obj.optString("logoImageUrl", ""))).ifBlank { "https://img.logo.dev/iitb.ac.in?token=pk_Og3jJE1jTrO0d9980fA5_Q&size=256&format=png" }
                    val hero = obj.optString("heroBannerUrl", img).ifBlank { img }
                    val rawRank = obj.optInt("ranking", -1)
                    val rank = if (rawRank > 0) rawRank else (id + 1)

                    map["id"] = id
                    map["imageUrl"] = img
                    map["image"] = img
                    map["heroBannerUrl"] = hero
                    map["ranking"] = rank
                    map["rankingNIRF"] = rank
                    list.add(map)
                }
                cachedRawColleges = list
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun toFirestoreValue(value: Any?): Any? {
        return when (value) {
            null, JSONObject.NULL -> null
            is JSONObject -> {
                val map = mutableMapOf<String, Any>()
                val keys = value.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val v = value.opt(k)
                    val converted = toFirestoreValue(v)
                    if (converted != null) {
                        map[k] = converted
                    }
                }
                map
            }
            is JSONArray -> {
                val list = mutableListOf<Any>()
                for (i in 0 until value.length()) {
                    val v = value.opt(i)
                    val converted = toFirestoreValue(v)
                    if (converted != null) {
                        list.add(converted)
                    }
                }
                list
            }
            else -> value
        }
    }

    fun loadCareersFromAssets(context: Context): List<CareerEntity> {
        cachedCareers?.let { return it }
        return synchronized(this) {
            cachedCareers?.let { return it }
            try {
                val jsonStr = try {
                    context.assets.open("careers.json").bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    context.assets.open("courses.json").bufferedReader().use { it.readText() }
                }
                val array = JSONArray(jsonStr)
                val list = mutableListOf<CareerEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val rawId = obj.optInt("id", -1)
                    val id = if (rawId >= 0) rawId else (i + 1)
                    val img = obj.optString("image", obj.optString("imageUrl", "")).ifBlank { "https://images.pexels.com/photos/5452292/pexels-photo-5452292.jpeg?auto=compress&cs=tinysrgb&w=1200" }
                    list.add(
                        CareerEntity(
                            id = id,
                            title = obj.optString("title", ""),
                            titleKn = obj.optString("titleKn", ""),
                            image = img,
                            tagline = obj.optString("tagline", ""),
                            taglineKn = obj.optString("taglineKn", ""),
                            description = obj.optString("description", ""),
                            descriptionKn = obj.optString("descriptionKn", ""),
                            overview = obj.optString("overview", ""),
                            overviewKn = obj.optString("overviewKn", ""),
                            whyChoose = obj.optString("whyChoose", ""),
                            whyChooseKn = obj.optString("whyChooseKn", ""),
                            benefits = obj.optString("benefits", ""),
                            benefitsKn = obj.optString("benefitsKn", ""),
                            eligibility = obj.optString("eligibility", ""),
                            eligibilityKn = obj.optString("eligibilityKn", ""),
                            subjects = obj.optString("subjects", ""),
                            subjectsKn = obj.optString("subjectsKn", ""),
                            exams = obj.optString("exams", ""),
                            examsKn = obj.optString("examsKn", ""),
                            pathway = obj.optString("pathway", ""),
                            pathwayKn = obj.optString("pathwayKn", ""),
                            skills = obj.optString("skills", ""),
                            skillsKn = obj.optString("skillsKn", ""),
                            jobRoles = obj.optString("jobRoles", ""),
                            jobRolesKn = obj.optString("jobRolesKn", ""),
                            opportunities = obj.optString("opportunities", ""),
                            opportunitiesKn = obj.optString("opportunitiesKn", ""),
                            topColleges = obj.optString("topColleges", ""),
                            topCollegesKn = obj.optString("topCollegesKn", ""),
                            topRecruiters = obj.optString("topRecruiters", ""),
                            topRecruitersKn = obj.optString("topRecruitersKn", ""),
                            salaryAverage = obj.optString("salaryAverage", "₹ 8 - 18 Lakhs / Year"),
                            salaryHighest = obj.optString("salaryHighest", "₹ 30+ Lakhs / Year"),
                            feesAverage = obj.optString("feesAverage", "₹ 2 - 8 Lakhs"),
                            duration = obj.optString("duration", "3-4 Years"),
                            scholarships = obj.optString("scholarships", ""),
                            scholarshipsKn = obj.optString("scholarshipsKn", ""),
                            futureScope = obj.optString("futureScope", ""),
                            futureScopeKn = obj.optString("futureScopeKn", ""),
                            certifications = obj.optString("certifications", ""),
                            certificationsKn = obj.optString("certificationsKn", ""),
                            faqs = obj.optString("faqs", ""),
                            faqsKn = obj.optString("faqsKn", ""),
                            category = obj.optString("category", "General"),
                            pathwayDetailed = obj.opt("pathwayDetailed")?.toString() ?: "",
                            pathwayDetailedKn = obj.opt("pathwayDetailedKn")?.toString() ?: ""
                        )
                    )
                }
                val result = if (list.isNotEmpty()) list else initialCareers
                cachedCareers = result
                result
            } catch (e: Exception) {
                initialCareers
            }
        }
    }

    fun loadRawCareersJson(context: Context): List<Map<String, Any>> {
        cachedRawCareers?.let { return it }
        return synchronized(this) {
            cachedRawCareers?.let { return it }
            try {
                val jsonStr = context.assets.open("careers.json").bufferedReader().use { it.readText() }
                val array = JSONArray(jsonStr)
                val list = mutableListOf<Map<String, Any>>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val map = mutableMapOf<String, Any>()
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = obj.opt(key)
                        val converted = toFirestoreValue(value)
                        if (converted != null) {
                            map[key] = converted
                        }
                    }
                    val rawId = obj.optInt("id", -1)
                    val id = if (rawId >= 0) rawId else (i + 1)
                    val img = obj.optString("image", obj.optString("imageUrl", "")).ifBlank { "https://images.pexels.com/photos/5452292/pexels-photo-5452292.jpeg?auto=compress&cs=tinysrgb&w=1200" }

                    map["id"] = id
                    map["image"] = img
                    map["imageUrl"] = img
                    list.add(map)
                }
                cachedRawCareers = list
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun extractFirstNumber(text: String, fallback: Double): Double {
        return try {
            val matcher = Pattern.compile("(\\d+(\\.\\d+)?)").matcher(text)
            if (matcher.find()) {
                matcher.group(1)?.toDoubleOrNull() ?: fallback
            } else {
                fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }

    fun normalizeImageUrl(rawUrl: String): String {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) return ""
        if (trimmed.contains("commons.wikimedia.org/wiki/Special:Redirect/file/")) {
            val parts = trimmed.split("?", limit = 2)
            val base = parts[0]
            val query = if (parts.size > 1) parts[1] else ""
            val prefix = "https://commons.wikimedia.org/wiki/Special:Redirect/file/"
            val normalizedBase = if (base.startsWith(prefix)) {
                prefix + base.substring(prefix.length).replace("%20", "_").replace(" ", "_")
            } else {
                base.replace("%20", "_").replace(" ", "_")
            }
            return when {
                query.isBlank() -> "$normalizedBase?width=800"
                !query.contains("width=") -> "$normalizedBase?$query&width=800"
                else -> "$normalizedBase?$query"
            }
        }
        return trimmed
    }

    fun loadCoursesFromAssets(context: Context): List<CourseEntity> {
        cachedCourses?.let { return it }
        return synchronized(this) {
            cachedCourses?.let { return it }
            try {
                val jsonStr = try {
                    context.assets.open("courses.json").bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    context.assets.open("courses_200_real_relevant_images.json").bufferedReader().use { it.readText() }
                }
                val array = JSONArray(jsonStr)
                val list = mutableListOf<CourseEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.optInt("id", i + 1)
                    val title = obj.optString("name", obj.optString("title", ""))
                    val titleKn = obj.optString("nameKn", obj.optString("titleKn", ""))
                    val category = obj.optString("category", "General")
                    val duration = obj.optString("durationYears", obj.optString("duration", "3-4 Years"))
                    val eligibility = obj.optString("eligibility", "")
                    val eligibilityKn = obj.optString("eligibilityKn", "")
                    val feesAvg = obj.optString("feesAverage", "3.5 Lakhs")
                    val salAvg = obj.optString("salaryAverage", "8.0 Lakhs")
                    val futureScope = obj.optString("careerScope", obj.optString("futureScope", ""))
                    val futureScopeKn = obj.optString("careerScopeKn", obj.optString("futureScopeKn", ""))
                    val desc = obj.optString("description", "")
                    val descKn = obj.optString("descriptionKn", "")
                    val rawImageUrl = obj.optString("imageUrl", obj.optString("image", "")).ifBlank {
                        obj.optString("image", "")
                    }
                    val imageUrl = normalizeImageUrl(rawImageUrl)

                    val feeVal = extractFirstNumber(feesAvg, 3.5)
                    val pkgVal = extractFirstNumber(salAvg, 8.0)

                    list.add(
                        CourseEntity(
                            id = id,
                            code = "CRS-%03d".format(id),
                            name = title,
                            nameKn = titleKn,
                            category = category,
                            durationYears = duration,
                            eligibility = eligibility,
                            eligibilityKn = eligibilityKn,
                            avgFeesLakhs = feeVal,
                            avgPackageLakhs = pkgVal,
                            careerScope = futureScope,
                            careerScopeKn = futureScopeKn,
                            description = desc,
                            descriptionKn = descKn,
                            topCollegesCount = 20,
                            imageUrl = imageUrl,
                            tagline = obj.optString("tagline", ""),
                            taglineKn = obj.optString("taglineKn", ""),
                            overview = obj.optString("overview", ""),
                            overviewKn = obj.optString("overviewKn", ""),
                            whyChoose = obj.optString("whyChoose", ""),
                            whyChooseKn = obj.optString("whyChooseKn", ""),
                            benefits = obj.optString("benefits", ""),
                            benefitsKn = obj.optString("benefitsKn", ""),
                            subjects = obj.optString("subjects", ""),
                            subjectsKn = obj.optString("subjectsKn", ""),
                            exams = obj.optString("exams", ""),
                            examsKn = obj.optString("examsKn", ""),
                            pathway = obj.optString("pathway", ""),
                            pathwayKn = obj.optString("pathwayKn", ""),
                            skills = obj.optString("skills", ""),
                            skillsKn = obj.optString("skillsKn", ""),
                            jobRoles = obj.optString("jobRoles", ""),
                            jobRolesKn = obj.optString("jobRolesKn", ""),
                            opportunities = obj.optString("opportunities", ""),
                            opportunitiesKn = obj.optString("opportunitiesKn", ""),
                            topColleges = obj.optString("topColleges", ""),
                            topCollegesKn = obj.optString("topCollegesKn", ""),
                            topRecruiters = obj.optString("topRecruiters", ""),
                            topRecruitersKn = obj.optString("topRecruitersKn", ""),
                            salaryAverage = salAvg,
                            salaryHighest = obj.optString("salaryHighest", "₹ 20+ LPA"),
                            feesAverage = feesAvg,
                            scholarships = obj.optString("scholarships", ""),
                            scholarshipsKn = obj.optString("scholarshipsKn", ""),
                            futureScope = futureScope,
                            futureScopeKn = futureScopeKn,
                            certifications = obj.optString("certifications", ""),
                            certificationsKn = obj.optString("certificationsKn", ""),
                            faqs = obj.optString("faqs", ""),
                            faqsKn = obj.optString("faqsKn", ""),
                            detailedPathway = obj.opt("detailedPathway")?.toString() ?: "",
                            detailedPathwayKn = obj.opt("detailedPathwayKn")?.toString() ?: "",
                            officialExamLinks = obj.opt("officialExamLinks")?.toString() ?: "",
                            cutoffGuidance = obj.opt("cutoffGuidance")?.toString() ?: ""
                        )
                    )
                }
                val result = if (list.isNotEmpty()) list else initialCourses
                cachedCourses = result
                result
            } catch (e: Exception) {
                initialCourses
            }
        }
    }

    fun loadRawCoursesJson(context: Context): List<Map<String, Any>> {
        cachedRawCourses?.let { return it }
        return synchronized(this) {
            cachedRawCourses?.let { return it }
            try {
                val jsonStr = try {
                    context.assets.open("courses.json").bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    context.assets.open("courses_200_real_relevant_images.json").bufferedReader().use { it.readText() }
                }
                val array = JSONArray(jsonStr)
                val list = mutableListOf<Map<String, Any>>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val map = mutableMapOf<String, Any>()
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = obj.opt(key)
                        val converted = toFirestoreValue(value)
                        if (converted != null) {
                            map[key] = converted
                        }
                    }
                    val id = obj.optInt("id", i + 1)
                    val title = obj.optString("name", obj.optString("title", ""))
                    val titleKn = obj.optString("nameKn", obj.optString("titleKn", ""))
                    val rawImg = obj.optString("imageUrl", obj.optString("image", ""))
                    val img = normalizeImageUrl(rawImg)
                    val duration = obj.optString("durationYears", obj.optString("duration", "3-4 Years"))
                    val feesAvg = obj.optString("feesAverage", "3.5 Lakhs")
                    val salAvg = obj.optString("salaryAverage", "8.0 Lakhs")
                    val careerScope = obj.optString("careerScope", obj.optString("futureScope", ""))
                    val careerScopeKn = obj.optString("careerScopeKn", obj.optString("futureScopeKn", ""))

                    map["id"] = id
                    map["code"] = "CRS-%03d".format(id)
                    map["name"] = title
                    map["nameKn"] = titleKn
                    map["title"] = title
                    map["titleKn"] = titleKn
                    map["imageUrl"] = img
                    map["image"] = img
                    map["durationYears"] = duration
                    map["duration"] = duration
                    map["avgFeesLakhs"] = extractFirstNumber(feesAvg, 3.5)
                    map["avgPackageLakhs"] = extractFirstNumber(salAvg, 8.0)
                    map["careerScope"] = careerScope
                    map["careerScopeKn"] = careerScopeKn
                    map["topCollegesCount"] = 20

                    list.add(map)
                }
                cachedRawCourses = list
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    val initialCareers = listOf(
        CareerEntity(
            id = 1,
            title = "Doctor (MBBS / MD)",
            titleKn = "ವೈದ್ಯರು (MBBS / MD)",
            image = "https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=800",
            tagline = "Diagnose, treat, and heal patients with clinical excellence",
            taglineKn = "ಕ್ಲಿನಿಕಲ್ ಶ್ರೇಷ್ಠತೆಯೊಂದಿಗೆ ರೋಗಿಗಳನ್ನು ಪತ್ತೆಹಚ್ಚಿ ಚಿಕಿತ್ಸೆ ನೀಡಿ",
            description = "A medical doctor diagnoses and treats human diseases, performs surgeries, prescribes life-saving medications, and promotes public healthcare.",
            descriptionKn = "ವೈದ್ಯರು ಮಾನವ ರೋಗಗಳನ್ನು ಪತ್ತೆಹಚ್ಚಿ ಚಿಕಿತ್ಸೆ ನೀಡುತ್ತಾರೆ, ಶಸ್ತ್ರಚಿಕಿತ್ಸೆಗಳನ್ನು ಮಾಡುತ್ತಾರೆ ಮತ್ತು ಸಾರ್ವಜನಿಕ ಆರೋಗ್ಯವನ್ನು ಉತ್ತೇಜಿಸುತ್ತಾರೆ.",
            overview = "Becoming a Doctor is one of the most noble and rewarding professions worldwide. Doctors specialize in fields like Cardiology, Neurology, Pediatrics, Surgery, and General Medicine.",
            overviewKn = "ವೈದ್ಯರಾಗುವುದು ಜಗತ್ತಿನ ಅತ್ಯಂತ ಗೌರವಾನ್ವಿತ ವೃತ್ತಿಯಾಗಿದೆ. ವೈದ್ಯರು ಹೃದ್ರೋಗ, ನರರೋಗ, ಮಕ್ಕಳ ವೈದ್ಯಕೀಯ ಮುಂತಾದ ಕ್ಷೇತ್ರಗಳಲ್ಲಿ ಪರಿಣತಿ ಪಡೆಯುತ್ತಾರೆ.",
            whyChoose = "High societal respect, lifelong job security, ability to save human lives, and excellent financial stability.",
            whyChooseKn = "ಉನ್ನತ ಸಾಮಾಜಿಕ ಗೌರವ, ಜೀವಿತಾವಧಿಯ ಉದ್ಯೋಗ ಭದ್ರತೆ, ಜೀವ ಉಳಿಸುವ ಸಾಮರ್ಥ್ಯ ಮತ್ತು ಅತ್ಯುತ್ತಮ ಆರ್ಥಿಕ ಸ್ಥಿರತೆ.",
            benefits = "• Noble social impact & saving lives\n• High earning potential in private & government sectors\n• Global career demand in UK, US, Gulf, and India\n• Specialist practice flexibility",
            benefitsKn = "• ಸಮಾಜಕ್ಕೆ ಸೇವೆ ಸಲ್ಲಿಸುವ ಅವಕಾಶ\n• ಖಾಸಗಿ ಮತ್ತು ಸರ್ಕಾರಿ ಕ್ಷೇತ್ರಗಳಲ್ಲಿ ಹೆಚ್ಚಿನ ಆದಾಯ\n• ಜಾಗತಿಕ ಉದ್ಯೋಗ ಬೇಡಿಕೆ\n• ತಜ್ಞ ವೈದ್ಯಕೀಯ ಅಭ್ಯಾಸದ ನಮ್ಯತೆ",
            eligibility = "12th Standard Science with PCB (Physics, Chemistry, Biology) minimum 50-60% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ ವಿಜ್ಞಾನ (PCB) ಕನಿಷ್ಠ 50-60% ಅಂಕಗಳು.",
            subjects = "Physics, Chemistry, Biology, Anatomy, Physiology, Pathology, Pharmacology, Surgery, General Medicine",
            subjectsKn = "ಭೌತಶಾಸ್ತ್ರ, ರಸಾಯನಶಾಸ್ತ್ರ, ಜೀವಶಾಸ್ತ್ರ, ಅನಾಟಮಿ, ಫಾರ್ಮಾಕಾಲಜಿ",
            exams = "NEET UG (Undergraduate), NEET PG / NEXT (Postgraduate), USMLE (USA), PLAB (UK)",
            examsKn = "NEET UG, NEET PG, NEXT, USMLE, PLAB",
            pathway = "Step 1: 10+2 PCB Science\nStep 2: Clear NEET UG exam\nStep 3: 5.5 Years MBBS Degree (4.5 yrs study + 1 yr internship)\nStep 4: Register with State Medical Council\nStep 5: Optional MD/MS PG specialization (3 years)",
            pathwayKn = "ಹಂತ 1: 10+2 PCB ವಿಜ್ಞಾನ\nಹಂತ 2: NEET UG ಪರೀಕ್ಷೆಯಲ್ಲಿ ಉತ್ತೀರ್ಣರಾಗಿ\nಹಂತ 3: 5.5 ವರ್ಷಗಳ MBBS ಪದವಿ\nಹಂತ 4: ವೈದ್ಯಕೀಯ ಮಂಡಳಿಯಲ್ಲಿ ನೋಂದಣಿ",
            skills = "Clinical Diagnosis, Emergency Triage, Surgical Precision, Compassion, Critical Thinking, Decision Making under Pressure",
            skillsKn = "ರೋಗನಿರ್ಣಯ, ಶಸ್ತ್ರಚಿಕಿತ್ಸೆಯ ನಿಖರತೆ, ಕರುಣೆ, ವಿಮರ್ಶಾತ್ಮಕ ಚಿಂತನೆ",
            jobRoles = "General Physician, Surgeon, Cardiologist, Pediatrician, Neurologist, Medical Officer, Hospital Director",
            jobRolesKn = "ಸಾಮಾನ್ಯ ವೈದ್ಯರು, ಶಸ್ತ್ರಚಿಕಿತ್ಸಕರು, ಹೃದ್ರೋಗ ತಜ್ಞರು, ಮಕ್ಕಳ ವೈದ್ಯರು",
            opportunities = "Government Hospitals, Private Multispecialty Chains, Own Private Clinic, Medical Research Institutes, WHO / International Health bodies",
            opportunitiesKn = "ಸರ್ಕಾರಿ ಆಸ್ಪತ್ರೆಗಳು, ಖಾಸಗಿ ಸೂಪರ್ ಸ್ಪೆಷಾಲಿಟಿ ಆಸ್ಪತ್ರೆಗಳು, ಸ್ವಂತ ಕ್ಲಿನಿಕ್",
            topColleges = "AIIMS New Delhi, CMC Vellore, JIPMER, Kasturba Medical College (KMC) Manipal, St. John's Medical College Bangalore",
            topCollegesKn = "AIIMS ನವದೆಹಲಿ, CMC ವೆಲ್ಲೂರು, KMC ಮಣಿಪಾಲ್, ಸೇಂಟ್ ಜಾನ್ಸ್ ಬೆಂಗಳೂರು",
            topRecruiters = "Apollo Hospitals, Fortis Healthcare, Max Healthcare, Manipal Hospitals, Narayana Health, AIIMS, NHS UK",
            topRecruitersKn = "ಅಪೋಲೋ ಆಸ್ಪತ್ರೆಗಳು, ಫೋರ್ಟಿಸ್, ಮಣಿಪಾಲ್ ಆಸ್ಪತ್ರೆಗಳು, ನಾರಾಯಣ ಹೆಲ್ತ್",
            salaryAverage = "₹ 10 - 18 Lakhs / Year",
            salaryHighest = "₹ 50+ Lakhs / Year",
            feesAverage = "₹ 1 - 15 Lakhs / Year",
            duration = "5.5 Years (MBBS) + 3 Years (MD/MS)",
            scholarships = "National Merit Scholarship, Central Sector Scholarship, Post Matric AYUSH & Medical Fellowships",
            scholarshipsKn = "ರಾಷ್ಟ್ರೀಯ ಮೆರಿಟ್ ವಿದ್ಯಾರ್ಥಿವೇತನ, ಸರ್ಕಾರಿ ಮೆಡಿಕಲ್ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "Extremely high demand due to growing healthcare infrastructure, telemedicine adoption, and aging population.",
            futureScopeKn = "ಆರೋಗ್ಯ ರಕ್ಷಣೆಯ ಅಗತ್ಯತೆಗಳು ಹೆಚ್ಚುತ್ತಿರುವುದರಿಂದ ಭವಿಷ್ಯದಲ್ಲಿ ಅಪಾರ ಬೇಡಿಕೆ ಇದೆ.",
            certifications = "State Medical Registration Certificate, ACLS / BLS Emergency Certification, Board Specialization Diploma",
            certificationsKn = "ರಾಜ್ಯ ವೈದ್ಯಕೀಯ ನೋಂದಣಿ ಪ್ರಮಾಣಪತ್ರ, BLS / ACLS ಪ್ರಮಾಣಪತ್ರ",
            faqs = "Q: Is NEET compulsory for MBBS in India?\nA: Yes, NEET UG is 100% mandatory for all medical admissions in India and abroad.\n\nQ: How long does it take to become a Specialist Doctor?\nA: Total 8.5 years (5.5 yrs MBBS + 3 yrs MD/MS).",
            faqsKn = "ಪ್ರಶ್ನೆ: ಭಾರತದಲ್ಲಿ MBBS ಗೆ NEET ಕಡ್ಡಾಯವೇ?\nಉತ್ತರ: ಹೌದು, ಭಾರತ ಮತ್ತು ವಿದೇಶಗಳಲ್ಲಿ MBBS ಪ್ರವೇಶಕ್ಕೆ NEET ಕಡ್ಡಾಯವಾಗಿದೆ.",
            category = "Medical"
        ),
        CareerEntity(
            id = 2,
            title = "Software Engineer & AI Architect",
            titleKn = "ಸಾಫ್ಟ್‌ವೇರ್ ಎಂಜಿನಿಯರ್ ಮತ್ತು AI ಆರ್ಕಿಟೆಕ್ಟ್",
            image = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800",
            tagline = "Design cutting-edge software applications and AI intelligence engines",
            taglineKn = "ಆಧುನಿಕ ಸಾಫ್ಟ್‌ವೇರ್ ಮತ್ತು AI ತಂತ್ರಜ್ಞಾನವನ್ನು ನಿರ್ಮಿಸಿ",
            description = "Software Engineers design, code, test, and deploy web applications, mobile apps, enterprise cloud infrastructure, and artificial intelligence models.",
            descriptionKn = "ಸಾಫ್ಟ್‌ವೇರ್ ಎಂಜಿನಿಯರ್‌ಗಳು ವೆಬ್ ಅಪ್ಲಿಕೇಶನ್‌ಗಳು, ಮೊಬೈಲ್ ಆಪ್‌ಗಳು ಮತ್ತು AI ಮಾದರಿಗಳನ್ನು ವಿನ್ಯಾಸಗೊಳಿಸಿ ಕೋಡಿಂಗ್ ಮಾಡುತ್ತಾರೆ.",
            overview = "The backbone of the global digital economy. Software engineers solve complex computational problems and power silicon technologies.",
            overviewKn = "ಡಿಜಿಟಲ್ ಜಗತ್ತಿನ ಪ್ರಮುಖ ತಂತ್ರಜ್ಞಾನ ಕ್ಷೇತ್. ಸಾಫ್ಟ್‌ವೇರ್ ಎಂಜಿನಿಯರ್‌ಗಳು ಡಿಜಿಟಲ್ ಪರಿಹಾರಗಳನ್ನು ನಿರ್ಮಿಸುತ್ತಾರೆ.",
            whyChoose = "Highest placement packages, remote work flexibility, creative problem solving, and rapid career progression.",
            whyChooseKn = "ಅಧಿಕ ವೇತನದ ಪ್ಯಾಕೇಜ್‌ಗಳು, ವರ್ಕ್ ಫ್ರಮ್ ಹೋಮ್ ನಮ್ಯತೆ ಮತ್ತು ವೇಗದ ವೃತ್ತಿ ಬೆಳವಣಿಗೆ.",
            benefits = "• Exceptional global compensation packages\n• Work remotely from anywhere in the world\n• Opportunity to build tech startups and SaaS tools\n• Rapid career progression to Tech Lead / CTO",
            benefitsKn = "• ಅತ್ಯುತ್ತಮ ಜಾಗತಿಕ ವೇತನ\n• ರಿಮೋಟ್ ಕೆಲಸದ ಅವಕಾಶ\n• ಸ್ವಂತ ಟೆಕ್ ಸ್ಟಾರ್ಟ್‌ಅಪ್ ಆರಂಭಿಸುವ ಸಾಮರ್ಥ್ಯ",
            eligibility = "12th Science PCM / Commerce with Computer Science / Any Bachelor's with coding skills.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCM/CS) ಅಥವಾ ಕಂಪ್ಯೂಟರ್ ಕೋಡಿಂಗ್ ಆಸಕ್ತಿ.",
            subjects = "Data Structures & Algorithms, Python, Java, Systems Architecture, Cloud Computing, Machine Learning",
            subjectsKn = "ಡೇಟಾ ಸ್ಟ್ರಕ್ಚರ್ಸ್, ಪೈಥಾನ್, ಜಾವಾ, ಮೆಷಿನ್ ಲರ್ನಿಂಗ್",
            exams = "JEE Main, JEE Advanced, KCET, COMEDK, GATE CS",
            examsKn = "JEE Main, JEE Advanced, KCET, COMEDK, GATE",
            pathway = "Step 1: 10+2 PCM\nStep 2: B.Tech Computer Science / BCA / B.Sc CS\nStep 3: Master Data Structures & System Design\nStep 4: Tech internships & open source projects\nStep 5: Campus / Off-campus recruitment in top tech firms",
            pathwayKn = "ಹಂತ 1: 10+2 PCM\nಹಂತ 2: B.Tech CS / BCA\nಹಂತ 3: ಕೋಡಿಂಗ್ ಕೌಶಲ್ಯಗಳ ಕರಗತ\nಹಂತ 4: ಪ್ರಮುಖ ಕಂಪನಿಗಳಲ್ಲಿ ಕೆಲಸ ಪಡೆಯಿರಿ",
            skills = "Python, Java, Kotlin, React, SQL, Cloud Architectures (AWS/GCP), Problem Solving",
            skillsKn = "ಪೈಥಾನ್, ಜಾವಾ, ಕೊಟ್ಲಿನ್, SQL, ಕ್ಲೌಡ್ ತಂತ್ರಜ್ಞಾನ",
            jobRoles = "Full Stack Developer, Mobile App Developer, Cloud Engineer, DevOps Lead, AI Researcher, CTO",
            jobRolesKn = "ಫುಲ್ ಸ್ಟಾಕ್ ಡೆವಲಪರ್, ಮೊಬೈಲ್ ಆಪ್ ಡೆವಲಪರ್, AI ಎಂಜಿನಿಯರ್",
            opportunities = "Global Tech Multinationals (FAANG), High-growth Unicorn Startups, Remote Global Companies, IT Consulting",
            opportunitiesKn = "ಗೂಗಲ್, ಮೈಕ್ರೋಸಾಫ್ಟ್, ಅಮೆಜಾನ್‌ನಂತಹ ಜಾಗತಿಕ ಕಂಪನಿಗಳು",
            topColleges = "IIT Bombay, IIT Bangalore (IISc), NIT Surathkal, BITS Pilani, IIIT Hyderabad, RVCE Bangalore",
            topCollegesKn = "IIT ಬಾಂಬೆ, BITS ಪಿಲಾನಿ, IIIT ಹೈದರಾಬಾದ್, RVCE ಬೆಂಗಳೂರು",
            topRecruiters = "Google, Microsoft, Amazon, Apple, Meta, TCS, Infosys, Wipro, Uber, Flipkart",
            topRecruitersKn = "ಗೂಗಲ್, ಮೈಕ್ರೋಸಾಫ್ಟ್, ಅಮೆಜಾನ್, ಇನ್ಫೋಸಿಸ್, ಟಿಸಿಎಸ್",
            salaryAverage = "₹ 8 - 22 Lakhs / Year",
            salaryHighest = "₹ 1+ Crore / Year",
            feesAverage = "₹ 2 - 6 Lakhs / Year",
            duration = "4 Years (B.Tech) / 3 Years (BCA)",
            scholarships = "Google Generation Scholarship, Amazon Future Engineer, Post-Matric Merit Grants",
            scholarshipsKn = "ಗೂಗಲ್ ಜೆನರೇಶನ್ ಸ್ಕಾಲರ್‌ಶಿಪ್, ಅಮೆಜಾನ್ ಫ್ಯೂಚರ್ ಎಂಜಿನಿಯರ್",
            futureScope = "Immense demand driven by AI automation, Cloud computing, Cybersecurity, and IoT expansion.",
            futureScopeKn = "ಆರ್ಟಿಫಿಷಿಯಲ್ ಇಂಟೆಲಿಜೆನ್ಸ್ ಮತ್ತು ಕ್ಲೌಡ್ ತಂತ್ರಜ್ಞಾನದ ಕಾರಣದಿಂದ ಅತ್ಯುನ್ನತ ಭವಿಷ್ಯ.",
            certifications = "AWS Certified Solutions Architect, Google Cloud Professional, Certified Kubernetes Administrator",
            certificationsKn = "AWS ಕ್ಲೌಡ್ ಸರ್ಟಿಫಿಕೇಟ್, ಗೂಗಲ್ ಕ್ಲೌಡ್ ಸರ್ಟಿಫಿಕೇಟ್",
            faqs = "Q: Can non-CS students become Software Engineers?\nA: Yes! Coding skills and portfolio projects matter more than degree title.\n\nQ: Is AI replacing Software Engineers?\nA: No, AI amplifies developer productivity.",
            faqsKn = "ಪ್ರಶ್ನೆ: CS ಅಲ್ಲದ ವಿದ್ಯಾರ್ಥಿಗಳು ಸಾಫ್ಟ್‌ವೇರ್ ಎಂಜಿನಿಯರ್ ಆಗಬಹುದೇ?\nಉತ್ತರ: ಹೌದು, ಕೋಡಿಂಗ್ ಕೌಶಲ್ಯ ಹೊಂದಿದ್ದರೆ ಖಂಡಿತ ಆಗಬಹುದು.",
            category = "Engineering"
        ),
        CareerEntity(
            id = 3,
            title = "Commercial Airline Pilot",
            titleKn = "ವಾಣಿಜ್ಯ ವಿಮಾನ ಪೈಲಟ್ (Commercial Pilot)",
            image = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800",
            tagline = "Fly luxury commercial passenger jets across global international airways",
            taglineKn = "ಜಾಗತಿಕ ವಿಮಾನ ಮಾರ್ಗಗಳಲ್ಲಿ ಪ್ರಯಾಣಿಕ ವಿಮಾನಗಳನ್ನು ಹಾರಿಸಿ",
            description = "Commercial Pilots operate passenger airlines, cargo freighters, and private jets while managing flight plans, navigation, and passenger safety.",
            descriptionKn = "ವಾಣಿಜ್ಯ ಪೈಲಟ್‌ಗಳು ಪ್ರಯಾಣಿಕರ ಮತ್ತು ಸರಕು ವಿಮಾನಗಳನ್ನು ಸುರಕ್ಷಿತವಾಗಿ ಹಾರಿಸುತ್ತಾರೆ.",
            overview = "A high-flying career offering adventure, world travel, high social prestige, and lucrative airline salaries.",
            overviewKn = "ಸಾಹಸಮಯ, ಜಗತ್ತಿನಾದ್ಯಂತ ಪ್ರವಾಸ ಮಾಡುವ ಮತ್ತು ಗೌರವಾನ್ವಿತ ವೃತ್ತಿಯಾಗಿದೆ.",
            whyChoose = "Travel the world, high salary packages, prestigious uniform, and thrilling aviation lifestyle.",
            whyChooseKn = "ಜಗತ್ತಿನಾದ್ಯಂತ ಉಚಿತ ಪ್ರಯಾಣ, ಹೆಮ್ಮೆಯ ಯುನಿಫಾರ್ಮ್, ಉನ್ನತ ವೇತನ.",
            benefits = "• Unlimited free global travel for pilot & family\n• Premium tax benefits & five-star stays\n• High prestige and commanding flight deck authority",
            benefitsKn = "• ಪೈಲಟ್ ಮತ್ತು ಕುಟುಂಬಕ್ಕೆ ಉಚಿತ ಪ್ರಯಾಣ\n• ಪಂಚತಾರಾ ಆತಿಥ್ಯ ಮತ್ತು ಅತ್ಯುನ್ನತ ವೇತನ",
            eligibility = "12th Standard Science with Physics & Mathematics min 50% + DGCA Class 1 Medical Fitness.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCM) ಕನಿಷ್ಠ 50% ಅಂಕಗಳು + ಮೆಡಿಕಲ್ ಫಿಟ್ನೆಸ್.",
            subjects = "Air Navigation, Aviation Meteorology, Air Regulations, Flight Performance, Aircraft Technical General",
            subjectsKn = "ಏರ್ ನೇವಿಗೇಶನ್, ಹವಾಮಾನ ಶಾಸ್ತ್ರ, ವಿಮಾನ ನಿಯಮಾವಳಿಗಳು",
            exams = "IGRUA Entrance Exam, DGCA CPL Theory Exams, Airline Cadet Pilot Tests",
            examsKn = "IGRUA ಪ್ರವೇಶ ಪರೀಕ್ಷೆ, DGCA CPL ಪರೀಕ್ಷೆಗಳು",
            pathway = "Step 1: 10+2 PCM\nStep 2: Obtain DGCA Class 2 & Class 1 Medical Certificates\nStep 3: Join DGCA Flying School / Airline Cadet Program\nStep 4: Complete 200 Flying Hours + Ground School\nStep 5: Get Commercial Pilot License (CPL)\nStep 6: Type Rating (Airbus A320 / Boeing 737) & Airline Induction",
            pathwayKn = "ಹಂತ 1: 10+2 PCM\nಹಂತ 2: DGCA ವೈದ್ಯಕೀಯ ಪರೀಕ್ಷೆ\nಹಂತ 3: ಫ್ಲೈಯಿಂಗ್ ಸ್ಕೂಲ್ ಸೇರ್ಪಡೆ\nಹಂತ 4: 200 ಗಂಟೆಗಳ ವಿಮಾನ ಚಾಲನೆ ಮತ್ತು CPL ಲೈಸೆನ್ಸ್ ಪಡೆಯಿರಿ",
            skills = "Spatial Awareness, Multi-tasking, Calm under Emergency, Communication, CRM Leadership",
            skillsKn = "ನಾಯಕತ್ವ, ತುರ್ತು ಪರಿಸ್ಥಿತಿಯಲ್ಲಿ ಶಾಂತತೆ, ಕಮ್ಯುನಿಕೇಶನ್",
            jobRoles = "First Officer (Co-Pilot), Flight Captain, Senior Commander, Flight Instructor, Chief Pilot",
            jobRolesKn = "ಫಸ್ಟ್ ಆಫೀಸರ್ (ಕೋ-ಪೈಲಟ್), ಫ್ಲೈಟ್ ಕ್ಯಾಪ್ಟನ್, ಇನ್ಸ್ಟ್ರಕ್ಟರ್",
            opportunities = "Domestic Commercial Airlines, International Flag Carriers, Cargo Airlines (FedEx/DHL), Private Jet Charter",
            opportunitiesKn = "ಇಂಡಿಗೋ, ಏರ್ ಇಂಡಿಯಾ, ಎಮಿರೇಟ್ಸ್, ಕತಾರ್ ಏರ್‌ವೇಸ್",
            topColleges = "IGRUA Amethi, CAE Oxford Aviation, New Zealand International Flying Academy, Chimes Aviation",
            topCollegesKn = "IGRUA ಅಮೇಥಿ, CAE ಆಕ್ಸ್‌ಫರ್ಡ್ ಏವಿಯೇಷನ್, ನ್ಯೂಜಿಲ್ಯಾಂಡ್ ಫ್ಲೈಯಿಂಗ್ ಅಕಾಡೆಮಿ",
            topRecruiters = "IndiGo Airlines, Air India, Emirates, Qatar Airways, Singapore Airlines, SpiceJet",
            topRecruitersKn = "ಇಂಡಿಗೋ, ಏರ್ ಇಂಡಿಯಾ, ಎಮಿರೇಟ್ಸ್, ಸಿಂಗಾಪುರ್ ಏರ್‌ವೇಸ್",
            salaryAverage = "₹ 18 - 35 Lakhs / Year",
            salaryHighest = "₹ 75+ Lakhs / Year",
            feesAverage = "₹ 35 - 50 Lakhs (One-time CPL Training)",
            duration = "1.5 to 2.5 Years",
            scholarships = "Government Aviation Subsidy, Airline Cadet Pilot Loan Schemes",
            scholarshipsKn = "ಸರ್ಕಾರಿ ಸಬ್ಸಿಡಿ, ಏರ್‌ಲೈನ್ ಕೆಡೆಟ್ ಸಾಲ ಯೋಜನೆಗಳು",
            futureScope = "Booming demand in India due to 1,000+ new aircraft orders by IndiGo and Air India.",
            futureScopeKn = "ಭಾರತದಲ್ಲಿ ನೂರಾರು ಹೊಸ ವಿಮಾನಗಳ ಸೇರ್ಪಡೆಯಿಂದ ಪೈಲಟ್‌ಗಳಿಗೆ ಭಾರೀ ಬೇಡಿಕೆ ಇದೆ.",
            certifications = "Commercial Pilot License (CPL), Multi-Engine Rating (ME), Instrument Rating (IR), Radio Telephony (RTR-A)",
            certificationsKn = "Commercial Pilot License (CPL), Type Rating A320/B737",
            faqs = "Q: What is the age limit for starting CPL training?\nA: Minimum 17 years to start, no upper limit for commercial flying up to 65 years.\n\nQ: Is wearing spectacles allowed?\nA: Yes, 6/6 vision with corrective glasses is acceptable under DGCA rules.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಕನ್ನಡಕ ಧರಿಸುವವರು ಪೈಲಟ್ ಆಗಬಹುದೇ?\nಉತ್ತರ: ಹೌದು, ಸುಧಾರಿತ ದೃಷ್ಟಿ 6/6 ಇದ್ದರೆ DGCA ಅನುಮತಿಸುತ್ತದೆ.",
            category = "Aviation"
        ),
        CareerEntity(
            id = 4,
            title = "Advocate & Corporate Lawyer",
            titleKn = "ವಕೀಲರು ಮತ್ತು ಕಾರ್ಪೊರೇಟ್ ಲಾಯರ್",
            image = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800",
            tagline = "Uphold justice, represent litigation cases, and advise corporate legal deals",
            taglineKn = "ನ್ಯಾಯವನ್ನು ಎತ್ತಿಹಿಡಿಯಿರಿ ಮತ್ತು ಕಾರ್ಪೊರೇಟ್ ಕಾನೂನು ಸಲಹೆ ನೀಡಿ",
            description = "Lawyers litigate court disputes, draft legal contracts, advise corporations on M&A compliance, and defend constitutional rights.",
            descriptionKn = "ವಕೀಲರು ನ್ಯಾಯಾಲಯದ ವ್ಯಾಜ್ಯಗಳನ್ನು ವಾದಿಸುತ್ತಾರೆ, ಒಪ್ಪಂದಗಳನ್ನು ಸಿದ್ಧಪಡಿಸುತ್ತಾರೆ ಮತ್ತು ಕಾರ್ಪೊರೇಟ್ ಕಾನೂನು ಸಲಹೆ ನೀಡುತ್ತಾರೆ.",
            overview = "Law is an intellectually stimulating career offering societal impact, corporate power, and independent practice.",
            overviewKn = "ಸಮಾಜದ ಮೇಲೆ ಪ್ರಭಾವ ಬೀರುವ ಮತ್ತು ಉನ್ನತ ಪ್ರತಿಷ್ಠೆಯ ವೃತ್ತಿಯಾಗಿದೆ.",
            whyChoose = "High intellectual independence, lucrative corporate consultancy fees, prestige, and judicial service pathways.",
            whyChooseKn = "ಸ್ವತಂತ್ರ ವೃತ್ತಿ, ಉನ್ನತ ಕಾರ್ಪೊರೇಟ್ ವೇತನ ಮತ್ತು ನ್ಯಾಯಾಂಗ ಸೇವೆಗಳ ಅವಕಾಶ.",
            benefits = "• Independent private litigation practice\n• High corporate compensation in M&A deals\n• Pathway to become Civil Judge or High Court Judge",
            benefitsKn = "• ಸ್ವತಂತ್ರ ವಕೀಲ ವೃತ್ತಿ\n• ಜಡ್ಜ್ ಅಥವಾ ಮ್ಯಾಜಿಸ್ಟ್ರೇಟ್ ಆಗುವ ಅವಕಾಶ",
            eligibility = "12th Standard Any Stream (Arts, Commerce, Science) minimum 45% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (ಯಾವುದೇ ವಿಭಾಗ) ಕನಿಷ್ಠ 45% ಅಂಕಗಳು.",
            subjects = "Constitutional Law, Criminal Jurisprudence, Corporate Law, Cyber Law, Contract Law, Intellectual Property",
            subjectsKn = "ಸಂವಿಧಾನ ಕಾನೂನು, ಅಪರಾಧ ಕಾನೂನು, ಕಾರ್ಪೊರೇಟ್ ಕಾನೂನು",
            exams = "CLAT (Common Law Admission Test), AILET, LSAT India, AIBE (All India Bar Examination)",
            examsKn = "CLAT, AILET, LSAT, AIBE",
            pathway = "Step 1: 10+2 Any Stream\nStep 2: Clear CLAT entrance exam\nStep 3: 5 Years Integrated BA LLB / BBA LLB\nStep 4: Enroll with State Bar Council\nStep 5: Pass AIBE exam for All-India Court Practice",
            pathwayKn = "ಹಂತ 1: 10+2 ಯಾವುದೇ ವಿಭಾಗ\nಹಂತ 2: CLAT ಪರೀಕ್ಷೆ ಉತ್ತೀರ್ಣ\nಹಂತ 3: 5 ವರ್ಷಗಳ BA LLB\nಹಂತ 4: ಬಾರ್ ಕೌನ್ಸಿಲ್‌ನೊಂದಿಗೆ ನೋಂದಣಿ",
            skills = "Persuasive Argumentation, Contract Drafting, Legal Research, Critical Reasoning, Negotiation",
            skillsKn = "ವಾದ ಮಂಡನೆ, ಕಾನೂನು ಸಂಶೋಧನೆ, ಸಂವಹನ ಕೌಶಲ್ಯ",
            jobRoles = "Litigation Advocate, Corporate Legal Advisor, In-House Counsel, Judicial Magistrate, Legal Consultant",
            jobRolesKn = "ನ್ಯಾಯಾಲಯದ ವಕೀಲರು, ಕಾರ್ಪೊರೇಟ್ ಲಾಯರ್, ಜಡ್ಜ್",
            opportunities = "Supreme Court / High Court Advocacy, Tier-1 Law Firms, Multinational Corporations, Banking Sector, Judiciary",
            opportunitiesKn = "ಹೈಕೋರ್ಟ್ / ಸುಪ್ರೀಂ ಕೋರ್ಟ್, ಪ್ರಮುಖ ಲಾಯರ್ ಫರ್ಮ್‌ಗಳು, ಕಂಪನಿಗಳು",
            topColleges = "NLSIU Bangalore, NALSAR Hyderabad, WBNUJS Kolkata, NLU Delhi, Symbiosis Law School",
            topCollegesKn = "NLSIU ಬೆಂಗಳೂರು, NALSAR ಹೈದರಾಬಾದ್, NLU ದೆಹಲಿ",
            topRecruiters = "Shardul Amarchand Mangaldas, AZB & Partners, Trilegal, Khaitan & Co, Big 4 Consultancies, Tata Group",
            topRecruitersKn = "ಶಾರ್ದೂಲ್ ಅಮರ್‌ಚಂದ್ ಮಂಗಳದಾಸ್, ಅಝೆಡ್‌ಬಿ & ಪಾರ್ಟ್ನರ್ಸ್, ಟಾಟಾ",
            salaryAverage = "₹ 8 - 18 Lakhs / Year",
            salaryHighest = "₹ 45+ Lakhs / Year",
            feesAverage = "₹ 2 - 4 Lakhs / Year",
            duration = "5 Years (Integrated LLB) / 3 Years (After Graduation)",
            scholarships = "Aditya Birla Law Scholarship, Government Post-Matric Merit Grants",
            scholarshipsKn = "ಆದಿತ್ಯ ಬಿರ್ಲಾ ಲಾ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "High expansion due to cyber laws, AI ethics, corporate compliance, and intellectual property patents.",
            futureScopeKn = "ಸೈಬರ್ ಕಾನೂನು ಮತ್ತು ಕಾರ್ಪೊರೇಟ್ ವಲಯದ ಬೆಳೆವಣಿಗೆಯಿಂದ ಉತ್ತಮ ಭವಿಷ್ಯ.",
            certifications = "Certificate of Practice (AIBE), Cyber Law Specialist Diploma, IPR Arbitration Certification",
            certificationsKn = "AIBE ಬಾರ್ ಕೌನ್ಸಿಲ್ ಪ್ರಮಾಣಪತ್ರ, ಸೈಬರ್ ಲಾ ಡಿಪ್ಲೊಮಾ",
            faqs = "Q: Is math necessary for law?\nA: No, basic numerical reasoning in CLAT is sufficient.\n\nQ: What is AIBE?\nA: All India Bar Examination required to practice in Indian courts.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಕಾನೂನು ಅಧ್ಯಯನಕ್ಕೆ ಗಣಿತ ಅಗತ್ಯವೇ?\nಉತ್ತರ: ಇಲ್ಲ, ಸಾಮಾನ್ಯ ತರ್ಕ ಸಾಕು.",
            category = "Law"
        ),
        CareerEntity(
            id = 5,
            title = "Electrical Engineer",
            titleKn = "ಎಲೆಕ್ಟ್ರಿಕಲ್ ಎಂಜಿನಿಯರ್ (Electrical Engineer)",
            image = "https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=800",
            tagline = "Power electric vehicles, renewable energy grids, and smart electronic circuits",
            taglineKn = "ಎಲೆಕ್ಟ್ರಿಕ್ ವಾಹನಗಳು ಮತ್ತು ವಿದ್ಯುತ್ ಗ್ರಿಡ್‌ಗಳನ್ನು ವಿನ್ಯಾಸಗೊಳಿಸಿ",
            description = "Electrical Engineers design, test, and manufacture electrical systems, EV motors, smart power distribution grids, and microelectronics.",
            descriptionKn = "ಎಲೆಕ್ಟ್ರಿಕಲ್ ಎಂಜಿನಿಯರ್‌ಗಳು ವಿದ್ಯುತ್ ವ್ಯವಸ್ಥೆಗಳು ಮತ್ತು ಎಲೆಕ್ಟ್ರಿಕ್ ವಾಹನ ಎಂಜಿನ್‌ಗಳನ್ನು ರೂಪಿಸುತ್ತಾರೆ.",
            overview = "Core engineering domain powering the green energy transition, electric vehicles (EV), and automation.",
            overviewKn = "ಎಲೆಕ್ಟ್ರಿಕ್ ವಾಹನಗಳು ಮತ್ತು ಸೋಲಾರ್ ಪವರ್ ಕ್ಷೇತ್ರದ ಪ್ರಮುಖ ಎಂಜಿನಿಯರಿಂಗ್ ವಿಭಾಗ.",
            whyChoose = "Core industry stability, government PSU jobs (NTPC/PowerGrid), and high growth in Electric Vehicle (EV) tech.",
            whyChooseKn = "ಸರ್ಕಾರಿ PSU ಉದ್ಯೋಗಗಳು ಮತ್ತು ಎಲೆಕ್ಟ್ರಿಕ್ ವಾಹನ ವಲಯದಲ್ಲಿ ಭಾರೀ ಬೇಡಿಕೆ.",
            benefits = "• Stable PSU government job security (GATE entry)\n• Pioneer roles in Electric Vehicles & Battery Tech\n• Renewable solar and wind energy expansion",
            benefitsKn = "• BHEL, NTPC ನಂತಹ PSU ಸರ್ಕಾರಿ ಕೆಲಸಗಳು\n• EV ಬ್ಯಾಟರಿ ತಂತ್ರಜ್ಞಾನದಲ್ಲಿ ಅವಕಾಶಗಳು",
            eligibility = "12th Standard Science with PCM (Physics, Chemistry, Math) minimum 55% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCM) ಕನಿಷ್ಠ 55% ಅಂಕಗಳು.",
            subjects = "Circuit Theory, Power Electronics, Control Systems, Microcontrollers, Renewable Energy, EV Motor Drives",
            subjectsKn = "ಸರ್ಕ್ಯೂಟ್ ಥಿಯರಿ, ಪವರ್ ಎಲೆಕ್ಟ್ರಾನಿಕ್ಸ್, ಇವಿ ಮೋಟಾರ್ ಡ್ರೈವ್‌ಗಳು",
            exams = "JEE Main, JEE Advanced, KCET, GATE Electrical",
            examsKn = "JEE Main, KCET, GATE",
            pathway = "Step 1: 10+2 PCM\nStep 2: Complete B.Tech Electrical Engineering (4 years)\nStep 3: Industry internships in Power / EV sector\nStep 4: Optional GATE exam for M.Tech or PSU recruitment",
            pathwayKn = "ಹಂತ 1: 10+2 PCM\nಹಂತ 2: 4 ವರ್ಷಗಳ B.Tech Electrical\nಹಂತ 3: GATE ಪರೀಕ್ಷೆ ಅಥವಾ ಖಾಸಗಿ ಉದ್ಯೋಗ",
            skills = "MATLAB, Circuit Simulation, Power Systems, PLC Automation, EV Powertrain Design",
            skillsKn = "MATLAB, ಪವರ್ ಸಿಸ್ಟಮ್ಸ್, PLC ಆಟೋಮೇಷನ್",
            jobRoles = "Power Systems Engineer, EV Powertrain Lead, Automation Engineer, Grid Consultant",
            jobRolesKn = "ಪವರ್ ಸಿಸ್ಟಮ್ಸ್ ಎಂಜಿನಿಯರ್, EV ಡಿಸೈನರ್, ಗ್ರಿಡ್ ಆಫೀಸರ್",
            topColleges = "IIT Madras, IIT Bombay, NIT Surathkal, RVCE Bangalore, BMSCE Bangalore",
            topCollegesKn = "IIT ಮದ್ರಾಸ್, NIT ಸುರತ್ಕಲ್, RVCE ಬೆಂಗಳೂರು",
            topRecruiters = "Tesla, Ather Energy, Tata Motors, PowerGrid Corporation, BHEL, ABB, Siemens, Schneider Electric",
            topRecruitersKn = "ಟಾಟಾ ಮೋಟಾರ್ಸ್, ಏಥರ್ ಎನರ್ಜಿ, BHEL, ಸೀಮೆನ್ಸ್",
            salaryAverage = "₹ 6 - 12 Lakhs / Year",
            salaryHighest = "₹ 28+ Lakhs / Year",
            feesAverage = "₹ 1.5 - 4 Lakhs / Year",
            duration = "4 Years",
            scholarships = "NTPC Engineering Scholarship, MHRD GATE Fellowship",
            scholarshipsKn = "NTPC ಎಂಜಿನಿಯರಿಂಗ್ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "Tremendous future driven by EV adoption, battery storage, and solar/wind power grids.",
            futureScopeKn = "ಎಲೆಕ್ಟ್ರಿಕ್ ವಾಹನ ಮತ್ತು ಸೋಲಾರ್ ಪವರ್‌ನಿಂದ ಪ್ರಕಾಶಮಾನ ಭವಿಷ್ಯ.",
            certifications = "Certified Power System Professional, EV Battery Management Systems Diploma",
            certificationsKn = "EV ಬ್ಯಾಟರಿ ಮ್ಯಾನೇಜ್‌ಮೆಂಟ್ ಸರ್ಟಿಫಿಕೇಟ್",
            faqs = "Q: What are the government career options for Electrical Engineers?\nA: BHEL, NTPC, PowerGrid, ISRO, and State Electricity Boards via GATE exam.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಎಲೆಕ್ಟ್ರಿಕಲ್ ಎಂಜಿನಿಯರ್‌ಗಳಿಗೆ ಸರ್ಕಾರಿ ಕೆಲಸಗಳು ಲಭ್ಯವೇ?\nಉತ್ತರ: ಹೌದು, GATE ಮೂಲಕ BHEL, NTPC, ISRO ನಲ್ಲಿ ಲಭ್ಯ.",
            category = "Engineering"
        ),
        CareerEntity(
            id = 6,
            title = "Civil Engineer & Infrastructure Lead",
            titleKn = "ಸಿವಿಲ್ ಎಂಜಿನಿಯರ್ (Civil Engineer)",
            image = "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b2?w=800",
            tagline = "Construct highways, skyscrapers, metro railways, and smart city bridges",
            taglineKn = "ಹೈವೇಗಳು, ಮೆಟ್ರೋ ರೈಲುಗಳು ಮತ್ತು ಸ್ಮಾರ್ಟ್ ಸಿಟಿ ಸೇತುವೆಗಳನ್ನು ನಿರ್ಮಿಸಿ",
            description = "Civil Engineers design, construct, supervise, and maintain major infrastructure projects such as bridges, dams, highways, airports, and green buildings.",
            descriptionKn = "ಸಿವಿಲ್ ಎಂಜಿನಿಯರ್‌ಗಳು ಕಟ್ಟಡಗಳು, ಸೇತುವೆಗಳು ಮತ್ತು ಹೆದ್ದಾರಿಗಳನ್ನು ನಿರ್ಮಿಸಿ ಉಸ್ತುವಾರಿ ವಹಿಸುತ್ತಾರೆ.",
            overview = "Nation-building career shaping urban skylines, metro connectivity, and mega infrastructure development.",
            overviewKn = "ದೇಶದ ಮೂಲಸೌಕರ್ಯ ಮತ್ತು ಕಟ್ಟಡಗಳ ನಿರ್ಮಾಣದ ಮುಖ್ಯ ವೃತ್ತಿ.",
            whyChoose = "Tangible satisfaction of building lasting landmarks, government IES service, and contracting business scope.",
            whyChooseKn = "ಶಾಶ್ವತ ನಿರ್ಮಾಣಗಳ ಸಂತೃಪ್ತಿ, ಸರ್ಕಾರಿ PWD / IES ಕೆಲಸಗಳು ಮತ್ತು ಸ್ವಂತ ಕಾಂಟ್ರಾಕ್ಟಿಂಗ್ ಉದ್ಯಮ.",
            benefits = "• Direct impact on national infrastructure development\n• High scope for starting independent construction firm\n• Prestigious Indian Engineering Services (IES) government roles",
            benefitsKn = "• ದೇಶದ ಮೂಲಸೌಕರ್ಯ ಅಭಿವೃದ್ಧಿಯಲ್ಲಿ ನೇರ ಪಾತ್ರ\n• ಸ್ವಂತ ಕನ್ಸ್ಟ್ರಕ್ಷನ್ ಕಂಪನಿ ಆರಂಭಿಸುವ ಅವಕಾಶ",
            eligibility = "12th Standard Science with PCM minimum 50% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCM) ಕನಿಷ್ಠ 50% ಅಂಕಗಳು.",
            subjects = "Structural Analysis, Geotechnical Engineering, Concrete Technology, AutoCAD, Construction Management",
            subjectsKn = "ಸ್ಟ್ರಾಕ್ಚರಲ್ ಎಂಜಿನಿಯರಿಂಗ್, ಕಾಂಕ್ರೀಟ್ ತಂತ್ರಜ್ಞಾನ, ಆಟೋಕ್ಯಾಡ್",
            exams = "JEE Main, KCET, GATE Civil, UPSC IES",
            examsKn = "JEE Main, KCET, GATE, UPSC IES",
            pathway = "Step 1: 10+2 PCM\nStep 2: B.Tech Civil Engineering (4 years)\nStep 3: Master AutoCAD / REVIT / STAAD Pro\nStep 4: Site Engineering internship & project management\nStep 5: UPSC IES / PWD / L&T recruitment",
            pathwayKn = "ಹಂತ 1: 10+2 PCM\nಹಂತ 2: 4 ವರ್ಷಗಳ B.Tech Civil\nಹಂತ 3: ಆಟೋಕ್ಯಾಡ್ ಕಲಿಯಿರಿ\nಹಂತ 4: PWD ಅಥವಾ L&T ಕಂಪನಿಗೆ ಸೇರಿಕೊಳ್ಳಿ",
            skills = "AutoCAD, REVIT, BIM, Site Supervision, Structural Calculation, Surveying",
            skillsKn = "AutoCAD, REVIT, ಸೈಟ್ ಮೇಲ್ವಿಚಾರಣೆ, ಸರ್ವೇಯಿಂಗ್",
            jobRoles = "Structural Designer, Site Engineer, Project Manager, Quantity Surveyor, PWD Executive Engineer",
            jobRolesKn = "ಸ್ಟ್ರಕ್ಚರಲ್ ಡಿಸೈನರ್, ಸೈಟ್ ಎಂಜಿನಿಯರ್, ಪ್ರಾಜೆಕ್ಟ್ ಮ್ಯಾನೇಜರ್",
            topColleges = "IIT Roorkee, IIT Bombay, NIT Surathkal, MSRIT Bangalore, BMSCE Bangalore",
            topCollegesKn = "IIT ರೂರ್ಕಿ, NIT ಸುರತ್ಕಲ್, BMSCE ಬೆಂಗಳೂರು",
            topRecruiters = "Larsen & Toubro (L&T), Tata Projects, Shapoorji Pallonji, NHAI, Metro Rail Corporations, AFCONS",
            topRecruitersKn = "L&T, ಟಾಟಾ ಪ್ರಾಜೆಕ್ಟ್ಸ್, NHAI, ಮೆಟ್ರೋ ರೈಲ್",
            salaryAverage = "₹ 5.5 - 11 Lakhs / Year",
            salaryHighest = "₹ 25+ Lakhs / Year",
            feesAverage = "₹ 1.2 - 3.5 Lakhs / Year",
            duration = "4 Years",
            scholarships = "L&T Build India Scholarship, Central Sector Scheme",
            scholarshipsKn = "L&T ಬಿಲ್ಡ್ ಇಂಡಿಯಾ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "Massive infrastructure investments in PM Gati Shakti, Bullet Trains, and Smart Cities.",
            futureScopeKn = "ಸ್ಮಾರ್ಟ್ ಸಿಟಿ ಯೋಜನೆಗಳು ಮತ್ತು ಮೆಟ್ರೋ ರೈಲು ಯೋಜನೆಗಳಿಂದ ಅಪಾರ ಅವಕಾಶಗಳು.",
            certifications = "AutoCAD Certified Professional, Project Management Professional (PMP), BIM Specialist",
            certificationsKn = "AutoCAD ಸರ್ಟಿಫಿಕೇಟ್, PMP ಪ್ರಾಜೆಕ್ಟ್ ಮ್ಯಾನೇಜ್‌ಮೆಂಟ್",
            faqs = "Q: What is L&T BIS Scholarship?\nA: Fully funded Master's sponsorship by L&T at IIT Madras/Delhi with guaranteed job.",
            faqsKn = "ಪ್ರಶ್ನೆ: L&T ಬಿಲ್ಡ್ ಇಂಡಿಯಾ ಸ್ಕಾಲರ್‌ಶಿಪ್ ಎಂದರೇನು?\nಉತ್ತರ: IIT ಗಳಲ್ಲಿ ಉಚಿತ M.Tech ಮತ್ತು L&T ನಲ್ಲಿ ನೇರ ಉದ್ಯೋಗ ನೀಡುತ್ತದೆ.",
            category = "Engineering"
        ),
        CareerEntity(
            id = 7,
            title = "Mechanical & Automotive Engineer",
            titleKn = "ಮೆಕ್ಯಾನಿಕಲ್ ಮತ್ತು ಆಟೋಮೊಬೈಲ್ ಎಂಜಿನಿಯರ್",
            image = "https://images.unsplash.com/photo-1537462715879-360eeb61a0ad?w=800",
            tagline = "Design automobiles, jet engines, robotics systems, and heavy industrial machinery",
            taglineKn = "ಆಟೋಮೊಬೈಲ್, ಜೆಟ್ ಎಂಜಿನ್ ಮತ್ತು ಕೈಗಾರಿಕಾ ಯಂತ್ರಗಳನ್ನು ರೂಪಿಸಿ",
            description = "Mechanical Engineers design and build mechanical components, automotive engines, aerospace structures, robotics systems, and HVAC systems.",
            descriptionKn = "ಯಂತ್ರಗಳು, ಎಂಜಿನ್‌ಗಳು ಮತ್ತು ರೋಬೋಟಿಕ್ಸ್ ವ್ಯವಸ್ಥೆಗಳನ್ನು ವಿನ್ಯಾಸಗೊಳಿಸುತ್ತಾರೆ.",
            overview = "The mother of engineering disciplines combining physics, materials science, thermodynamics, and manufacturing.",
            overviewKn = "ಎಂಜಿನಿಯರಿಂಗ್‌ನ ಮೂಲ ವಿಭಾಗ. ವಾಹನಗಳು ಮತ್ತು ಯಂತ್ರೋಪಕರಣಗಳ ತಯಾರಿಕೆ.",
            whyChoose = "Diverse hands-on engineering, scope in EV & aerospace, and high demand in manufacturing giants.",
            whyChooseKn = "ಆಟೋಮೊಬೈಲ್ ಮತ್ತು ಬಾಹ್ಯಾಕಾಶ ಕ್ಷೇತ್ರದಲ್ಲಿ ಉತ್ತಮ ಅವಕಾಶಗಳು.",
            benefits = "• Work with cars, aircraft, and robotics hardware\n• Versatile scope across automotive, aerospace, and energy\n• Global career scope in Germany, Japan, US, and India",
            benefitsKn = "• ಕಾರ್‌ಗಳು ಮತ್ತು ರಾಕೆಟ್‌ಗಳ ತಯಾರಿಕೆಯಲ್ಲಿ ಕೆಲಸ ಮಾಡಿ\n• ಜರ್ಮನಿ, ಜಪಾನ್‌ನಲ್ಲಿ ಉನ್ನತ ಬೇಡಿಕೆ",
            eligibility = "12th Standard Science with PCM minimum 50% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCM) ಕನಿಷ್ಠ 50% ಅಂಕಗಳು.",
            subjects = "Thermodynamics, Fluid Mechanics, CAD/CAM, Machine Design, Heat Transfer, Mechatronics",
            subjectsKn = "ಥರ್ಮೋಡೈನಾಮಿಕ್ಸ್, ಯಂತ್ರ ವಿನ್ಯಾಸ, CAD/CAM",
            exams = "JEE Main, KCET, COMEDK, GATE Mechanical",
            examsKn = "JEE Main, KCET, GATE",
            pathway = "Step 1: 10+2 PCM\nStep 2: B.Tech Mechanical Engineering (4 years)\nStep 3: Master SolidWorks / ANSYS simulation tools\nStep 4: Campus placement in automotive / manufacturing firms",
            pathwayKn = "ಹಂತ 1: 10+2 PCM\nಹಂತ 2: 4 ವರ್ಷಗಳ B.Tech Mechanical\nಹಂತ 3: SolidWorks ಕಲಿಯಿರಿ",
            skills = "SolidWorks, CATIA, ANSYS FEA, 3D Printing, Thermal Analysis, CNC Programming",
            skillsKn = "SolidWorks, CATIA, 3D ಪ್ರಿಂಟಿಂಗ್",
            jobRoles = "Automotive Engineer, CAD Designer, Plant Maintenance Lead, Aerodynamicist, R&D Manager",
            jobRolesKn = "ಆಟೋಮೋಟಿವ್ ಎಂಜಿನಿಯರ್, CAD ಡಿಸೈನರ್, R&D ಎಂಜಿನಿಯರ್",
            topColleges = "IIT Madras, IIT Kharagpur, NIT Trichy, RVCE Bangalore, PSG Tech Coimbatore",
            topCollegesKn = "IIT ಮದ್ರಾಸ್, NIT ತಿರಚಿ, RVCE ಬೆಂಗಳೂರು",
            topRecruiters = "Mahindra & Mahindra, Tata Motors, Mercedes-Benz, Bosch, General Electric, ISRO, Boeing",
            topRecruitersKn = "ಮಹೀಂದ್ರಾ, ಟಾಟಾ ಮೋಟಾರ್ಸ್, ಬಾಷ್, ಬೋಯಿಂಗ್",
            salaryAverage = "₹ 6 - 13 Lakhs / Year",
            salaryHighest = "₹ 30+ Lakhs / Year",
            feesAverage = "₹ 1.5 - 4 Lakhs / Year",
            duration = "4 Years",
            scholarships = "Siemens Scholarship, Post-Matric Merit Fellowships",
            scholarshipsKn = "ಸೀಮೆನ್ಸ್ ಎಂಜಿನಿಯರಿಂಗ್ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "High transformation into EV powertrains, 3D metal printing, and autonomous drone manufacturing.",
            futureScopeKn = "ಎಲೆಕ್ಟ್ರಿಕ್ ವಾಹನಗಳು ಮತ್ತು ಡ್ರೋನ್ ತಯಾರಿಕೆಯಲ್ಲಿ ಹೆಚ್ಚಿನ ಅವಕಾಶಗಳು.",
            certifications = "SolidWorks Certified Associate (CSWA), ANSYS CFD Simulation Diploma",
            certificationsKn = "SolidWorks ಸರ್ಟಿಫಿಕೇಟ್",
            faqs = "Q: Is Germany a good destination for Mechanical Engineers?\nA: Yes, Germany is the global hub for automotive mechanical engineering higher education.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಮೆಕ್ಯಾನಿಕಲ್ ಎಂಜಿನಿಯರಿಂಗ್‌ಗೆ ಜರ್ಮನಿ ಉತ್ತಮವೇ?\nಉತ್ತರ: ಹೌದು, ಜರ್ಮನಿಯು ಆಟೋಮೊಬೈಲ್ ಶಿಕ್ಷಣದ ಜಾಗತಿಕ ಕೇಂದ್ರವಾಗಿದೆ.",
            category = "Engineering"
        ),
        CareerEntity(
            id = 8,
            title = "Registered Nurse (B.Sc Nursing)",
            titleKn = "ನರ್ಸ್ / ಶೂಶ್ರೂಷಕರು (B.Sc Nursing)",
            image = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800",
            tagline = "Provide compassionate critical care, patient monitoring, and clinical nursing",
            taglineKn = "ಆಸ್ಪತ್ರೆಗಳಲ್ಲಿ ರೋಗಿಗಳ ಆರೈಕೆ ಮತ್ತು ಆರೋಗ್ಯ ಸೇವೆ ನೀಡಿ",
            description = "Registered Nurses administer patient treatments, assist surgical teams, manage ICU life-support equipment, and coordinate hospital care.",
            descriptionKn = "ನರ್ಸ್‌ಗಳು ರೋಗಿಗಳಿಗೆ ಚಿಕಿತ್ಸೆ ನೀಡುತ್ತಾರೆ ಮತ್ತು ಆಸ್ಪತ್ರೆಯ ಪಾಲನೆ ವಹಿಸುತ್ತಾರೆ.",
            overview = "Essential healthcare pillar with massive international job opportunities in NHS UK, USA, Gulf, and Canada.",
            overviewKn = "ಆರೋಗ್ಯ ಕ್ಷೇತ್ರದ ಪ್ರಮುಖ ತಳಹದಿ. ಜಾಗತಿಕವಾಗಿ ಅಪಾರ ಬೇಡಿಕೆ ಇರುವ ವೃತ್ತಿ.",
            whyChoose = "Immediate 100% placement, opportunity to work abroad (UK/USA/Gulf), and humanitarian job satisfaction.",
            whyChooseKn = "100% ಉದ್ಯೋಗ ಖಾತರಿ, ವಿದೇಶಗಳಲ್ಲಿ (UK, US, ದುಬೈ) ಕೆಲಸದ ಅವಕಾಶ.",
            benefits = "• Massive overseas recruitment with direct green card / PR pathways\n• 100% employment guarantee upon graduation\n• Respectable role saving patient lives daily",
            benefitsKn = "• UK/USA ಗೆ ಸುಲಭ ವೀಸಾ ಮತ್ತು ಉದ್ಯೋಗ ಅವಕಾಶಗಳು\n• 100% ಉದ್ಯೋಗ ಖಾತರಿ",
            eligibility = "12th Standard Science with PCB (Physics, Chemistry, Biology) minimum 45% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCB) ಕನಿಷ್ಠ 45% ಅಂಕಗಳು.",
            subjects = "Anatomy, Physiology, Medical-Surgical Nursing, Pharmacology, Critical Care, Pediatric Nursing",
            subjectsKn = "ಅನಾಟಮಿ, ಫಾರ್ಮಾಕಾಲಜಿ, ಮೆಡಿಕಲ್ ನರ್ಸಿಂಗ್",
            exams = "State Nursing Entrance Exams, AIIMS Nursing Test, NCLEX-RN (USA), OET / IELTS",
            examsKn = "AIIMS ನರ್ಸಿಂಗ್ ಪರೀಕ್ಷೆ, NCLEX-RN, OET",
            pathway = "Step 1: 10+2 PCB Science\nStep 2: 4 Years B.Sc Nursing Degree\nStep 3: Register with State Nursing Council\nStep 4: Clinical practice in hospitals\nStep 5: Pass OET/NCLEX for UK/USA/Gulf migration",
            pathwayKn = "ಹಂತ 1: 10+2 PCB ವಿಜ್ಞಾನ\nಹಂತ 2: 4 ವರ್ಷಗಳ B.Sc Nursing\nಹಂತ 3: ನರ್ಸಿಂಗ್ ಕೌನ್ಸಿಲ್ ನೋಂದಣಿ\nಹಂತ 4: ವಿದೇಶಿ ಪರೀಕ್ಷೆಗಳು (NCLEX/OET)",
            skills = "Patient Assessment, IV Medication Administration, ICU Equipment Operation, Empathy, Emergency Life Support",
            skillsKn = "ರೋಗಿ ಆರೈಕೆ, ಐಸಿಯು ಕಾರ್ಯಾಚರಣೆ, ಕರುಣೆ",
            jobRoles = "Staff Nurse, Critical Care ICU Nurse, Operation Theatre Nurse, Nurse Educator, Nursing Superintendent",
            jobRolesKn = "ಸ್ಟಾಫ್ ನರ್ಸ್, ಐಸಿಯು ನರ್ಸ್, ನರ್ಸಿಂಗ್ ಸೂಪರಿಂಟೆಂಡೆಂಟ್",
            topColleges = "AIIMS Delhi, CMC Vellore, St. John's College of Nursing Bangalore, Manipal College of Nursing",
            topCollegesKn = "AIIMS, CMC ವೆಲ್ಲೂರು, ಸೇಂಟ್ ಜಾನ್ಸ್ ನರ್ಸಿಂಗ್ ಬೆಂಗಳೂರು, ಮಣಿಪಾಲ್",
            topRecruiters = "Apollo Hospitals, Fortis, Manipal Hospitals, NHS United Kingdom, Cleveland Clinic Abu Dhabi",
            topRecruitersKn = "ಅಪೋಲೋ ಆಸ್ಪತ್ರೆಗಳು, UK ನರ್ಸಿಂಗ್ ಸೇವೆಗಳು, ಮಣಿಪಾಲ್",
            salaryAverage = "₹ 4 - 8 Lakhs / Year (India) | ₹ 30 - 50 Lakhs / Year (UK / USA)",
            salaryHighest = "₹ 60+ Lakhs / Year (Abroad)",
            feesAverage = "₹ 80,000 - 2 Lakhs / Year",
            duration = "4 Years",
            scholarships = "Pragatai Nursing Scholarship, Post-Matric Minority Grants",
            scholarshipsKn = "ಪ್ರಗತಿ ನರ್ಸಿಂಗ್ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "Unprecedented global demand due to nurse shortage in European countries and expanding hospital chains.",
            futureScopeKn = "ಯುರೋಪ್ ಮತ್ತು ಅಮೆರಿಕದಲ್ಲಿ ನರ್ಸ್‌ಗಳಿಗೆ ಭಾರೀ ಅಭಾವವಿರುವುದರಿಂದ ಅಪಾರ ಅವಕಾಶ.",
            certifications = "Registered Nurse Certificate (RN/RM), BLS/ACLS Certification, NCLEX-RN USA",
            certificationsKn = "RN/RM ನೋಂದಣಿ, NCLEX-RN ಲೈಸೆನ್ಸ್",
            faqs = "Q: What is NCLEX-RN?\nA: The national exam required to practice as a Licensed Registered Nurse in the USA and Canada.",
            faqsKn = "ಪ್ರಶ್ನೆ: NCLEX-RN ಎಂದರೇನು?\nಉತ್ತರ: ಅಮೆರಿಕ ಮತ್ತು ಕೆನಡಾದಲ್ಲಿ ನರ್ಸ್ ಆಗಿ ಕೆಲಸ ಮಾಡಲು ಅಗತ್ಯವಿರುವ ಪರೀಕ್ಷೆ.",
            category = "Medical"
        ),
        CareerEntity(
            id = 9,
            title = "Pharmacist (B.Pharm / Pharm.D)",
            titleKn = "ಫಾರ್ಮಸಿಸ್ಟ್ (B.Pharm / Pharm.D)",
            image = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=800",
            tagline = "Formulate life-saving drugs, manage retail pharmacies, and direct pharmaceutical R&D",
            taglineKn = "ಔಷಧಿಗಳನ್ನು ತಯಾರಿಸಿ, ಪರೀಕ್ಷಿಸಿ ಮತ್ತು ವೈದ್ಯಕೀಯ ಶಾಪ್ ನಡೆಸಿ",
            description = "Pharmacists research drug chemical formulations, conduct clinical trials, inspect quality control, and dispense prescription medications.",
            descriptionKn = "ಫಾರ್ಮಸಿಸ್ಟ್‌ಗಳು ಔಷಧಿಗಳ ತಯಾರಿಕೆ, ಗುಣಮಟ್ಟ ಪರಿಶೀಲನೆ ಮತ್ತು ವಿತರಣೆ ಮಾಡುತ್ತಾರೆ.",
            overview = "Dynamic pharmaceutical science branch bridging medicine and chemical sciences.",
            overviewKn = "ಔಷಧ ತಯಾರಿಕೆ ಮತ್ತು ವಿತರಣೆಯ ಮುಖ್ಯ ವಿಭಾಗ.",
            whyChoose = "Ability to own drug store business, steady pharma company jobs, and drug inspector public sector career.",
            whyChooseKn = "ಸ್ವಂತ ಮೆಡಿಕಲ್ ಶಾಪ್ ಉದ್ಯಮ, ಡ್ರಗ್ ಇನ್ಸ್ಪೆಕ್ಟರ್ ಸರ್ಕಾರಿ ಕೆಲಸ.",
            benefits = "• License to open independent retail/wholesale pharmacy\n• Career in top pharmaceutical research & manufacturing\n• Government Drug Inspector public officer status",
            benefitsKn = "• ಲೈಸೆನ್ಸ್ ಪಡೆದು ಸ್ವಂತ ಮೆಡಿಕಲ್ ಸ್ಟೋರ್ ತೆರೆಯಿರಿ\n• ಡ್ರಗ್ ಇನ್ಸ್ಪೆಕ್ಟರ್ ಆಗುವ ಅವಕಾಶ",
            eligibility = "12th Standard Science with PCB/PCM minimum 50% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCB/PCM) ಕನಿಷ್ಠ 50% ಅಂಕಗಳು.",
            subjects = "Pharmaceutics, Pharmacology, Pharmaceutical Chemistry, Pharmacognosy, Clinical Pharmacy, Hospital Pharmacy",
            subjectsKn = "ಫಾರ್ಮಾಸ್ಯುಟಿಕ್ಸ್, ಫಾರ್ಮಾಕಾಲಜಿ, ಕೆಮಿಸ್ಟ್ರಿ",
            exams = "KCET, GPAT (Graduate Pharmacy Aptitude Test), NIPER JEE",
            examsKn = "KCET, GPAT, NIPER JEE",
            pathway = "Step 1: 10+2 PCB/PCM\nStep 2: 4 Years B.Pharm or 6 Years Pharm.D\nStep 3: Register with Pharmacy Council of India (PCI)\nStep 4: Join Pharma R&D or open private pharmacy business",
            pathwayKn = "ಹಂತ 1: 10+2 PCB/PCM\nಹಂತ 2: 4 ವರ್ಷಗಳ B.Pharm\nಹಂತ 3: ಫಾರ್ಮಸಿ ಕೌನ್ಸಿಲ್ ನೋಂದಣಿ",
            skills = "Chemical Analysis, Drug Safety Monitoring, Quality Control, Regulatory Compliance, Retail Management",
            skillsKn = "ರಾಸಾಯನಿಕ ವಿಶ್ಲೇಷಣೆ, ಔಷಧಿ ಸುರಕ್ಷತೆ, ಮೆಡಿಕಲ್ ಶಾಪ್ ಆಡಳಿತ",
            jobRoles = "Drug Inspector, Pharmacist, Clinical Research Associate, Quality Control Manager, Regulatory Affairs Lead",
            jobRolesKn = "ಡ್ರಗ್ ಇನ್ಸ್ಪೆಕ್ಟರ್, ಫಾರ್ಮಸಿಸ್ಟ್, R&D ಸೈಂಟಿಸ್ಟ್",
            topColleges = "NIPER Mohali, Jamia Hamdard, Manipal College of Pharmaceutical Sciences, JSS College of Pharmacy Mysore",
            topCollegesKn = "NIPER, ಮಣಿಪಾಲ್ ಫಾರ್ಮಸಿ, JSS ಫಾರ್ಮಸಿ ಮೈಸೂರು",
            topRecruiters = "Sun Pharma, Cipla, Dr. Reddy's Laboratories, Lupin, Pfizer, Apollo Pharmacy, Biocon",
            topRecruitersKn = "ಸಿಪ್ಲಾ, ಡಾ. ರೆಡ್ಡೀಸ್, ಸನ್ ಫಾರ್ಮಾ, ಬಯೋಕಾನ್, ಅಪೋಲೋ ಫಾರ್ಮಸಿ",
            salaryAverage = "₹ 5 - 10 Lakhs / Year",
            salaryHighest = "₹ 24+ Lakhs / Year",
            feesAverage = "₹ 1 - 3 Lakhs / Year",
            duration = "4 Years (B.Pharm) / 6 Years (Pharm.D)",
            scholarships = "GPAT AICTE Scholarship (₹12,400/month), Post-Matric Grants",
            scholarshipsKn = "GPAT ಸ್ಕಾಲರ್‌ಶಿಪ್ (ತಿಂಗಳಿಗೆ ₹12,400)",
            futureScope = "Booming demand as India is known as the 'Pharmacy of the World' supplying global vaccines and generic drugs.",
            futureScopeKn = "ಭಾರತವು ಜಾಗತಿಕ ಔಷಧ ತಯಾರಿಕೆಯ ಕೇಂದ್ರವಾಗಿರುವುದರಿಂದ ಭವಿಷ್ಯದಲ್ಲಿ ಉತ್ತಮ ಬೇಡಿಕೆ.",
            certifications = "PCI Registered Pharmacist Certificate, Pharmacovigilance Professional Certification",
            certificationsKn = "PCI ಫಾರ್ಮಸಿಸ್ಟ್ ನೋಂದಣಿ ಪ್ರಮಾಣಪತ್ರ",
            faqs = "Q: Can I open a medical shop with B.Pharm?\nA: Yes, B.Pharm graduates get a legally valid Drug License from the Pharmacy Council.",
            faqsKn = "ಪ್ರಶ್ನೆ: B.Pharm ನಂತರ ಮೆಡಿಕಲ್ ಶಾಪ್ ತೆರೆಯಬಹುದೇ?\nಉತ್ತರ: ಹೌದು, B.Pharm ಹೊಂದಿದವರಿಗೆ ಸರ್ಕಾರಿ ಡ್ರಗ್ ಲೈಸೆನ್ಸ್ ಸಿಗುತ್ತದೆ.",
            category = "Medical"
        ),
        CareerEntity(
            id = 10,
            title = "Dentist (BDS / MDS)",
            titleKn = "ದಂತ ವೈದ್ಯರು (Dentist / BDS)",
            image = "https://images.unsplash.com/photo-1606811841689-23dfddce3e95?w=800",
            tagline = "Treat oral diseases, perform dental surgeries, and design cosmetic smiles",
            taglineKn = "ಹಲ್ಲುಗಳ ಚಿಕಿತ್ಸೆ ಮತ್ತು ಕಾಸ್ಮೆಟಿಕ್ ನಗೆಯ ವಿನ್ಯಾಸ ಮಾಡಿ",
            description = "Dentists diagnose and treat oral conditions, perform root canals, align teeth with braces, craft dentures, and perform oral surgery.",
            descriptionKn = "ದಂತ ವೈದ್ಯರು ಹಲ್ಲಿನ ನೋವು, ದಂತ ಶಸ್ತ್ರಚಿಕಿತ್ಸೆ ಮತ್ತು ರೂಟ್ ಕೆನಾಲ್ ಚಿಕಿತ್ಸೆ ನೀಡುತ್ತಾರೆ.",
            overview = "Specialized medical branch focusing on dental healthcare, facial cosmetics, and oral pathology.",
            overviewKn = "ಹಲ್ಲು ಮತ್ತು ಮುಖದ ಸೌಂದರ್ಯಕ್ಕೆ ಸಂಬಂಧಿಸಿದ ವೈದ್ಯಕೀಯ ಶಾಖೆ.",
            whyChoose = "Independent clinic ownership, comfortable regular work hours (no midnight shifts), and high cosmetic revenue.",
            whyChooseKn = "ಸ್ವಂತ ಡೆಂಟಲ್ ಕ್ಲಿನಿಕ್ ಸ್ಥಾಪನೆ, ರಾತ್ರಿ ಶಿಫ್ಟ್‌ಗಳಿಲ್ಲದ ಆರಾಮದಾಯಕ ವೃತ್ತಿ.",
            benefits = "• Private dental clinic business ownership\n• No emergency night duty shifts compared to general medicine\n• High earning scope in cosmetic dentistry & aligners",
            benefitsKn = "• ಸ್ವಂತ ಡೆಂಟಲ್ ಕ್ಲಿನಿಕ್ ಉದ್ಯಮ\n• ರಾತ್ರಿ ಡ್ಯೂಟಿ ತೊಂದರೆ ಇಲ್ಲ",
            eligibility = "12th Standard Science with PCB minimum 50% + NEET UG Qualified.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCB) ಕನಿಷ್ಠ 50% + NEET UG ಉತ್ತೀರ್ಣ.",
            subjects = "Oral Anatomy, Oral Pathology, Prosthodontics, Orthodontics, Endodontics, Oral Surgery",
            subjectsKn = "ಆರಲ್ ಅನಾಟಮಿ, ಆರ್ಥೋಡಾಂಟಿಕ್ಸ್, ಎಂಡೋಡಾಂಟಿಕ್ಸ್",
            exams = "NEET UG, NEET MDS (Postgraduate)",
            examsKn = "NEET UG, NEET MDS",
            pathway = "Step 1: 10+2 PCB Science\nStep 2: Clear NEET UG exam\nStep 3: 5 Years BDS Degree (4 yrs study + 1 yr internship)\nStep 4: Register with Dental Council of India (DCI)\nStep 5: Open private dental clinic or pursue MDS (3 years)",
            pathwayKn = "ಹಂತ 1: 10+2 PCB ವಿಜ್ಞಾನ\nಹಂತ 2: NEET UG ಪರೀಕ್ಷೆ\nಹಂತ 3: 5 ವರ್ಷಗಳ BDS ಪದವಿ\nಹಂತ 4: ಸ್ವಂತ ಡೆಂಟಲ್ ಕ್ಲಿನಿಕ್ ಆರಂಭಿಸಿ",
            skills = "Fine Motor Control, Oral Surgery, Patient Comfort, Cosmetic Aesthetics, Radiography Analysis",
            skillsKn = "ದಂತ ಶಸ್ತ್ರಚಿಕಿತ್ಸೆ, ನಿಖರತೆ, ರೋಗಿ ಸಮಾಧಾನ ಪಡಿಸುವುದು",
            jobRoles = "Dental Surgeon, Orthodontist, Periodontist, Cosmetic Dentist, Dental Officer",
            jobRolesKn = "ಡೆಂಟಲ್ ಸರ್ಜನ್, ಆರ್ಥೋಡಾಂಟಿಸ್ಟ್, ಕಾಸ್ಮೆಟಿಕ್ ಡೆಂಟಿಸ್ಟ್",
            topColleges = "Manipal College of Dental Sciences, Maulana Azad Institute of Dental Sciences Delhi, AB Shetty Dental Mangalore",
            topCollegesKn = "ಮಣಿಪಾಲ್ ಡೆಂಟಲ್ ಕಾಲೇಜು, ಮೌಲಾನಾ ಆಜಾದ್ ದೆಹಲಿ, AB ಶೆಟ್ಟಿ ಮಂಗಳೂರು",
            topRecruiters = "Clove Dental, Sabka Dentist, Apollo White Dental, Army Dental Corps, Own Private Dental Practice",
            topRecruitersKn = "ಕ್ಲೋವ್ ಡೆಂಟಲ್, ಅಪೋಲೋ ವೈಟ್ ಡೆಂಟಲ್, ಸ್ವಂತ ಕ್ಲಿನಿಕ್",
            salaryAverage = "₹ 6 - 12 Lakhs / Year",
            salaryHighest = "₹ 30+ Lakhs / Year",
            feesAverage = "₹ 2 - 5 Lakhs / Year",
            duration = "5 Years (BDS) + 3 Years (MDS)",
            scholarships = "DCI Merit Scholarships, State Post-Matric Fellowships",
            scholarshipsKn = "ರಾಜ್ಯ ಸರ್ಕಾರಿ ವೈದ್ಯಕೀಯ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "Rapid growth in cosmetic smile design, invisible aligners, and dental implantology.",
            futureScopeKn = "ಕಾಸ್ಮೆಟಿಕ್ ಡೆಂಟಿಸ್ಟ್ರಿ ಮತ್ತು ಅಲೈನರ್ಸ್ ತಂತ್ರಜ್ಞಾನದಿಂದ ಹೆಚ್ಚುತ್ತಿರುವ ಬೇಡಿಕೆ.",
            certifications = "DCI Registration Certificate, Certificate in Dental Implantology / Clear Aligners",
            certificationsKn = "ಡೆಂಟಲ್ ಕೌನ್ಸಿಲ್ ನೋಂದಣಿ ಪ್ರಮಾಣಪತ್ರ",
            faqs = "Q: Is it profitable to start a private dental clinic in India?\nA: Yes, a well-located private dental clinic yields high profit margins with loyal patients.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಸ್ವಂತ ಡೆಂಟಲ್ ಕ್ಲಿನಿಕ್ ಸ್ಥಾಪಿಸುವುದು ಲಾಭದಾಯಕವೇ?\nಉತ್ತರ: ಹೌದು, ಉತ್ತಮ ಸ್ಥಳದಲ್ಲಿದ್ದರೆ ದೀರ್ಘಕಾಲಿಕ ಹೆಚ್ಚಿನ ಲಾಭ ನೀಡುತ್ತದೆ.",
            category = "Medical"
        ),
        CareerEntity(
            id = 11,
            title = "Architect & Smart City Designer",
            titleKn = "ಆರ್ಕಿಟೆಕ್ಟ್ ಮತ್ತು ಕಟ್ಟಡ ವಿನ್ಯಾಸಕ (Architect)",
            image = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800",
            tagline = "Design iconic building structures, eco-friendly homes, and urban master plans",
            taglineKn = "ಐಕಾನಿಕ್ ಕಟ್ಟಡಗಳು ಮತ್ತು ಪರಿಸರ ಸ್ನೇಹಿ ಮನೆಗಳನ್ನು ವಿನ್ಯಾಸಗೊಳಿಸಿ",
            description = "Architects conceptualize building aesthetics, space layout plans, green building standards, structural 3D modeling, and urban master plans.",
            descriptionKn = "ಆರ್ಕಿಟೆಕ್ಟ್‌ಗಳು ಕಟ್ಟಡಗಳ ಸೌಂದರ್ಯ, ಬ್ಲೂಪ್ರಿಂಟ್ ಮತ್ತು 3D ಮಾದರಿಗಳನ್ನು ರೂಪಿಸುತ್ತಾರೆ.",
            overview = "A creative blend of artistic design, structural engineering, environmental science, and spatial functionality.",
            overviewKn = "ಕಲೆ ಮತ್ತು ಎಂಜಿನಿಯರಿಂಗ್‌ನ ಸೃಜನಶೀಲ ಮಿಶ್ರಣ.",
            whyChoose = "Creative artistic satisfaction, independent firm ownership, and designing structural monuments.",
            whyChooseKn = "ಸೃಜನಶೀಲ ವಿನ್ಯಾಸದ ಆನಂದ, ಸ್ವಂತ ಆರ್ಕಿಟೆಕ್ಚರ್ ಫರ್ಮ್ ಸ್ಥಾಪನೆ.",
            benefits = "• Creative artistic expression in real-world structures\n• Scope to start independent Architectural Design Studio\n• High demand in luxury interior & real estate development",
            benefitsKn = "• ಸೃಜನಶೀಲ ವಿನ್ಯಾಸದ ಸ್ವಾತಂತ್ರ್ಯ\n• ಸ್ವಂತ ಆರ್ಕಿಟೆಕ್ಚರ್ ಸ್ಟುಡಿಯೋ ಆರಂಭಿಸಬಹುದು",
            eligibility = "12th Standard Science with PCM minimum 50% + NATA Entrance Qualified.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCM) ಕನಿಷ್ಠ 50% + NATA ಪರೀಕ್ಷೆ ಉತ್ತೀರ್ಣ.",
            subjects = "Architectural Design, Building Materials, Structural Mechanics, AutoCAD, 3D SketchUp, Climate Responsive Design",
            subjectsKn = "ಆರ್ಕಿಟೆಕ್ಚರಲ್ ಡಿಸೈನ್, 3D ಮಾದರಿ, ಆಟೋಕ್ಯಾಡ್",
            exams = "NATA (National Aptitude Test in Architecture), JEE Main Paper 2 (B.Arch)",
            examsKn = "NATA, JEE Main Paper 2",
            pathway = "Step 1: 10+2 PCM Science\nStep 2: Clear NATA or JEE Main Paper 2\nStep 3: 5 Years B.Arch Degree\nStep 4: Register with Council of Architecture (COA)\nStep 5: Join architectural firm or launch private practice",
            pathwayKn = "ಹಂತ 1: 10+2 PCM\nಹಂತ 2: NATA ಪರೀಕ್ಷೆ ಉತ್ತೀರ್ಣ\nಹಂತ 3: 5 ವರ್ಷಗಳ B.Arch\nಹಂತ 4: ಕೌನ್ಸಿಲ್ ಆಫ್ ಆರ್ಕಿಟೆಕ್ಚರ್ ನೋಂದಣಿ",
            skills = "Architectural Drawing, AutoCAD, Revit, 3D Rendering (V-Ray/Lumion), Spatial Planning, Client Communication",
            skillsKn = "ಚಿತ್ರಕಲೆ ಕೌಶಲ್ಯ, 3D ಸಾಫ್ಟ್‌ವೇರ್, ಸೃಜನಶೀಲತೆ",
            jobRoles = "Project Architect, Urban Planner, Interior Designer, Landscape Architect, BIM Designer",
            jobRolesKn = "ಪ್ರಾಜೆಕ್ಟ್ ಆರ್ಕಿಟೆಕ್ಟ್, ಅರ್ಬನ್ ಪ್ಲಾನರ್, ಇಂಟೀರಿಯರ್ ಡಿಸೈನರ್",
            topColleges = "School of Planning and Architecture (SPA) Delhi, CEPT University Ahmedabad, IIT Roorkee, BMS College of Architecture Bangalore",
            topCollegesKn = "SPA ನವದೆಹಲಿ, CEPT ಅಹಮದಾಬಾದ್, BMS ಆರ್ಕಿಟೆಕ್ಚರ್ ಬೆಂಗಳೂರು",
            topRecruiters = "Hafeez Contractor, Morphogenesis, DLF, Sobha Developers, Prestige Group, RSP Architects",
            topRecruitersKn = "ಹಫೀಜ್ ಕಾಂಟ್ರಾಕ್ಟರ್, ಪ್ರೆಸ್ಟೀಜ್ ಗ್ರೂಪ್, ಶೋಭಾ ಡೆವಲಪರ್ಸ್",
            salaryAverage = "₹ 6 - 14 Lakhs / Year",
            salaryHighest = "₹ 35+ Lakhs / Year",
            feesAverage = "₹ 1.5 - 4 Lakhs / Year",
            duration = "5 Years",
            scholarships = "COA Merit Scholarship, Central Heritage Grants",
            scholarshipsKn = "COA ಮೆರಿಟ್ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "High growth driven by green buildings, net-zero carbon architecture, and smart cities.",
            futureScopeKn = "ಪರಿಸರ ಸ್ನೇಹಿ ಹಸಿರು ಕಟ್ಟಡಗಳು ಮತ್ತು ಸ್ಮಾರ್ಟ್ ಸಿಟಿಗಳಿಂದ ಪ್ರಕಾಶಮಾನ ಭವಿಷ್ಯ.",
            certifications = "COA Architect Registration License, IGBC Accredited Green Building Professional",
            certificationsKn = "COA ನೋಂದಣಿ ಲೈಸೆನ್ಸ್, IGBC ಗ್ರೀನ್ ಬಿಲ್ಡಿಂಗ್ ಸರ್ಟಿಫಿಕೇಟ್",
            faqs = "Q: Is drawing skill compulsory for NATA?\nA: Yes, basic hand sketching and spatial perspective sense are evaluated in NATA.",
            faqsKn = "ಪ್ರಶ್ನೆ: NATA ಗೆ ಚಿತ್ರಕಲೆ ಕೌಶಲ್ಯ ಅಗತ್ಯವೇ?\nಉತ್ತರ: ಹೌದು, ಮೂಲಭೂತ ಡ್ರಾಯಿಂಗ್ ಕೌಶಲ್ಯ ಅಗತ್ಯವಿದೆ.",
            category = "Design"
        ),
        CareerEntity(
            id = 12,
            title = "Chartered Accountant (CA)",
            titleKn = "ಚಾರ್ಟರ್ಡ್ ಅಕೌಂಟೆಂಟ್ (CA)",
            image = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=800",
            tagline = "Direct corporate audit, taxation strategy, GST compliance, and financial management",
            taglineKn = "ಕಾರ್ಪೊರೇಟ್ ಆಡಿಟಿಂಗ್, ತೆರಿಗೆ ತಂತ್ರ ಮತ್ತು ಹಣಕಾಸು ನಿರ್ವಹಣೆ ಮಾಡಿ",
            description = "Chartered Accountants audit company balance sheets, optimize corporate tax strategy, manage GST compliance, and provide financial advisory.",
            descriptionKn = "ಸಿಎಗಳು ಕಂಪನಿಗಳ ಲೆಕ್ಕಪತ್ರ ಪರೀಕ್ಷೆ, ತೆರಿಗೆ ಉಳಿತಾಯ ಮತ್ತು ಜಿಎಸ್ಟಿ ಸಲ್ಲಿಕೆ ಮಾಡುತ್ತಾರೆ.",
            overview = "The premier finance qualification in India conferring statutory auditing authority.",
            overviewKn = "ಭಾರತದ ಅತ್ಯಂತ ಉನ್ನತ ಹಣಕಾಸು ಮತ್ತು ಲೆಕ್ಕಪತ್ರ ವೃತ್ತಿ.",
            whyChoose = "Highest trust in business world, statutory signature power, independent audit practice, and lucrative Big 4 packages.",
            whyChooseKn = "ಕಾನೂನುಬದ್ಧ ಆಡಿಟಿಂಗ್ ಅಧಿಕಾರ, ಅತ್ಯುನ್ನತ ವೇತನ ಮತ್ತು ಸ್ವತಂತ್ರ ಕಚೇರಿ ಸ್ಥಾಪನೆ.",
            benefits = "• Exclusive legal authority to audit Indian company balance sheets\n• High financial rewards in Big 4 accounting firms\n• Freedom to start independent CA Audit Firm",
            benefitsKn = "• ಕಂಪನಿ ಬ್ಯಾನೆನ್ಸ್ ಶೀಟ್ ಆಡಿಟ್ ಮಾಡುವ ಕಾನೂನು ಅಧಿಕಾರ\n• ಬಿಗ್ 4 ಕಂಪನಿಗಳಲ್ಲಿ ಕೆಲಸ ಅಥವಾ ಸ್ವಂತ ಫರ್ಮ್",
            eligibility = "12th Standard Any Stream (Commerce preferred) minimum 50% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (ಯಾವುದೇ ವಿಭಾಗ, ಕಾಮರ್ಸ್ ಉತ್ತಮ) ಕನಿಷ್ಠ 50%.",
            subjects = "Financial Accounting, Corporate Law, Direct Taxation, Indirect Tax (GST), Auditing, Strategic Cost Management",
            subjectsKn = "ಲೆಕ್ಕಪತ್ರ ನಿರ್ವಹಣೆ, ತೆರಿಗೆ ಕಾನೂನು, ಜಿಎಸ್ಟಿ, ಆಡಿಟಿಂಗ್",
            exams = "ICAI Foundation, ICAI Intermediate, ICAI Final Exam",
            examsKn = "ICAI ಫೌಂಡೇಶನ್, ಇಂಟರ್‌ಮೀಡಿಯೇಟ್, ಫೈನಲ್",
            pathway = "Step 1: Pass 10+2 Exams\nStep 2: Clear CA Foundation Exam\nStep 3: Clear CA Intermediate Group 1 & 2\nStep 4: Complete 2 Years Articleship Training under Practicing CA\nStep 5: Pass CA Final Exam & Become ICAI Member",
            pathwayKn = "ಹಂತ 1: 10+2 ಕಾಮರ್ಸ್\nಹಂತ 2: CA ಫೌಂಡೇಶನ್ ಪರೀಕ್ಷೆ\nಹಂತ 3: CA ಇಂಟರ್‌ಮೀಡಿಯೇಟ್ + 2 ವರ್ಷಗಳ ಆರ್ಟಿಕಲ್‌ಶಿಪ್\nಹಂತ 4: CA ಫೈನಲ್ ಉತ್ತೀರ್ಣರಾಗಿ",
            skills = "Financial Auditing, GST Compliance, Corporate Taxation, Financial Modeling, Analytical Precision",
            skillsKn = "ಆಡಿಟಿಂಗ್, ಜಿಎಸ್‌ಟಿ, ತೆರಿಗೆ ನಿರ್ವಹಣೆ, ಫೈನಾನ್ಷಿಯಲ್ ಅನಾಲಿಸಿಸ್",
            jobRoles = "Chartered Accountant, Statutory Auditor, CFO, Chief Tax Consultant, Forensic Auditor",
            jobRolesKn = "ಚಾರ್ಟರ್ಡ್ ಅಕೌಂಟೆಂಟ್, ಸಿಎಫ್‌ಒ (CFO), ಟ್ಯಾಕ್ಸ್ ಕನ್ಸಲ್ಟೆಂಟ್",
            topColleges = "Institute of Chartered Accountants of India (ICAI - Statutory Body)",
            topCollegesKn = "ಇನ್‌ಸ್ಟಿಟ್ಯೂಟ್ ಆಫ್ ಚಾರ್ಟರ್ಡ್ ಅಕೌಂಟೆಂಟ್ಸ್ ಆಫ್ ಇಂಡಿಯಾ (ICAI)",
            topRecruiters = "Deloitte, PwC, EY (Ernst & Young), KPMG, Reliance Industries, HDFC Bank, Tata Consultancy",
            topRecruitersKn = "ಡೆಲೋಯಿಟ್, PwC, EY, KPMG, ಎಚ್‌ಡಿಎಫ್‌ಸಿ ಬ್ಯಾಂಕ್",
            salaryAverage = "₹ 9 - 18 Lakhs / Year",
            salaryHighest = "₹ 40+ Lakhs / Year",
            feesAverage = "₹ 50,000 - 1.5 Lakhs (Total Exam & Coaching Cost)",
            duration = "4 to 5 Years",
            scholarships = "ICAI Merit Scholarships, EWS Financial Assistance",
            scholarshipsKn = "ICAI ಮೆರಿಟ್ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "Evergreen demand because every registered company in India must legally undergo annual CA Audit.",
            futureScopeKn = "ಎಲ್ಲಾ ಕಂಪನಿಗಳಿಗೆ ಆಡಿಟಿಂಗ್ ಕಡ್ಡಾಯವಾಗಿರುವುದರಿಂದ ಸದಾ ಉನ್ನತ ಬೇಡಿಕೆ.",
            certifications = "ICAI Membership Certificate, Certificate of Practice (COP)",
            certificationsKn = "ICAI ಸದಸ್ಯತ್ವ ಪ್ರಮಾಣಪತ್ರ (COP)",
            faqs = "Q: Can Science students pursue CA?\nA: Yes! Anyone after 12th science can clear CA Foundation with dedication.",
            faqsKn = "ಪ್ರಶ್ನೆ: ವಿಜ್ಞಾನ ವಿದ್ಯಾರ್ಥಿಗಳು CA ಆಗಬಹುದೇ?\nಉತ್ತರ: ಹೌದು, 12ನೇ ವಿಜ್ಞಾನ ನಂತರ CA ಫೌಂಡೇಶನ್ ಬರೆಯಬಹುದು.",
            category = "Commerce"
        ),
        CareerEntity(
            id = 13,
            title = "Data Scientist & Big Data AI Lead",
            titleKn = "ಡೇಟಾ ಸೈಂಟಿಸ್ಟ್ (Data Scientist)",
            image = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800",
            tagline = "Extract strategic insights from massive data, machine learning, and predictive models",
            taglineKn = "ದೊಡ್ಡ ಡೇಟಾದಿಂದ ಉಪಯುಕ್ತ ಮಾಹಿತಿಯನ್ನು ಹೊರತೆಗೆಯಿರಿ",
            description = "Data Scientists analyze complex datasets using machine learning algorithms, statistical tools, and Python to drive business intelligence decisions.",
            descriptionKn = "ಡೇಟಾ ಸೈಂಟಿಸ್ಟ್‌ಗಳು ಡೇಟಾವನ್ನು ವಿಶ್ಲೇಷಿಸಿ ಯಂತ್ರ ಕಲಿಕೆ (Machine Learning) ಮೂಲಕ ಮುನ್ಸೂಚನೆ ನೀಡುತ್ತಾರೆ.",
            overview = "Voted 'The Sexiest Job of the 21st Century' offering immense salary growth and strategic corporate influence.",
            overviewKn = "21ನೇ ಶತಮಾನದ ಅತ್ಯಂತ ಉನ್ನತ ಬೇಡಿಕೆಯ ಡಿಜಿಟಲ್ ವೃತ್ತಿ.",
            whyChoose = "Highest salaries across IT sector, business strategy impact, and working with modern AI technology.",
            whyChooseKn = "ಐಟಿ ವಲಯದಲ್ಲಿ ಅತ್ಯುನ್ನತ ವೇತನ, ಆಧುನಿಕ ಎಐ ತಂತ್ರಜ್ಞಾನದ ಜೊತೆ ಕೆಲಸ.",
            benefits = "• Top-tier IT compensation packages\n• High influence on executive business decisions\n• Global remote work options",
            benefitsKn = "• ಉನ್ನತ ಹಂತದ ಐಟಿ ವೇತನ\n• ಜಾಗತಿಕ ರಿಮೋಟ್ ಕೆಲಸದ ಅವಕಾಶಗಳು",
            eligibility = "12th Standard Science PCM / Computer Science / Statistics / Mathematics.",
            eligibilityKn = "12ನೇ ತರಗತಿ (PCM/CS/Maths).",
            subjects = "Python, R, Linear Algebra, Probability & Statistics, SQL, Machine Learning, Deep Learning, PowerBI",
            subjectsKn = "ಪೈಥಾನ್, ಮೆಷಿನ್ ಲರ್ನಿಂಗ್, ಸ್ಟಾಟಿಸ್ಟಿಕ್ಸ್, SQL",
            exams = "JEE Main, B.Tech / B.Sc CS Entrance Exams, GATE Data Science",
            examsKn = "JEE Main, GATE Data Science",
            pathway = "Step 1: 10+2 PCM\nStep 2: B.Tech CS / B.Sc Statistics / BCA\nStep 3: Master Python, SQL, Pandas, Scikit-Learn\nStep 4: Build real-world Kaggle portfolio projects\nStep 5: Apply for Data Science roles",
            pathwayKn = "ಹಂತ 1: 10+2 PCM\nಹಂತ 2: B.Tech CS / B.Sc Statistics\nಹಂತ 3: ಪೈಥಾನ್ ಮತ್ತು SQL ಕಲಿಯಿರಿ\nಹಂತ 4: ಡೇಟಾ ಅನಾಲಿಸ್ಟ್/ಸೈಂಟಿಸ್ಟ್ ಆಗಿ ಸೇರಿ",
            skills = "Python Data Stack (Pandas/NumPy), SQL, Machine Learning, Tableau, Predictive Modeling",
            skillsKn = "ಪೈಥಾನ್, SQL, ಮೆಷಿನ್ ಲರ್ನಿಂಗ್, ಡೇಟಾ ವಿಶ್ಲೇಷಣೆ",
            jobRoles = "Data Scientist, Data Analyst, Machine Learning Engineer, Business Intelligence Lead",
            jobRolesKn = "ಡೇಟಾ ಸೈಂಟಿಸ್ಟ್, ಡೇಟಾ ಅನಾಲಿಸ್ಟ್, ML ಎಂಜಿನಿಯರ್",
            topColleges = "ISI Kolkata, IIT Madras, IISc Bangalore, BITS Pilani, IIIT Bangalore",
            topCollegesKn = "ISI ಕೋಲ್ಕತ್ತಾ, IISc ಬೆಂಗಳೂರು, IIT ಮದ್ರಾಸ್",
            topRecruiters = "Amazon, Microsoft, Google, Accenture, Tiger Analytics, Fractal Analytics, Mu Sigma",
            topRecruitersKn = "ಅಮೆಜಾನ್, ಗೂಗಲ್, ಮೈಕ್ರೋಸಾಫ್ಟ್, ಆಕ್ಸೆಂಟ್ಚರ್",
            salaryAverage = "₹ 10 - 20 Lakhs / Year",
            salaryHighest = "₹ 55+ Lakhs / Year",
            feesAverage = "₹ 2 - 5 Lakhs / Year",
            duration = "4 Years (B.Tech) / 3 Years (B.Sc)",
            scholarships = "IBM Data Science Grant, Post-Matric Tech Fellowships",
            scholarshipsKn = "IBM ಡೇಟಾ ಸೈನ್ಸ್ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "Unstoppable expansion as every enterprise uses big data to personalize services and optimize costs.",
            futureScopeKn = "ಎಲ್ಲಾ ಪ್ರಮುಖ ಕಂಪನಿಗಳು ಡೇಟಾ ಆಧಾರಿತ ನಿರ್ಧಾರಗಳನ್ನು ತೆಗೆದುಕೊಳ್ಳುವುದರಿಂದ ಅಪಾರ ಅವಕಾಶ.",
            certifications = "TensorFlow Developer Certificate, Microsoft Certified Azure Data Scientist, AWS Machine Learning",
            certificationsKn = "ಮೈಕ್ರೋಸಾಫ್ಟ್ ಡೇಟಾ ಸೈಂಟಿಸ್ಟ್ ಸರ್ಟಿಫಿಕೇಟ್",
            faqs = "Q: Is math necessary for Data Science?\nA: Yes, basic statistics, linear algebra, and probability are crucial.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಡೇಟಾ ಸೈನ್ಸ್‌ಗೆ ಗಣಿತ ಅಗತ್ಯವೇ?\nಉತ್ತರ: ಹೌದು, ಮೂಲಭೂತ ಸಂಖ್ಯಾಶಾಸ್ತ್ರ (Statistics) ಅಗತ್ಯವಿದೆ.",
            category = "IT"
        ),
        CareerEntity(
            id = 14,
            title = "Clinical Psychologist & Counsellor",
            titleKn = "ಮನೋವಿಜ್ಞಾನಿ (Psychologist & Counsellor)",
            image = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=800",
            tagline = "Counsel mental health, conduct psychotherapy, and diagnose behavioral wellness",
            taglineKn = "ಮಾನಸಿಕ ಆರೋಗ್ಯಕ್ಕೆ ಆಪ್ತಸಲಹೆ ಮತ್ತು ಕೌನ್ಸೆಲಿಂಗ್ ನೀಡಿ",
            description = "Psychologists counsel individuals, conduct cognitive behavioral therapy (CBT), treat anxiety/depression, and enhance mental well-being.",
            descriptionKn = "ಮನೋವಿಜ್ಞಾನಿಗಳು ಮಾನಸಿಕ ಒತ್ತಡ, ಖಿನ್ನತೆ ನಿವಾರಿಸಲು ಕೌನ್ಸೆಲಿಂಗ್ ನೀಡುತ್ತಾರೆ.",
            overview = "Fast-growing healthcare field addressing mental health awareness, corporate wellness, and student counseling.",
            overviewKn = "ಮಾನಸಿಕ ಆರೋಗ್ಯದ ಅರಿವು ಹೆಚ್ಚುತ್ತಿರುವ ಈ ಕಾಲದಲ್ಲಿ ಅತ್ಯಂತ ಅಗತ್ಯದ ವೃತ್ತಿ.",
            whyChoose = "Deep personal fulfillment, independent private practice, and high demand in corporate wellness.",
            whyChooseKn = "ಮಾನಸಿಕ ನೆಮ್ಮದಿ ನೀಡುವ ತೃಪ್ತಿ, ಸ್ವಂತ ಕೌನ್ಸೆಲಿಂಗ್ ಕ್ಲಿನಿಕ್ ಸ್ಥಾಪನೆ.",
            benefits = "• High personal satisfaction helping people heal emotionally\n• Flexible private therapy practice hours\n• Scope in schools, corporate MNCs, and psychiatric hospitals",
            benefitsKn = "• ಜನರ ಜೀವನ ಸುಧಾರಿಸುವ ಮಾನವೀಯ ವೃತ್ತಿ\n• ಶಾಲೆಗಳು ಮತ್ತು ಕಾರ್ಪೊರೇಟ್ ಸಂಸ್ಥೆಗಳಲ್ಲಿ ಕೌನ್ಸೆಲರ್ ಅವಕಾಶ",
            eligibility = "12th Standard Any Stream (Arts / Science preferred) minimum 50% marks.",
            eligibilityKn = "12ನೇ ತರಗತಿ (ಯಾವುದೇ ವಿಭಾಗ) ಕನಿಷ್ಠ 50%.",
            subjects = "General Psychology, Abnormal Psychology, Cognitive Therapy, Psychometrics, Counseling Skills",
            subjectsKn = "ಮನೋವಿಜ್ಞಾನ, ಕೌನ್ಸೆಲಿಂಗ್, ಥೆರಪಿ",
            exams = "CUET UG / PG, NIMHANS Entrance Exam, RCI M.Phil Entrance",
            examsKn = "CUET, NIMHANS ಪ್ರವೇಶ ಪರೀಕ್ಷೆ",
            pathway = "Step 1: 10+2 Any Stream\nStep 2: 3/4 Years B.A. / B.Sc Psychology\nStep 3: 2 Years M.A. / M.Sc Clinical Psychology\nStep 4: M.Phil in Clinical Psychology for Rehabilitation Council of India (RCI) license\nStep 5: Practice as Licensed Clinical Psychologist",
            pathwayKn = "ಹಂತ 1: 10+2 ಯಾವುದೇ ವಿಭಾಗ\nಹಂತ 2: B.A. / B.Sc Psychology\nಹಂತ 3: M.A. Psychology + RCI ಲೈಸೆನ್ಸ್",
            skills = "Active Listening, Psychotherapy (CBT), Empathetic Communication, Psychological Assessment, Confidentiality",
            skillsKn = "ಗಮನವಿಟ್ಟು ಕೇಳುವುದು, ಕರುಣೆ, ಕೌನ್ಸೆಲಿಂಗ್",
            jobRoles = "Clinical Psychologist, Corporate Wellness Specialist, School Counsellor, Neuropsychologist",
            jobRolesKn = "ಕ್ಲಿನಿಕಲ್ ಸೈಕಾಲಜಿಸ್ಟ್, ಶಾಲಾ ಕೌನ್ಸೆಲರ್, ಕಾರ್ಪೊರೇಟ್ ವೆಲ್ನೆಸ್ ಕೋಚ್",
            topColleges = "NIMHANS Bangalore, Christ University Bangalore, Delhi University, TISS Mumbai",
            topCollegesKn = "NIMHANS ಬೆಂಗಳೂರು, ಕ್ರೈಸ್ಟ್ ಯುನಿವರ್ಸಿಟಿ, TISS ಮುಂಬೈ",
            topRecruiters = "NIMHANS, MindPeers, Fortis Mental Health, International Schools, Corporate Wellness Programs",
            topRecruitersKn = "NIMHANS, ಫೋರ್ಟಿಸ್ ಮೆಂಟಲ್ ಹೆಲ್ತ್, ಅಂತರರಾಷ್ಟ್ರೀಯ ಶಾಲೆಗಳು",
            salaryAverage = "₹ 5 - 10 Lakhs / Year",
            salaryHighest = "₹ 22+ Lakhs / Year",
            feesAverage = "₹ 60,000 - 2 Lakhs / Year",
            duration = "3 Years (BA) + 2 Years (MA) + 2 Years (M.Phil)",
            scholarships = "NIMHANS Fellowship, Government Social Welfare Fellowships",
            scholarshipsKn = "NIMHANS ಫೆಲೋಶಿಪ್",
            futureScope = "Tremendous increase in demand due to rising corporate stress, student career anxiety, and therapy acceptance.",
            futureScopeKn = "ಮಾನಸಿಕ ಆರೋಗ್ಯದ ಮಹತ್ವ ಹೆಚ್ಚುತ್ತಿರುವುದರಿಂದ ಭವಿಷ್ಯದಲ್ಲಿ ಉನ್ನತ ಬೇಡಿಕೆ.",
            certifications = "RCI Licensed Clinical Psychologist Registration, CBT Certified Practitioner",
            certificationsKn = "RCI ನೋಂದಣಿ ಪ್ರಮಾಣಪತ್ರ",
            faqs = "Q: What is the difference between Psychiatrist and Psychologist?\nA: Psychiatrists hold MBBS+MD and prescribe drugs; Psychologists hold M.A./M.Phil and use therapy.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಸೈಕಿಯಾಟ್ರಿಸ್ಟ್ ಮತ್ತು ಸೈಕಾಲಜಿಸ್ಟ್ ನಡುವಿನ ವ್ಯತ್ಯಾಸವೇನು?\nಉತ್ತರ: ಸೈಕಿಯಾಟ್ರಿಸ್ಟ್ (MBBS ವೈದ್ಯರು) ಔಷಧಿ ನೀಡುತ್ತಾರೆ; ಸೈಕಾಲಜಿಸ್ಟ್ ಥೆರಪಿ ನೀಡುವುದನ್ನು ಮಾಡುತ್ತಾರೆ.",
            category = "Health Sciences"
        ),
        CareerEntity(
            id = 15,
            title = "Air Hostess & Cabin Crew Manager",
            titleKn = "ಏರ್ ಹೊಸ್ಟೆಸ್ ಮತ್ತು ಕ್ಯಾಬಿನ್ ಕ್ರೂ (Air Hostess)",
            image = "https://images.unsplash.com/photo-1569154941061-e231b4725ef1?w=800",
            tagline = "Ensure passenger inflight safety, hospitality excellence, and international air travel",
            taglineKn = "ವಿಮಾನದಲ್ಲಿ ಪ್ರಯಾಣಿಕರ ಸುರಕ್ಷತೆ ಮತ್ತು ಆತಿಥ್ಯ ಸೇವೆ ಒದಗಿಸಿ",
            description = "Cabin Crew members greet passengers, handle inflight safety procedures, serve gourmet meals, and manage emergency evacuation readiness.",
            descriptionKn = "ಏರ್ ಹೊಸ್ಟೆಸ್ ಮತ್ತು ಕ್ಯಾಬಿನ್ ಕ್ರೂ ಸಿಬ್ಬಂದಿ ವಿಮಾನದಲ್ಲಿ ಪ್ರಯಾಣಿಕರ ಸುರಕ್ಷತೆ ಮತ್ತು ಸೇವೆ ವಹಿಸುತ್ತಾರೆ.",
            overview = "Exciting aviation hospitality career offering global travel, luxury lifestyle, and attractive tax-free salaries in Gulf airlines.",
            overviewKn = "ಅಂತರರಾಷ್ಟ್ರೀಯ ಪ್ರವಾಸ ಮತ್ತು ಉನ್ನತ ಶೈಲಿಯ ವಿಮಾನಯಾನ ವೃತ್ತಿ.",
            whyChoose = "Fly to global destinations every week, tax-free international salary, five-star layover hotels, and high glamor.",
            whyChooseKn = "ಜಗತ್ತಿನ ವಿವಿಧ ದೇಶಗಳಿಗೆ ಪ್ರವಾಸ, ತೆರಿಗೆ ರಹಿತ ವೇತನ, ಪಂಚತಾರಾ ಹೊಟೇಲ್ ವಾಸ್ತವ್ಯ.",
            benefits = "• Visit 50+ countries with free flight tickets for family\n• Tax-free earnings in Emirates, Qatar Airways, and Etihad\n• Free luxury accommodation in Dubai / Doha",
            benefitsKn = "• ಉಚಿತ ವಿಮಾನ ಟಿಕೆಟ್‌ಗಳು, ದುಬೈನಲ್ಲಿ ಉಚಿತ ಉನ್ನತ ವಾಸ್ತವ್ಯ",
            eligibility = "12th Standard Any Stream minimum 50% marks + Good communication & personality.",
            eligibilityKn = "12ನೇ ತರಗತಿ ಕನಿಷ್ಠ 50% + ಉತ್ತಮ ಸಂವಹನ ಕೌಶಲ್ಯ.",
            subjects = "Aviation Safety, Emergency Evacuation, Grooming & Etiquette, Inflight Service, First Aid",
            subjectsKn = "ವಿಮಾನ ಸುರಕ್ಷತೆ, ಆತಿಥ್ಯ, ಪ್ರಥಮ ಚಿಕಿತ್ಸೆ",
            exams = "Airline Group Discussion, Personal Interview, Swimming & Medical Fitness Test",
            examsKn = "ಏರ್‌ಲೈನ್ ಸಂದರ್ಶನ ಮತ್ತು ಮೆಡಿಕಲ್ ಪರೀಕ್ಷೆ",
            pathway = "Step 1: Pass 10+2 Any Stream\nStep 2: Diploma in Aviation / Hospitality (Optional)\nStep 3: Apply directly to Airline Cabin Crew Open Days\nStep 4: Group Discussion & Grooming Interview\nStep 5: 3 Months Airline Flying Training in Academy",
            pathwayKn = "ಹಂತ 1: 10+2 ಯಾವುದೇ ವಿಭಾಗ\nಹಂತ 2: ಏರ್‌ಲೈನ್ ಕ್ಯಾಬಿನ್ ಕ್ರೂ ಸಂದರ್ಶನ\nಹಂತ 3: 3 ತಿಂಗಳ ಏರ್‌ಲೈನ್ ತರಬೇತಿ ಪಡೆದು ಕೆಲಸ ಆರಂಭಿಸಿ",
            skills = "Grooming Excellence, Inflight Safety, Fluency in English, Customer Service, Emergency Management",
            skillsKn = "ಇಂಗ್ಲಿಷ್ ಸಂವಹನ, ಆತಿಥ್ಯ, ಸುರಕ್ಷತಾ ನಿಯಮಗಳು",
            jobRoles = "Flight Attendant, Senior Cabin Crew, Inflight Service Manager, Cabin Safety Trainer",
            jobRolesKn = "ಫ್ಲೈಟ್ ಅಟೆಂಡೆಂಟ್, ಸೀನಿಯರ್ ಕ್ಯಾಬಿನ್ ಕ್ರೂ, ಕ್ಯಾಬಿನ್ ಟ್ರೇನರ್",
            topColleges = "Frankfinn Institute of Air Hostess Training, IATA Certified Academies, Airline Direct In-House Academies",
            topCollegesKn = "ಫ್ರಾಂಕ್‌ಫಿನ್ ಇನ್‌ಸ್ಟಿಟ್ಯೂಟ್, IATA ಅಕಾಡೆಮಿಗಳು",
            topRecruiters = "Emirates, Qatar Airways, Etihad, IndiGo, Air India, Singapore Airlines, Vistara",
            topRecruitersKn = "ಎಮಿರೇಟ್ಸ್, ಕತಾರ್ ಏರ್‌ವೇಸ್, ಇಂಡಿಗೋ, ಏರ್ ಇಂಡಿಯಾ",
            salaryAverage = "₹ 6 - 12 Lakhs (Domestic) | ₹ 18 - 28 Lakhs / Year (International)",
            salaryHighest = "₹ 35+ Lakhs / Year (Tax-Free)",
            feesAverage = "₹ 80,000 - 1.8 Lakhs (Diploma / Training)",
            duration = "6 Months to 1 Year Diploma",
            scholarships = "Airline In-House Paid Training Programs",
            scholarshipsKn = "ಏರ್‌ಲೈನ್ ಉಚಿತ ತರಬೇತಿ ಕಾರ್ಯಕ್ರಮಗಳು",
            futureScope = "Huge hiring boom in India with over 100+ new international airports opening.",
            futureScopeKn = "ಭಾರತದಲ್ಲಿ ನೂರಾರು ಹೊಸ ವಿಮಾನ ನಿಲ್ದಾಣಗಳು ತೆರೆಯುತ್ತಿರುವುದರಿಂದ ಅಪಾರ ಬೇಡಿಕೆ.",
            certifications = "IATA Cabin Crew Certificate, Aviation First Aid & CRM Safety License",
            certificationsKn = "IATA ಕ್ಯಾಬಿನ್ ಕ್ರೂ ಪ್ರಮಾಣಪತ್ರ",
            faqs = "Q: Is height criterion strict for Cabin Crew?\nA: Yes, minimum reach requirement (usually 212 cm reach on toes) is mandatory for reaching overhead safety bins.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಎತ್ತರ ಕಡ್ಡಾಯವೇ?\nಉತ್ತರ: ಹೌದು, ವಿಮಾನದ ಸೇಫ್ಟಿ ಬಾಕ್ಸ್ ತಲುಪಲು ಕನಿಷ್ಠ ಎತ್ತರ ಅಗತ್ಯವಿದೆ.",
            category = "Aviation"
        ),
        CareerEntity(
            id = 16,
            title = "High School & College Educator",
            titleKn = "ಶಿಕ್ಷಕರು ಮತ್ತು ಉಪನ್ಯಾಸಕರು (Teacher / Educator)",
            image = "https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=800",
            tagline = "Educate future generations, conduct academic pedagogy, and shape student minds",
            taglineKn = "ಮುಂದಿನ ಪೀಳಿಗೆಗೆ ಮೌಲ್ಯಯುತ ಶಿಕ್ಷಣ ನೀಡಿ",
            description = "Educators teach academic curricula in schools, colleges, and coaching institutes while mentoring student growth and educational research.",
            descriptionKn = "ಶಿಕ್ಷಕರು ಮತ್ತು ಉಪನ್ಯಾಸಕರು ಶಾಲೆ, ಕಾಲೇಜುಗಳಲ್ಲಿ ಪಾಠ ಪ್ರವಚನ ನಡೆಸುತ್ತಾರೆ.",
            overview = "The foundational noble career that builds all other professions. High job stability and long summer vacations.",
            overviewKn = "ಎಲ್ಲಾ ವೃತ್ತಿಗಳಿಗೆ ತಾಯಿಯಂತಿರುವ ಪವಿತ್ರ ಶಿಕ್ಷಣ ಕ್ಷೇತ್ರ.",
            whyChoose = "Social respect, work-life balance with regular holidays, government job pension security, and shaping youth.",
            whyChooseKn = "ಸಮಾಜದಲ್ಲಿ ಅಪಾರ ಗೌರವ, ನಿಗದಿತ ಕೆಲಸದ ಸಮಯ ಮತ್ತು ರಜಾ ದಿನಗಳು.",
            benefits = "• High social respect and community prestige\n• Regular school holidays & summer vacation\n• Government pension & security in public schools",
            benefitsKn = "• ಸಮಾಜದಲ್ಲಿ ನಿಸ್ವಾರ್ಥ ಸೇವೆಯ ಗೌರವ\n• ಪ್ರವಾಸಗಳು ಮತ್ತು ಬೇಸಿಗೆ ರಜೆಗಳು",
            eligibility = "Graduation in relevant subject (B.Sc / B.A. / B.Com) + B.Ed Degree.",
            eligibilityKn = "ಪದವಿ (B.Sc/B.A.) + B.Ed ಕೋರ್ಸ್.",
            subjects = "Pedagogy, Educational Psychology, Mathematics, Science, Kannada, English, Social Studies",
            subjectsKn = "ಬೋಧನಾ ಶಾಸ್ತ್ರ, ಗಣಿತ, ವಿಜ್ಞಾನ, ಕನ್ನಡ, ಇಂಗ್ಲಿಷ್",
            exams = "KTET (Karnataka TET), CTET (Central TET), NET / SET (For College Lecturers)",
            examsKn = "KTET, CTET, UGC NET, SET",
            pathway = "Step 1: Graduation in Subject (3/4 Years)\nStep 2: Complete B.Ed Degree (2 Years)\nStep 3: Clear KTET / CTET exam\nStep 4: Government Teacher Recruitment (GPSTR / HSTR) or join top International Schools",
            pathwayKn = "ಹಂತ 1: ಪದವಿ (B.A./B.Sc)\nಹಂತ 2: 2 ವರ್ಷಗಳ B.Ed\nಹಂತ 3: KTET / CTET ಪರೀಕ್ಷೆ ಉತ್ತೀರ್ಣರಾಗಿ",
            skills = "Classroom Management, Clear Explanation, Patience, Student Mentorship, Digital Smart Board Teaching",
            skillsKn = "ಬೋಧನಾ ಕೌಶಲ್ಯ, ತಾಳ್ಮೆ, ತರಗತಿ ನಿರ್ವಹಣೆ",
            jobRoles = "Primary Teacher, High School Teacher (TGT), Senior Secondary Teacher (PGT), College Lecturer, Principal",
            jobRolesKn = "ಪ್ರಾಥಮಿಕ ಶಿಕ್ಷಕರು, ಪ್ರೌಢಶಾಲಾ ಶಿಕ್ಷಕರು (TGT), ಉಪನ್ಯಾಸಕರು",
            topColleges = "Regional Institute of Education (RIE) Mysore, Bangalore University, Central University of Karnataka",
            topCollegesKn = "RIE ಮೈಸೂರು, ಬೆಂಗಳೂರು ವಿಶ್ವವಿದ್ಯಾಲಯ",
            topRecruiters = "Government Public Schools (GPSTR), Kendriya Vidyalaya (KVS), Navodaya Vidyalaya, National Public School, DPS",
            topRecruitersKn = "ಸರ್ಕಾರಿ ಶಾಲೆಗಳು (GPSTR), ಕೇಂದ್ರೀಯ ವಿದ್ಯಾಲಯ (KVS), ನವೋದಯ, ನ್ಯಾಷನಲ್ ಪಬ್ಲಿಕ್ ಸ್ಕೂಲ್",
            salaryAverage = "₹ 4.5 - 9 Lakhs / Year",
            salaryHighest = "₹ 18+ Lakhs / Year",
            feesAverage = "₹ 30,000 - 90,000 / Year",
            duration = "3 Years (Bachelor's) + 2 Years (B.Ed)",
            scholarships = "National Teacher Welfare Fellowships, State Merit Grants",
            scholarshipsKn = "ರಾಷ್ಟ್ರೀಯ ಶಿಕ್ಷಕ ಕಲ್ಯಾಣ ಸ್ಕಾಲರ್‌ಶಿಪ್",
            futureScope = "High demand for specialized STEM teachers, EdTech online educators, and international IB school faculty.",
            futureScopeKn = "ಎಡ್‌ಟೆಕ್ ಮತ್ತು ಇಂಟರ್ನ್ಯಾಷನಲ್ ಶಾಲೆಗಳಿಂದ ಶಿಕ್ಷಕರಿಗೆ ಹೊಸ ಅವಕಾಶಗಳು.",
            certifications = "KTET / CTET Eligibility Certificate, B.Ed Degree, Digital Pedagogy Certificate",
            certificationsKn = "KTET / CTET ಉತ್ತೀರ್ಣ ಪ್ರಮಾಣಪತ್ರ",
            faqs = "Q: Is TET mandatory for government school teachers?\nA: Yes, TET (Teacher Eligibility Test) is legally mandatory across India.",
            faqsKn = "ಪ್ರಶ್ನೆ: ಸರ್ಕಾರಿ ಶಾಲೆ ಶಿಕ್ಷಕರಾಗಲು TET ಕಡ್ಡಾಯವೇ?\nಉತ್ತರ: ಹೌದು, TET (Teacher Eligibility Test) ಕಡ್ಡಾಯವಾಗಿದೆ.",
            category = "Education"
        )
    )

    val initialColleges = listOf(
        CollegeEntity(
            id = 1,
            name = "All India Institute of Medical Sciences (AIIMS)",
            nameKn = "ಆಲ್ ಇಂಡಿಯಾ ಇನ್‌ಸ್ಟಿಟ್ಯೂಟ್ ಆಫ್ ಮೆಡಿಕಲ್ ಸೈನ್ಸಸ್ (AIIMS)",
            location = "New Delhi, Delhi",
            locationKn = "ನವದೆಹಲಿ, ದೆಹಲಿ",
            state = "Delhi",
            city = "New Delhi",
            coursesOffered = "MBBS, Nursing, Allied Health Sciences, MD/MS",
            coursesOfferedKn = "ಎಂಬಿಬಿಎಸ್, ನರ್ಸಿಂಗ್, ಅಲೈಡ್ ಹೆಲ್ತ್ ಸೈನ್ಸಸ್, ಎಂಡಿ/ಎಮ್‌ಎಸ್",
            minFeesLakhs = 0.05,
            maxFeesLakhs = 0.20,
            hostelAvailable = true,
            hostelFeesPerYear = 12000,
            recognition = "NMC Recognized, Institute of National Importance",
            recognitionKn = "ಎನ್‌ಎಮ್‌ಸಿ ಮಾನ್ಯತೆ ಪಡೆದ, ರಾಷ್ಟ್ರೀಯ ಮಹತ್ವದ ಸಂಸ್ಥೆ",
            ranking = 1,
            eligibility = "12th PCB min 60% + NEET UG Rank under 100",
            eligibilityKn = "12ನೇ ತರಗತಿ ಪಿಸಿಬಿ ಕನಿಷ್ಠ 60% + ನೀಟ್ ಯುಜಿ ರ‍್ಯಾಂಕ್ 100ರ ಒಳಗೆ",
            admissionStatus = "Open",
            imageUrl = "https://images.unsplash.com/photo-1562774053-701939374585?w=800",
            heroBannerUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=1200",
            overview = "AIIMS New Delhi is the premier medical university in Asia, offering world-class medical education, clinical research, and super-specialty hospital infrastructure.",
            overviewKn = "AIIMS ನವದೆಹಲಿಯು ಏಷ್ಯಾದ ಅಗ್ರಗಣ್ಯ ವೈದ್ಯಕೀಯ ವಿಶ್ವವಿದ್ಯಾಲಯವಾಗಿದ್ದು, ವಿಶ್ವದರ್ಜೆಯ ವೈದ್ಯಕೀಯ ಶಿಕ್ಷಣ, ಕ್ಲಿನಿಕಲ್ ಸಂಶೋಧನೆ ಮತ್ತು ಸೂಪರ್-ಸ್ಪೆಷಾಲಿಟಿ ಆಸ್ಪತ್ರೆ ಮೂಲಸೌಕರ್ಯವನ್ನು ಒದಗಿಸುತ್ತದೆ.",
            averagePackageLakhs = 18.0,
            highestPackageLakhs = 35.0,
            isFeatured = true
        ),
        CollegeEntity(
            id = 2,
            name = "Christian Medical College (CMC)",
            nameKn = "ಕ್ರಿಶ್ಚಿಯನ್ ಮೆಡಿಕಲ್ ಕಾಲೇಜು (CMC)",
            location = "Vellore, Tamil Nadu",
            locationKn = "ವೇಲೂರು, ತಮಿಳುನಾಡು",
            state = "Tamil Nadu",
            city = "Vellore",
            coursesOffered = "MBBS, Nursing, Physiotherapy, Allied Health Sciences",
            coursesOfferedKn = "ಎಂಬಿಬಿಎಸ್, ನರ್ಸಿಂಗ್, ಫಿಸಿಯೋಥೆರಪಿ, ಅಲೈಡ್ ಹೆಲ್ತ್ ಸೈನ್ಸಸ್",
            minFeesLakhs = 0.60,
            maxFeesLakhs = 1.50,
            hostelAvailable = true,
            hostelFeesPerYear = 45000,
            recognition = "NAAC A++, NMC Approved, UGC",
            recognitionKn = "ಎನ್‌ಎಎಸಿ A++, ಎನ್‌ಎಮ್‌ಸಿ ಅನುಮೋದಿತ, ಯುಜಿಸಿ",
            ranking = 2,
            eligibility = "12th PCB min 60% + NEET UG Score",
            eligibilityKn = "12ನೇ ತರಗತಿ ಪಿಸಿಬಿ ಕನಿಷ್ಠ 60% + ನೀಟ್ ಯುಜಿ ಅಂಕಗಳು",
            admissionStatus = "Open",
            imageUrl = "https://images.unsplash.com/photo-1519452635265-7b1fbfd1e4e0?w=800",
            heroBannerUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=1200",
            overview = "CMC Vellore is renowned for holistic healthcare education, top-tier clinical rotations, and excellent research grants.",
            overviewKn = "ಸಿಎಮ್‌ಸಿ ವೇಲೂರು ಸಮಗ್ರ ಆರೋಗ್ಯ ರಕ್ಷಣಾ ಶಿಕ್ಷಣ, ಉನ್ನತ ಮಟ್ಟದ ಕ್ಲಿನಿಕಲ್ ತರಬೇತಿ ಮತ್ತು ಅತ್ಯುತ್ತಮ ಸಂಶೋಧನೆಗೆ ಹೆಸರುವಾಸಿಯಾಗಿದೆ.",
            averagePackageLakhs = 15.0,
            highestPackageLakhs = 28.0,
            isFeatured = true
        ),
        CollegeEntity(
            id = 3,
            name = "Indian Institute of Technology (IIT) Bombay",
            nameKn = "ಇಂಡಿಯನ್ ಇನ್‌ಸ್ಟಿಟ್ಯೂಟ್ ಆಫ್ ಟೆಕ್ನಾಲಜಿ (IIT) ಮುಂಬೈ",
            location = "Mumbai, Maharashtra",
            locationKn = "ಮುಂಬೈ, ಮಹಾರಾಷ್ಟ್ರ",
            state = "Maharashtra",
            city = "Mumbai",
            coursesOffered = "Engineering, Computer Science, AI, Aerospace",
            coursesOfferedKn = "ಎಂಜಿನಿಯರಿಂಗ್, ಕಂಪ್ಯೂಟರ್ ಸೈನ್ಸ್, ಎಐ, ಏರೋಸ್ಪೇಸ್",
            minFeesLakhs = 2.20,
            maxFeesLakhs = 8.80,
            hostelAvailable = true,
            hostelFeesPerYear = 60000,
            recognition = "Institute of Eminence, AICTE Approved",
            recognitionKn = "ಉನ್ನತ ಶ್ರೇಷ್ಠತೆಯ ಸಂಸ್ಥೆ, ಎಐಸಿಟಿಇ ಅನುಮೋದಿತ",
            ranking = 3,
            eligibility = "12th PCM min 75% + JEE Advanced Top Ranks",
            eligibilityKn = "12ನೇ ತರಗತಿ ಪಿಸಿಎಮ್ ಕನಿಷ್ಠ 75% + ಜೆಇಇ ಅಡ್ವಾನ್ಸ್ಡ್ ಉನ್ನತ ರ‍್ಯಾಂಕ್",
            admissionStatus = "Closing Soon",
            imageUrl = "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=800",
            heroBannerUrl = "https://images.unsplash.com/photo-1498243691581-b145c3f54a5a?w=1200",
            overview = "IIT Bombay leads global engineering education with state-of-the-art incubation labs, international exchange, and top global recruiters.",
            overviewKn = "ಐಐಟಿ ಮುಂಬೈ ಜಾಗತಿಕ ಎಂಜಿನಿಯರಿಂಗ್ ಶಿಕ್ಷಣದಲ್ಲಿ ಆಧುನಿಕ ಇನ್‌ಕ್ಯುಬೇಷನ್ ಲ್ಯಾಬ್‌ಗಳು ಮತ್ತು ಉನ್ನತ ಜಾಗತಿಕ ಕಂಪನಿಗಳ ನೇಮಕಾತಿಯೊಂದಿಗೆ ಮುಂಚೂಣಿಯಲ್ಲಿದೆ.",
            averagePackageLakhs = 24.0,
            highestPackageLakhs = 120.0,
            isFeatured = true
        )
    )

    val initialCourses = listOf(
        CourseEntity(
            id = 1,
            code = "MBBS",
            name = "Bachelor of Medicine and Bachelor of Surgery",
            nameKn = "ಬ್ಯಾಚುಲರ್ ಆಫ್ ಮೆಡಿಸಿನ್ ಮತ್ತು ಬ್ಯಾಚುಲರ್ ಆಫ್ ಸರ್ಜರಿ (MBBS)",
            category = "Medical",
            durationYears = "5.5 Years",
            eligibility = "12th PCB min 50% + NEET UG Qualified",
            eligibilityKn = "12ನೇ ತರಗತಿ ಪಿಸಿಬಿ ಕನಿಷ್ಠ 50% + ನೀಟ್ ಯುಜಿ ಅರ್ಹತೆ",
            avgFeesLakhs = 8.5,
            avgPackageLakhs = 16.0,
            careerScope = "Hospital Doctor, Medical Officer, Surgeon, Clinical Researcher, PG Specialization",
            careerScopeKn = "ಆಸ್ಪತ್ರೆ ವೈದ್ಯರು, ಮೆಡಿಕಲ್ ಆಫೀಸರ್, ಶಸ್ತ್ರಚಿಕಿತ್ಸಕರು, ಕ್ಲಿನಿಕಲ್ ಸಂಶೋಧಕರು, ಸ್ನಾತಕೋತ್ತರ ವಿಶೇಷತೆ",
            description = "The premier undergraduate medical degree program preparing doctors in anatomy, physiology, pharmacology, medicine, and surgery.",
            descriptionKn = "ಅಂಗರಚನಾಶಾಸ್ತ್ರ, ಶರೀರಶಾಸ್ತ್ರ, ಫಾರ್ಮಾಕೋಲಜಿ, ವೈದ್ಯಕೀಯ ಮತ್ತು ಶಸ್ತ್ರಚಿಕಿತ್ಸೆಯಲ್ಲಿ ವೈದ್ಯರನ್ನು ಸಿದ್ಧಪಡಿಸುವ ಪ್ರಮುಖ ಪದವಿ ಕಾರ್ಯಕ್ರಮ."
        ),
        CourseEntity(
            id = 2,
            code = "BTECH-CS",
            name = "B.Tech Computer Science & Engineering",
            nameKn = "ಬಿ.ಟೆಕ್ ಕಂಪ್ಯೂಟರ್ ಸೈನ್ಸ್ & ಎಂಜಿನಿಯರಿಂಗ್",
            category = "Engineering",
            durationYears = "4 Years",
            eligibility = "12th PCM min 60% + JEE / State Entrance",
            eligibilityKn = "12ನೇ ತರಗತಿ ಪಿಸಿಎಮ್ ಕನಿಷ್ಠ 60% + ಜೆಇಇ / ರಾಜ್ಯ ಪ್ರವೇಶ ಪರೀಕ್ಷೆ",
            avgFeesLakhs = 6.5,
            avgPackageLakhs = 14.0,
            careerScope = "Software Engineer, Full Stack Developer, Cloud Architect, Systems Engineer",
            careerScopeKn = "ಸಾಫ್ಟ್‌ವೇರ್ ಎಂಜಿನಿಯರ್, ಫುಲ್ ಸ್ಟ್ಯಾಕ್ ಡೆವಲಪರ್, ಕ್ಲೌಡ್ ಆರ್ಕಿಟೆಕ್ಟ್, ಸಿಸ್ಟಮ್ಸ್ ಎಂಜಿನಿಯರ್",
            description = "Foundational CS degree covering algorithms, data structures, operating systems, cloud networks, and software engineering.",
            descriptionKn = "ಆಲ್ಗಾರಿದಮ್‌ಗಳು, ಡೇಟಾ ಸ್ಟ್ರಕ್ಚರ್‌ಗಳು, ಆಪರೇಟಿಂಗ್ ಸಿಸ್ಟಮ್‌ಗಳು, ಕ್ಲೌಡ್ ನೆಟ್‌ವರ್ಕ್‌ಗಳು ಮತ್ತು ಸಾಫ್ಟ್‌ವೇರ್ ಎಂಜಿನಿಯರಿಂಗ್ ಒಳಗೊಂಡ ಮೂಲಭೂತ ಸಿಎಸ್ ಪದವಿ."
        )
    )

    val initialScholarships = listOf(
        ScholarshipEntity(
            id = 1,
            title = "National Merit Scholarship for Higher Education",
            provider = "Ministry of Education & EduVerse Trust",
            amountText = "Up to ₹1,50,000 / Year",
            eligibility = "12th marks 85%+ or NEET/JEE Top 5%",
            deadline = "15th August 2026",
            category = "Merit"
        )
    )

    val initialNotifications = listOf(
        NotificationEntity(
            id = 1,
            title = "NEET UG 2026 State Counselling Started",
            message = "Online registration and choice filling for MBBS, BDS & BAMS seats are now live.",
            category = "Admission Open"
        )
    )

    val initialStudents = emptyList<StudentProfileEntity>()

    val initialAdmissionForms = emptyList<AdmissionFormEntity>()

    val initialCounsellingBookings = emptyList<CounsellingBookingEntity>()
}
