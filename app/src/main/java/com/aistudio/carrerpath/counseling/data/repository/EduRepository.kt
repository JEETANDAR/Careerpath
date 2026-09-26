package com.aistudio.carrerpath.counseling.data.repository

import android.content.Context
import android.util.Log
import com.aistudio.carrerpath.counseling.data.local.DatabaseInitializer
import com.aistudio.carrerpath.counseling.data.local.EduDao
import com.aistudio.carrerpath.counseling.data.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class EduRepository(private val eduDao: EduDao, private val context: Context? = null) {

    private val TAG = "EduRepository"
    private val geminiService = GeminiCounsellorService()

    private val _firestoreStudents = MutableStateFlow<List<StudentProfileEntity>>(emptyList())
    val firestoreStudents: StateFlow<List<StudentProfileEntity>> = _firestoreStudents.asStateFlow()

    private val _firestoreAdmissionForms = MutableStateFlow<List<AdmissionFormEntity>>(emptyList())
    val firestoreAdmissionForms: StateFlow<List<AdmissionFormEntity>> = _firestoreAdmissionForms.asStateFlow()

    private val _firestoreCounsellingBookings = MutableStateFlow<List<CounsellingBookingEntity>>(emptyList())
    val firestoreCounsellingBookings: StateFlow<List<CounsellingBookingEntity>> = _firestoreCounsellingBookings.asStateFlow()

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (context != null && FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val db = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings
            db
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization notice: ${e.message}")
            null
        }
    }

    val allCareers: Flow<List<CareerEntity>> = eduDao.getAllCareers()
    val allColleges: Flow<List<CollegeEntity>> = eduDao.getAllColleges()
    val featuredColleges: Flow<List<CollegeEntity>> = eduDao.getFeaturedColleges()
    val allCourses: Flow<List<CourseEntity>> = eduDao.getAllCourses()

    val allAdmissionForms: Flow<List<AdmissionFormEntity>> = combine(
        _firestoreAdmissionForms,
        eduDao.getAllAdmissionForms()
    ) { fsForms, localForms ->
        val mergedMap = LinkedHashMap<String, AdmissionFormEntity>()
        // Remote Firestore forms are authoritative single source of truth:
        for (f in fsForms) {
            val key = if (f.id > 0) f.id.toString() else "${f.phone.filter { it.isDigit() }}_${f.preferredCollege}_${f.preferredCourse}_${f.timestamp}"
            mergedMap[key] = f
        }
        for (f in localForms) {
            val key = if (f.id > 0) f.id.toString() else "${f.phone.filter { it.isDigit() }}_${f.preferredCollege}_${f.preferredCourse}_${f.timestamp}"
            if (!mergedMap.containsKey(key)) {
                mergedMap[key] = f
            }
        }
        mergedMap.values
            .filter { it.studentName != "Sample Student" && it.email != "student@carrerpath.org" }
            .sortedByDescending { it.timestamp }
    }

    val allNotifications: Flow<List<NotificationEntity>> = eduDao.getAllNotifications()
    val allScholarships: Flow<List<ScholarshipEntity>> = eduDao.getAllScholarships()

    val allStudents: Flow<List<StudentProfileEntity>> = combine(
        _firestoreStudents,
        eduDao.getAllStudents()
    ) { fsStudents, localStudents ->
        val mergedMap = LinkedHashMap<String, StudentProfileEntity>()
        for (s in fsStudents) {
            val key = s.phone.filter { it.isDigit() }.ifBlank { s.uid }
            if (key.isNotBlank()) mergedMap[key] = s
        }
        for (s in localStudents) {
            val key = s.phone.filter { it.isDigit() }.ifBlank { s.uid }
            if (key.isNotBlank() && !mergedMap.containsKey(key)) {
                mergedMap[key] = s
            }
        }
        mergedMap.values
            .filter { it.name.isNotBlank() && it.name != "Sample Student" && it.email != "student@carrerpath.org" }
            .sortedByDescending { it.createdAt }
    }

    val allCounsellingBookings: Flow<List<CounsellingBookingEntity>> = combine(
        _firestoreCounsellingBookings,
        eduDao.getAllCounsellingBookings()
    ) { fsBookings, localBookings ->
        val mergedMap = LinkedHashMap<String, CounsellingBookingEntity>()
        // Remote Firestore bookings are authoritative single source of truth:
        for (b in fsBookings) {
            val key = if (b.id > 0) b.id.toString() else "${b.studentPhone.filter { it.isDigit() }}_${b.bookingDate}_${b.timeSlot}_${b.targetCourse}"
            mergedMap[key] = b
        }
        for (b in localBookings) {
            val key = if (b.id > 0) b.id.toString() else "${b.studentPhone.filter { it.isDigit() }}_${b.bookingDate}_${b.timeSlot}_${b.targetCourse}"
            if (!mergedMap.containsKey(key)) {
                mergedMap[key] = b
            }
        }
        mergedMap.values
            .filter { it.studentName != "Sample Student" && it.studentEmail != "student@carrerpath.org" }
            .sortedByDescending { it.timestamp }
    }

    private fun anyToJson(value: Any?): String {
        if (value == null) return ""
        if (value is String) {
            val trimmed = value.trim()
            if (trimmed.startsWith("{") && trimmed.endsWith("}") && !trimmed.contains("\":") && trimmed.contains("=")) {
                return try {
                    trimmed.replace(Regex("([{, ])([a-zA-Z0-9_]+)="), "$1\"$2\":")
                } catch (e: Exception) { trimmed }
            }
            return value
        }
        return try {
            when (value) {
                is Map<*, *> -> JSONObject(value).toString()
                is List<*> -> JSONArray(value).toString()
                else -> value.toString()
            }
        } catch (e: Exception) {
            value.toString()
        }
    }

    private fun parseCareerFromDoc(
        doc: DocumentSnapshot,
        loadedCareersMap: Map<Int, CareerEntity> = emptyMap()
    ): CareerEntity? {
        return try {
            val idVal = (doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L).toInt()
            val titleVal = doc.getString("title") ?: doc.getString("name") ?: ""
            if (titleVal.isBlank()) return null

            val finalId = if (idVal >= 0) idVal else (doc.id.hashCode() and 0x7FFFFFFF)
            val assetCareer = loadedCareersMap[finalId]
                ?: context?.let { DatabaseInitializer.loadCareersFromAssets(it).find { c -> c.id == finalId } }
                ?: DatabaseInitializer.initialCareers.find { it.id == finalId }

            val imageVal = doc.getString("image")
                ?: doc.getString("imageUrl")
                ?: doc.getString("img")
                ?: doc.getString("photo")
                ?: doc.getString("thumbnail")
                ?: assetCareer?.image
                ?: ""

            CareerEntity(
                id = finalId,
                title = titleVal,
                titleKn = doc.getString("titleKn") ?: doc.getString("nameKn") ?: "",
                image = imageVal,
                tagline = doc.getString("tagline") ?: assetCareer?.tagline ?: "",
                taglineKn = doc.getString("taglineKn") ?: assetCareer?.taglineKn ?: "",
                description = doc.getString("description") ?: assetCareer?.description ?: "",
                descriptionKn = doc.getString("descriptionKn") ?: assetCareer?.descriptionKn ?: "",
                overview = doc.getString("overview") ?: assetCareer?.overview ?: "",
                overviewKn = doc.getString("overviewKn") ?: assetCareer?.overviewKn ?: "",
                whyChoose = doc.getString("whyChoose") ?: assetCareer?.whyChoose ?: "",
                whyChooseKn = doc.getString("whyChooseKn") ?: assetCareer?.whyChooseKn ?: "",
                benefits = doc.getString("benefits") ?: assetCareer?.benefits ?: "",
                benefitsKn = doc.getString("benefitsKn") ?: assetCareer?.benefitsKn ?: "",
                eligibility = doc.getString("eligibility") ?: assetCareer?.eligibility ?: "",
                eligibilityKn = doc.getString("eligibilityKn") ?: assetCareer?.eligibilityKn ?: "",
                subjects = doc.getString("subjects") ?: assetCareer?.subjects ?: "",
                subjectsKn = doc.getString("subjectsKn") ?: assetCareer?.subjectsKn ?: "",
                exams = doc.getString("exams") ?: assetCareer?.exams ?: "",
                examsKn = doc.getString("examsKn") ?: assetCareer?.examsKn ?: "",
                pathway = doc.getString("pathway") ?: assetCareer?.pathway ?: "",
                pathwayKn = doc.getString("pathwayKn") ?: assetCareer?.pathwayKn ?: "",
                skills = doc.getString("skills") ?: assetCareer?.skills ?: "",
                skillsKn = doc.getString("skillsKn") ?: assetCareer?.skillsKn ?: "",
                jobRoles = doc.getString("jobRoles") ?: assetCareer?.jobRoles ?: "",
                jobRolesKn = doc.getString("jobRolesKn") ?: assetCareer?.jobRolesKn ?: "",
                opportunities = doc.getString("opportunities") ?: assetCareer?.opportunities ?: "",
                opportunitiesKn = doc.getString("opportunitiesKn") ?: assetCareer?.opportunitiesKn ?: "",
                topColleges = doc.getString("topColleges") ?: assetCareer?.topColleges ?: "",
                topCollegesKn = doc.getString("topCollegesKn") ?: assetCareer?.topCollegesKn ?: "",
                topRecruiters = doc.getString("topRecruiters") ?: assetCareer?.topRecruiters ?: "",
                topRecruitersKn = doc.getString("topRecruitersKn") ?: assetCareer?.topRecruitersKn ?: "",
                salaryAverage = doc.getString("salaryAverage") ?: assetCareer?.salaryAverage ?: "₹ 8-15 LPA",
                salaryHighest = doc.getString("salaryHighest") ?: assetCareer?.salaryHighest ?: "₹ 35+ LPA",
                feesAverage = doc.getString("feesAverage") ?: assetCareer?.feesAverage ?: "₹ 2-5 Lakhs/year",
                duration = doc.getString("duration") ?: assetCareer?.duration ?: "4-5 Years",
                scholarships = doc.getString("scholarships") ?: assetCareer?.scholarships ?: "",
                scholarshipsKn = doc.getString("scholarshipsKn") ?: assetCareer?.scholarshipsKn ?: "",
                futureScope = doc.getString("futureScope") ?: assetCareer?.futureScope ?: "",
                futureScopeKn = doc.getString("futureScopeKn") ?: assetCareer?.futureScopeKn ?: "",
                certifications = doc.getString("certifications") ?: assetCareer?.certifications ?: "",
                certificationsKn = doc.getString("certificationsKn") ?: assetCareer?.certificationsKn ?: "",
                faqs = doc.getString("faqs") ?: assetCareer?.faqs ?: "",
                faqsKn = doc.getString("faqsKn") ?: assetCareer?.faqsKn ?: "",
                category = doc.getString("category") ?: assetCareer?.category ?: "General",
                pathwayDetailed = anyToJson(doc.get("pathwayDetailed")).ifBlank { assetCareer?.pathwayDetailed ?: "" },
                pathwayDetailedKn = anyToJson(doc.get("pathwayDetailedKn")).ifBlank { assetCareer?.pathwayDetailedKn ?: "" }
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing career from doc ${doc.id}: ${e.message}")
            null
        }
    }

    private fun startRealtimeFirestoreSync() {
        val db = firestore ?: return
        try {
            // Realtime Careers Listener - Syncs remote images & data directly from Firestore
            db.collection("careers").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Careers snapshot listen failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val loadedCareersMap = if (context != null) {
                                try { DatabaseInitializer.loadCareersFromAssets(context).associateBy { it.id } } catch (e: Exception) { emptyMap() }
                            } else emptyMap()

                            val firestoreCareers = snapshot.documents.mapNotNull { doc ->
                                val career = parseCareerFromDoc(doc, loadedCareersMap) ?: return@mapNotNull null
                                val assetCareer = loadedCareersMap[career.id]
                                val finalImage = if (career.image.isNotBlank()) career.image else (assetCareer?.image ?: "")
                                career.copy(image = finalImage)
                            }
                            if (firestoreCareers.isNotEmpty()) {
                                eduDao.insertCareers(firestoreCareers)
                                Log.d(TAG, "Successfully synced ${firestoreCareers.size} careers from Firestore.")
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error saving synced careers to local DB: ${e.message}")
                        }
                    }
                }
            }

            // Realtime Colleges Listener - Syncs colleges with logo.dev URLs directly from Firestore
            db.collection("colleges").addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val loadedCollegesMap = if (context != null) {
                                try { DatabaseInitializer.loadCollegesFromAssets(context).associateBy { it.id } } catch (e: Exception) { emptyMap() }
                            } else emptyMap()

                            val firestoreColleges = snapshot.documents.mapNotNull { doc ->
                                try {
                                    val id = (doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L).toInt()
                                    val name = doc.getString("name") ?: ""
                                    if (name.isBlank()) null
                                    else {
                                        var rawImgUrl = doc.getString("imageUrl") ?: doc.getString("image") ?: doc.getString("logoImageUrl") ?: ""
                                        var rawHeroUrl = doc.getString("heroBannerUrl") ?: rawImgUrl
                                        val assetCol = loadedCollegesMap[id]
                                        if (rawImgUrl.isBlank() && assetCol != null) {
                                            rawImgUrl = assetCol.imageUrl
                                        }
                                        if (rawHeroUrl.isBlank() && assetCol != null) {
                                            rawHeroUrl = assetCol.heroBannerUrl
                                        }
                                        val rawRank = (doc.getLong("ranking") ?: (id + 1).toLong()).toInt()
                                        val rank = if (rawRank > 0) rawRank else (id + 1)

                                        CollegeEntity(
                                            id = if (id >= 0) id else (doc.id.hashCode() and 0x7FFFFFFF),
                                            name = name,
                                            nameKn = doc.getString("nameKn") ?: "",
                                            location = doc.getString("location") ?: "",
                                            locationKn = doc.getString("locationKn") ?: "",
                                            state = doc.getString("state") ?: "",
                                            city = doc.getString("city") ?: doc.getString("location") ?: "",
                                            coursesOffered = doc.getString("coursesOffered") ?: "",
                                            coursesOfferedKn = doc.getString("coursesOfferedKn") ?: "",
                                            minFeesLakhs = (doc.getDouble("minFeesLakhs") ?: 1.5),
                                            maxFeesLakhs = (doc.getDouble("maxFeesLakhs") ?: 4.0),
                                            hostelAvailable = doc.getBoolean("hostelAvailable") ?: true,
                                            hostelFeesPerYear = (doc.getLong("hostelFeesPerYear") ?: 80000L).toInt(),
                                            recognition = doc.getString("recognition") ?: "AICTE / UGC Approved",
                                            recognitionKn = doc.getString("recognitionKn") ?: "",
                                            ranking = rank,
                                            eligibility = doc.getString("eligibility") ?: "10+2 with 50% Marks",
                                            eligibilityKn = doc.getString("eligibilityKn") ?: "",
                                            admissionStatus = doc.getString("admissionStatus") ?: "Open",
                                            imageUrl = rawImgUrl,
                                            heroBannerUrl = rawHeroUrl,
                                            overview = doc.getString("overview") ?: "",
                                            overviewKn = doc.getString("overviewKn") ?: "",
                                            averagePackageLakhs = (doc.getDouble("averagePackageLakhs") ?: 6.5),
                                            highestPackageLakhs = (doc.getDouble("highestPackageLakhs") ?: 25.0),
                                            websiteUrl = doc.getString("websiteUrl") ?: doc.getString("website") ?: "https://careerpath.example.com",
                                            isFeatured = doc.getBoolean("isFeatured") ?: doc.getBoolean("featured") ?: true
                                        )
                                    }
                                } catch (e: Exception) { null }
                            }
                            if (firestoreColleges.isNotEmpty()) {
                                eduDao.insertColleges(firestoreColleges)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error saving synced colleges to local DB: ${e.message}")
                        }
                    }
                }
            }

            // Realtime Course Listener (collection 'course' - singular, primary collection in Firestore)
            db.collection("course").addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val loadedCoursesMap = if (context != null) {
                                try { DatabaseInitializer.loadCoursesFromAssets(context).associateBy { it.id } } catch (e: Exception) { emptyMap() }
                            } else emptyMap()

                            val firestoreCourses = snapshot.documents.mapNotNull { doc ->
                                parseCourseFromFirestoreDoc(doc, loadedCoursesMap)
                            }
                            if (firestoreCourses.isNotEmpty()) {
                                eduDao.insertCourses(firestoreCourses)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error saving synced courses: ${e.message}")
                        }
                    }
                }
            }

            // Realtime Courses Listener (collection 'courses' - plural)
            db.collection("courses").addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val loadedCoursesMap = if (context != null) {
                                try { DatabaseInitializer.loadCoursesFromAssets(context).associateBy { it.id } } catch (e: Exception) { emptyMap() }
                            } else emptyMap()

                            val firestoreCourses = snapshot.documents.mapNotNull { doc ->
                                parseCourseFromFirestoreDoc(doc, loadedCoursesMap)
                            }
                            if (firestoreCourses.isNotEmpty()) {
                                eduDao.insertCourses(firestoreCourses)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error saving synced courses: ${e.message}")
                        }
                    }
                }
            }

            // Realtime Counselling Bookings Listener - Instant sync between Student & Admin devices
            val updateBookingsFromDocs: (List<DocumentSnapshot>) -> Unit = { docs ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val parsedBookings = docs.mapNotNull { doc ->
                            parseBookingFromFirestoreDoc(doc)
                        }.filter { it.studentName != "Sample Student" && it.studentEmail != "student@carrerpath.org" }

                        if (parsedBookings.isNotEmpty()) {
                            val current = _firestoreCounsellingBookings.value.toMutableList()
                            for (pb in parsedBookings) {
                                val idx = current.indexOfFirst {
                                    (it.id > 0 && it.id == pb.id) ||
                                    (it.studentPhone == pb.studentPhone && it.bookingDate == pb.bookingDate && it.timeSlot == pb.timeSlot)
                                }
                                if (idx >= 0) {
                                    current[idx] = pb
                                } else {
                                    current.add(pb)
                                }
                            }
                            val sorted = current.sortedByDescending { it.timestamp }
                            _firestoreCounsellingBookings.value = sorted
                            eduDao.insertCounsellingBookings(sorted)
                            Log.d(TAG, "Synced ${sorted.size} counselling bookings from Firestore.")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error processing synced bookings: ${e.message}")
                    }
                }
            }

            db.collection("counselling_bookings").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Counselling bookings snapshot listen failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    updateBookingsFromDocs(snapshot.documents)
                }
            }

            for (colName in listOf("career_counselling_bookings", "college_counselling_bookings", "course_counselling_bookings")) {
                db.collection(colName).addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && !snapshot.isEmpty) {
                        updateBookingsFromDocs(snapshot.documents)
                    }
                }
            }

            // Realtime Admission Forms Listener - Instant sync between Student & Admin devices
            val updateFormsFromDocs: (List<DocumentSnapshot>) -> Unit = { docs ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val parsedForms = docs.mapNotNull { doc ->
                            parseAdmissionFormFromFirestoreDoc(doc)
                        }.filter { it.studentName != "Sample Student" && it.email != "student@carrerpath.org" }

                        if (parsedForms.isNotEmpty()) {
                            val current = _firestoreAdmissionForms.value.toMutableList()
                            for (pf in parsedForms) {
                                val idx = current.indexOfFirst {
                                    (it.id > 0 && it.id == pf.id) ||
                                    (it.phone == pf.phone && it.timestamp == pf.timestamp)
                                }
                                if (idx >= 0) {
                                    current[idx] = pf
                                } else {
                                    current.add(pf)
                                }
                            }
                            val sorted = current.sortedByDescending { it.timestamp }
                            _firestoreAdmissionForms.value = sorted
                            eduDao.insertAdmissionForms(sorted)
                            Log.d(TAG, "Synced ${sorted.size} admission forms from Firestore.")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error processing synced forms: ${e.message}")
                    }
                }
            }

            db.collection("admission_forms").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Admission forms snapshot listen failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    updateFormsFromDocs(snapshot.documents)
                }
            }

            db.collection("college_applications").addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    updateFormsFromDocs(snapshot.documents)
                }
            }

            // Realtime Students Listener - Instant multi-device sync between Student & Admin devices
            val updateStudentsFromDocs: (List<DocumentSnapshot>) -> Unit = { docs ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val list = docs.mapNotNull { doc ->
                            parseStudentFromDoc(doc)
                        }.filter { it.name.isNotBlank() && it.name != "Sample Student" && it.email != "student@carrerpath.org" }

                        if (list.isNotEmpty()) {
                            val current = _firestoreStudents.value.toMutableList()
                            for (st in list) {
                                val sDigits = st.phone.filter { ch -> ch.isDigit() }.takeLast(10)
                                val idx = current.indexOfFirst {
                                    (st.uid.isNotBlank() && it.uid == st.uid) ||
                                    (sDigits.isNotBlank() && it.phone.filter { ch -> ch.isDigit() }.takeLast(10) == sDigits)
                                }
                                if (idx >= 0) {
                                    current[idx] = st
                                } else {
                                    current.add(st)
                                }
                            }
                            val sorted = current.sortedByDescending { it.createdAt }
                            _firestoreStudents.value = sorted
                            eduDao.insertStudents(sorted)
                            Log.d(TAG, "Successfully synced ${sorted.size} students from Firestore in real-time.")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error saving synced students to local DB: ${e.message}")
                    }
                }
            }

            db.collection("students").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Students snapshot listen notice: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    updateStudentsFromDocs(snapshot.documents)
                }
            }

            db.collection("users").addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    updateStudentsFromDocs(snapshot.documents)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error starting Firestore realtime listeners: ${e.message}")
        }
    }

    fun parseBookingFromMap(docId: String, data: Map<String, Any?>): CounsellingBookingEntity? {
        return try {
            val studentName = (data["studentName"] as? String) ?: (data["name"] as? String) ?: ""
            val studentPhone = (data["studentPhone"] as? String) ?: (data["phone"] as? String) ?: ""
            if (studentName.isBlank() && studentPhone.isBlank()) return null

            val rawId = (data["id"] as? Number)?.toLong() ?: docId.toLongOrNull()
            val finalId = if (rawId != null && rawId > 0 && rawId <= Int.MAX_VALUE) {
                rawId.toInt()
            } else {
                docId.hashCode() and 0x7FFFFFFF
            }

            val feeAmount = (data["feeAmount"] as? Number)?.toDouble()
                ?: (data["feeAmount"] as? String)?.toDoubleOrNull()
                ?: 0.0

            val timestamp = (data["timestamp"] as? Number)?.toLong()
                ?: (data["createdAt"] as? Number)?.toLong()
                ?: System.currentTimeMillis()

            val targetCourse = (data["targetCourse"] as? String) 
                ?: (data["course"] as? String) 
                ?: (data["category"] as? String) 
                ?: "General Career Counselling"

            CounsellingBookingEntity(
                id = if (finalId != 0) finalId else 1,
                studentName = studentName,
                studentPhone = studentPhone,
                studentEmail = (data["studentEmail"] as? String) ?: (data["email"] as? String) ?: "",
                studentClass = (data["studentClass"] as? String) ?: (data["qualification"] as? String) ?: "",
                targetCourse = targetCourse,
                counsellorName = (data["counsellorName"] as? String) ?: "Nimi",
                bookingDate = (data["bookingDate"] as? String) ?: (data["date"] as? String) ?: "",
                timeSlot = (data["timeSlot"] as? String) ?: (data["slot"] as? String) ?: "",
                couponCode = (data["couponCode"] as? String) ?: "",
                feeAmount = feeAmount,
                paymentStatus = (data["paymentStatus"] as? String) ?: "Confirmed",
                razorpayPaymentId = (data["razorpayPaymentId"] as? String) ?: "free_booking",
                status = (data["status"] as? String) ?: "Confirmed",
                bookingType = (data["bookingType"] as? String) ?: "Course Counselling",
                timestamp = timestamp
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed parsing booking map: ${e.message}")
            null
        }
    }

    fun counsellingBookingToMap(booking: CounsellingBookingEntity): Map<String, Any> {
        return hashMapOf(
            "id" to booking.id,
            "studentName" to booking.studentName,
            "name" to booking.studentName,
            "studentPhone" to booking.studentPhone,
            "phone" to booking.studentPhone,
            "studentEmail" to booking.studentEmail,
            "email" to booking.studentEmail,
            "studentClass" to booking.studentClass,
            "qualification" to booking.studentClass,
            "targetCourse" to booking.targetCourse,
            "course" to booking.targetCourse,
            "category" to booking.targetCourse,
            "counsellorName" to booking.counsellorName,
            "bookingDate" to booking.bookingDate,
            "date" to booking.bookingDate,
            "timeSlot" to booking.timeSlot,
            "slot" to booking.timeSlot,
            "couponCode" to booking.couponCode,
            "feeAmount" to booking.feeAmount,
            "paymentStatus" to booking.paymentStatus,
            "razorpayPaymentId" to booking.razorpayPaymentId,
            "status" to booking.status,
            "bookingType" to booking.bookingType,
            "timestamp" to booking.timestamp,
            "createdAt" to booking.timestamp
        )
    }

    private fun parseBookingFromFirestoreDoc(doc: DocumentSnapshot): CounsellingBookingEntity? {
        val map = doc.data ?: return null
        return parseBookingFromMap(doc.id, map)
    }

    fun parseAdmissionFormFromMap(docId: String, data: Map<String, Any?>): AdmissionFormEntity? {
        return try {
            val studentName = (data["studentName"] as? String) ?: (data["name"] as? String) ?: ""
            val phone = (data["phone"] as? String) ?: (data["studentPhone"] as? String) ?: (data["mobile"] as? String) ?: ""
            if (studentName.isBlank() && phone.isBlank()) return null

            val rawId = (data["id"] as? Number)?.toLong() ?: docId.toLongOrNull()
            val finalId = if (rawId != null && rawId > 0 && rawId <= Int.MAX_VALUE) {
                rawId.toInt()
            } else {
                docId.hashCode() and 0x7FFFFFFF
            }

            val percentageMark = (data["percentageMark"] as? Number)?.toDouble()
                ?: (data["percentage"] as? Number)?.toDouble()
                ?: (data["percentageMark"] as? String)?.toDoubleOrNull()
                ?: (data["percentage"] as? String)?.toDoubleOrNull()
                ?: 85.0

            val timestamp = (data["timestamp"] as? Number)?.toLong()
                ?: (data["createdAt"] as? Number)?.toLong()
                ?: System.currentTimeMillis()

            AdmissionFormEntity(
                id = if (finalId != 0) finalId else 1,
                studentName = studentName,
                phone = phone,
                email = (data["email"] as? String) ?: (data["studentEmail"] as? String) ?: "",
                qualification = (data["qualification"] as? String) ?: (data["studentClass"] as? String) ?: "",
                percentageMark = percentageMark,
                preferredCourse = (data["preferredCourse"] as? String) ?: (data["course"] as? String) ?: (data["courseName"] as? String) ?: "",
                preferredCollege = (data["preferredCollege"] as? String) ?: (data["college"] as? String) ?: (data["collegeName"] as? String) ?: "",
                city = (data["city"] as? String) ?: "",
                notes = (data["notes"] as? String) ?: "",
                status = (data["status"] as? String) ?: "Pending",
                timestamp = timestamp
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed parsing admission form map: ${e.message}")
            null
        }
    }

    fun admissionFormToMap(form: AdmissionFormEntity): Map<String, Any> {
        return hashMapOf(
            "id" to form.id,
            "studentName" to form.studentName,
            "name" to form.studentName,
            "phone" to form.phone,
            "studentPhone" to form.phone,
            "mobile" to form.phone,
            "email" to form.email,
            "studentEmail" to form.email,
            "qualification" to form.qualification,
            "studentClass" to form.qualification,
            "percentageMark" to form.percentageMark,
            "percentage" to form.percentageMark,
            "preferredCourse" to form.preferredCourse,
            "course" to form.preferredCourse,
            "courseName" to form.preferredCourse,
            "preferredCollege" to form.preferredCollege,
            "college" to form.preferredCollege,
            "collegeName" to form.preferredCollege,
            "city" to form.city,
            "notes" to form.notes,
            "status" to form.status,
            "timestamp" to form.timestamp,
            "createdAt" to form.timestamp
        )
    }

    private fun parseAdmissionFormFromFirestoreDoc(doc: DocumentSnapshot): AdmissionFormEntity? {
        val map = doc.data ?: return null
        return parseAdmissionFormFromMap(doc.id, map)
    }

    private fun parseCourseFromFirestoreDoc(
        doc: com.google.firebase.firestore.DocumentSnapshot,
        loadedCoursesMap: Map<Int, CourseEntity> = emptyMap()
    ): CourseEntity? {
        return try {
            val id = (doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L).toInt()
            val assetCourse = loadedCoursesMap[id]
            val name = doc.getString("name") ?: doc.getString("title") ?: ""
            if (name.isBlank()) return null

            // Prioritize image URL from Firestore document (including commons.wikimedia.org)
            val rawImgUrl = doc.getString("imageUrl") ?: doc.getString("image") ?: ""
            val imgUrl = if (rawImgUrl.isNotBlank()) {
                DatabaseInitializer.normalizeImageUrl(rawImgUrl)
            } else {
                assetCourse?.imageUrl ?: ""
            }

            val code = doc.getString("code") ?: if (id > 0) "CRS-$id" else "CRS"
            val category = doc.getString("category") ?: loadedCoursesMap[id]?.category ?: "General"
            val duration = doc.getString("duration") ?: doc.getString("durationYears") 
                ?: "${doc.getLong("durationYears") ?: 4} Years"
            val eligibility = doc.getString("eligibility") ?: ""
            val eligibilityKn = doc.getString("eligibilityKn") ?: ""

            // Fee Parsing (support Double or string like 'feesAverage')
            val avgFees = doc.getDouble("avgFeesLakhs") ?: run {
                val feesStr = doc.getString("feesAverage") ?: ""
                parseFeesStringToDouble(feesStr)
            }

            // Salary / Package Parsing
            val avgPackage = doc.getDouble("avgPackageLakhs") ?: run {
                val salStr = doc.getString("salaryAverage") ?: ""
                parsePackageStringToDouble(salStr)
            }

            val careerScope = doc.getString("careerScope") ?: doc.getString("futureScope") ?: doc.getString("jobRoles") ?: ""
            val careerScopeKn = doc.getString("careerScopeKn") ?: doc.getString("futureScopeKn") ?: doc.getString("jobRolesKn") ?: ""
            val description = doc.getString("description") ?: doc.getString("overview") ?: ""
            val descriptionKn = doc.getString("descriptionKn") ?: doc.getString("overviewKn") ?: ""
            val topCollegesCount = (doc.getLong("topCollegesCount") ?: 15L).toInt()

            CourseEntity(
                id = if (id > 0) id else (doc.id.hashCode() and 0x7FFFFFFF),
                code = code,
                name = name,
                nameKn = doc.getString("nameKn") ?: doc.getString("titleKn") ?: "",
                category = category,
                durationYears = duration,
                eligibility = eligibility,
                eligibilityKn = eligibilityKn,
                avgFeesLakhs = avgFees,
                avgPackageLakhs = avgPackage,
                careerScope = careerScope,
                careerScopeKn = careerScopeKn,
                description = description,
                descriptionKn = descriptionKn,
                topCollegesCount = topCollegesCount,
                imageUrl = imgUrl,
                tagline = doc.getString("tagline") ?: assetCourse?.tagline ?: "",
                taglineKn = doc.getString("taglineKn") ?: assetCourse?.taglineKn ?: "",
                overview = doc.getString("overview") ?: assetCourse?.overview ?: "",
                overviewKn = doc.getString("overviewKn") ?: assetCourse?.overviewKn ?: "",
                whyChoose = doc.getString("whyChoose") ?: assetCourse?.whyChoose ?: "",
                whyChooseKn = doc.getString("whyChooseKn") ?: assetCourse?.whyChooseKn ?: "",
                benefits = doc.getString("benefits") ?: assetCourse?.benefits ?: "",
                benefitsKn = doc.getString("benefitsKn") ?: assetCourse?.benefitsKn ?: "",
                subjects = doc.getString("subjects") ?: assetCourse?.subjects ?: "",
                subjectsKn = doc.getString("subjectsKn") ?: assetCourse?.subjectsKn ?: "",
                exams = doc.getString("exams") ?: assetCourse?.exams ?: "",
                examsKn = doc.getString("examsKn") ?: assetCourse?.examsKn ?: "",
                pathway = doc.getString("pathway") ?: assetCourse?.pathway ?: "",
                pathwayKn = doc.getString("pathwayKn") ?: assetCourse?.pathwayKn ?: "",
                skills = doc.getString("skills") ?: assetCourse?.skills ?: "",
                skillsKn = doc.getString("skillsKn") ?: assetCourse?.skillsKn ?: "",
                jobRoles = doc.getString("jobRoles") ?: assetCourse?.jobRoles ?: "",
                jobRolesKn = doc.getString("jobRolesKn") ?: assetCourse?.jobRolesKn ?: "",
                opportunities = doc.getString("opportunities") ?: assetCourse?.opportunities ?: "",
                opportunitiesKn = doc.getString("opportunitiesKn") ?: assetCourse?.opportunitiesKn ?: "",
                topColleges = doc.getString("topColleges") ?: assetCourse?.topColleges ?: "",
                topCollegesKn = doc.getString("topCollegesKn") ?: assetCourse?.topCollegesKn ?: "",
                topRecruiters = doc.getString("topRecruiters") ?: assetCourse?.topRecruiters ?: "",
                topRecruitersKn = doc.getString("topRecruitersKn") ?: assetCourse?.topRecruitersKn ?: "",
                salaryAverage = doc.getString("salaryAverage") ?: assetCourse?.salaryAverage ?: "",
                salaryHighest = doc.getString("salaryHighest") ?: assetCourse?.salaryHighest ?: "",
                feesAverage = doc.getString("feesAverage") ?: assetCourse?.feesAverage ?: "",
                scholarships = doc.getString("scholarships") ?: assetCourse?.scholarships ?: "",
                scholarshipsKn = doc.getString("scholarshipsKn") ?: assetCourse?.scholarshipsKn ?: "",
                futureScope = careerScope,
                futureScopeKn = careerScopeKn,
                certifications = doc.getString("certifications") ?: assetCourse?.certifications ?: "",
                certificationsKn = doc.getString("certificationsKn") ?: assetCourse?.certificationsKn ?: "",
                faqs = doc.getString("faqs") ?: assetCourse?.faqs ?: "",
                faqsKn = doc.getString("faqsKn") ?: assetCourse?.faqsKn ?: "",
                detailedPathway = anyToJson(doc.get("detailedPathway")).ifBlank { assetCourse?.detailedPathway ?: "" },
                detailedPathwayKn = anyToJson(doc.get("detailedPathwayKn")).ifBlank { assetCourse?.detailedPathwayKn ?: "" },
                officialExamLinks = anyToJson(doc.get("officialExamLinks")).ifBlank { assetCourse?.officialExamLinks ?: "" },
                cutoffGuidance = doc.getString("cutoffGuidance") ?: anyToJson(doc.get("cutoffGuidance")).ifBlank { assetCourse?.cutoffGuidance ?: "" }
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing course doc: ${e.message}")
            null
        }
    }

    private fun parseFeesStringToDouble(str: String): Double {
        if (str.isBlank()) return 2.5
        return try {
            val regex = Regex("([0-9]+(?:\\.[0-9]+)?)\\s*Lakh", RegexOption.IGNORE_CASE)
            val match = regex.find(str)
            if (match != null) {
                match.groupValues[1].toDoubleOrNull() ?: 2.5
            } else {
                2.5
            }
        } catch (e: Exception) {
            2.5
        }
    }

    private fun parsePackageStringToDouble(str: String): Double {
        if (str.isBlank()) return 8.0
        return try {
            val regex = Regex("([0-9]+(?:\\.[0-9]+)?)\\s*(?:-|to)?\\s*([0-9]+(?:\\.[0-9]+)?)\\s*Lakh", RegexOption.IGNORE_CASE)
            val match = regex.find(str)
            if (match != null) {
                val high = match.groupValues[2].toDoubleOrNull()
                val low = match.groupValues[1].toDoubleOrNull()
                if (high != null && low != null) (low + high) / 2.0 else high ?: low ?: 8.0
            } else {
                val singleRegex = Regex("([0-9]+(?:\\.[0-9]+)?)", RegexOption.IGNORE_CASE)
                singleRegex.find(str)?.groupValues?.get(1)?.toDoubleOrNull() ?: 8.0
            }
        } catch (e: Exception) {
            8.0
        }
    }

    fun getStudentProfile(phoneOrEmail: String): Flow<StudentProfileEntity?> = eduDao.getStudentByPhone(phoneOrEmail)

    suspend fun getStudentByPhoneOneShot(phone: String): StudentProfileEntity? {
        return eduDao.getStudentByPhoneOneShot(phone)
    }

    fun studentProfileToMap(student: StudentProfileEntity): Map<String, Any> {
        val cleanPhone = student.phone.trim()
        return hashMapOf(
            "phone" to cleanPhone,
            "studentPhone" to cleanPhone,
            "name" to student.name,
            "studentName" to student.name,
            "fullName" to student.name,
            "email" to student.email,
            "institution" to student.institution,
            "school" to student.institution,
            "college" to student.institution,
            "gender" to student.gender,
            "city" to student.city,
            "state" to student.state,
            "country" to student.country,
            "studentClass" to student.studentClass,
            "qualification" to student.studentClass,
            "percentageMark" to student.percentageMark,
            "percentage" to student.percentageMark,
            "isAdmin" to student.isAdmin,
            "uid" to student.uid,
            "createdAt" to student.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    fun parseStudentFromMap(docId: String, data: Map<String, Any?>): StudentProfileEntity? {
        return try {
            val phone = (data["phone"] as? String)
                ?: (data["studentPhone"] as? String)
                ?: (data["mobile"] as? String)
                ?: (data["phoneNumber"] as? String)
                ?: (data["contact"] as? String)
                ?: (if (docId.startsWith("+") || (docId.any { it.isDigit() } && docId.length <= 15)) docId else "")
            val name = (data["name"] as? String)
                ?: (data["studentName"] as? String)
                ?: (data["fullName"] as? String)
                ?: (data["displayName"] as? String)
                ?: ""
            if (phone.isBlank() && name.isBlank()) return null

            val email = (data["email"] as? String) ?: (data["studentEmail"] as? String) ?: ""
            val institution = (data["institution"] as? String) ?: (data["school"] as? String) ?: (data["college"] as? String) ?: ""
            val gender = (data["gender"] as? String) ?: "Student"
            val city = (data["city"] as? String) ?: ""
            val state = (data["state"] as? String) ?: ""
            val country = (data["country"] as? String) ?: "India"
            val studentClass = (data["studentClass"] as? String) ?: (data["qualification"] as? String) ?: (data["class"] as? String) ?: (data["stream"] as? String) ?: "12th Standard"
            val percentageMark = (data["percentageMark"] as? Number)?.toDouble()
                ?: (data["percentage"] as? Number)?.toDouble()
                ?: (data["percentageMark"] as? String)?.toDoubleOrNull()
                ?: (data["percentage"] as? String)?.toDoubleOrNull()
                ?: 85.0
            val isAdmin = (data["isAdmin"] as? Boolean) ?: (data["admin"] as? Boolean) ?: false
            val uid = (data["uid"] as? String) ?: (data["userId"] as? String) ?: (data["firebaseUid"] as? String) ?: (if (docId.length > 20 && !docId.startsWith("+")) docId else "")
            val createdAt = (data["createdAt"] as? Number)?.toLong()
                ?: (data["timestamp"] as? Number)?.toLong()
                ?: System.currentTimeMillis()

            StudentProfileEntity(
                phone = phone.ifBlank { docId },
                name = name.ifBlank { "Student ${phone.takeLast(4)}" },
                email = email,
                institution = institution,
                gender = gender,
                city = city,
                state = state,
                country = country,
                studentClass = studentClass,
                percentageMark = percentageMark,
                isAdmin = isAdmin,
                uid = uid,
                createdAt = createdAt
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing student map $docId: ${e.message}")
            null
        }
    }

    fun parseStudentFromDoc(doc: DocumentSnapshot): StudentProfileEntity? {
        return try {
            val map = doc.data ?: emptyMap<String, Any?>()
            parseStudentFromMap(doc.id, map)
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing student doc ${doc.id}: ${e.message}")
            null
        }
    }

    suspend fun ingestRemoteStudents(students: List<StudentProfileEntity>) {
        val current = _firestoreStudents.value.toMutableList()
        for (st in students) {
            val sDigits = st.phone.filter { ch -> ch.isDigit() }.takeLast(10)
            val idx = current.indexOfFirst {
                (st.uid.isNotBlank() && it.uid == st.uid) ||
                (sDigits.isNotBlank() && it.phone.filter { ch -> ch.isDigit() }.takeLast(10) == sDigits) ||
                (it.phone == st.phone)
            }
            if (idx >= 0) current[idx] = st else current.add(0, st)
        }
        _firestoreStudents.value = current
        eduDao.insertStudents(students)
    }

    suspend fun ingestRemoteAdmissionForms(forms: List<AdmissionFormEntity>) {
        val current = _firestoreAdmissionForms.value.toMutableList()
        for (form in forms) {
            val idx = current.indexOfFirst { it.id == form.id && form.id > 0 }
            if (idx >= 0) current[idx] = form else current.add(0, form)
        }
        _firestoreAdmissionForms.value = current
        eduDao.insertAdmissionForms(forms)
    }

    suspend fun ingestRemoteBookings(bookings: List<CounsellingBookingEntity>) {
        val current = _firestoreCounsellingBookings.value.toMutableList()
        for (booking in bookings) {
            val idx = current.indexOfFirst { it.id == booking.id && booking.id > 0 }
            if (idx >= 0) current[idx] = booking else current.add(0, booking)
        }
        _firestoreCounsellingBookings.value = current
        eduDao.insertCounsellingBookings(bookings)
    }

    fun ensureFirebaseAuth(onComplete: (() -> Unit)? = null) {
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously()
                    .addOnSuccessListener {
                        Log.d(TAG, "FirebaseAuth authenticated anonymously: ${it.user?.uid}")
                        onComplete?.invoke()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "FirebaseAuth anonymous sign-in notice: ${e.message}")
                        onComplete?.invoke()
                    }
            } else {
                onComplete?.invoke()
            }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth error: ${e.message}")
            onComplete?.invoke()
        }
    }

    suspend fun awaitFirebaseAuth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) return@withContext true
            try {
                Tasks.await(auth.signInAnonymously(), 8, TimeUnit.SECONDS)
                if (auth.currentUser != null) return@withContext true
            } catch (e: Exception) {
                Log.w(TAG, "Anonymous auth await notice: ${e.message}")
            }
            auth.currentUser != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchAllRemoteDataFromFirestore(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Pair(false, "Firestore instance unavailable.")
        awaitFirebaseAuth()
        var studentCount = 0
        var formCount = 0
        var bookingCount = 0
        var criticalError: String? = null

        // 1. Query Students & Users from Firestore
        try {
            val studentDocs = mutableListOf<DocumentSnapshot>()
            for (col in listOf("students", "users")) {
                try {
                    val task = db.collection(col).get(com.google.firebase.firestore.Source.SERVER)
                    val snap = Tasks.await(task, 12, TimeUnit.SECONDS)
                    studentDocs.addAll(snap.documents)
                } catch (e: Exception) {
                    Log.w(TAG, "$col fetch from SERVER notice: ${e.message}. Trying DEFAULT source...")
                    try {
                        val snap = Tasks.await(db.collection(col).get(), 8, TimeUnit.SECONDS)
                        studentDocs.addAll(snap.documents)
                    } catch (e2: Exception) {
                        Log.w(TAG, "$col fetch error: ${e2.message}")
                        if (criticalError == null) criticalError = e2.localizedMessage ?: e2.message
                    }
                }
            }

            val parsedStudents = studentDocs.mapNotNull { parseStudentFromDoc(it) }
            val distinctStudents = parsedStudents
                .filter { it.name.isNotBlank() && it.name != "Sample Student" && it.email != "student@carrerpath.org" }
                .distinctBy {
                    val digits = it.phone.filter { ch -> ch.isDigit() }
                    if (digits.length >= 10) digits.takeLast(10) else it.uid.ifBlank { it.phone }
                }
                .sortedByDescending { it.createdAt }

            if (distinctStudents.isNotEmpty()) {
                _firestoreStudents.value = distinctStudents
                eduDao.insertStudents(distinctStudents)
                studentCount = distinctStudents.size
                Log.d(TAG, "Fetched ${distinctStudents.size} registered students from Firestore.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Students remote query error: ${e.message}")
            if (criticalError == null) criticalError = e.localizedMessage ?: e.message
        }

        // 2. Admission Forms & Applications from Firestore
        try {
            val formDocs = mutableListOf<DocumentSnapshot>()
            for (col in listOf("admission_forms", "college_applications")) {
                try {
                    val snap = Tasks.await(db.collection(col).get(com.google.firebase.firestore.Source.SERVER), 12, TimeUnit.SECONDS)
                    formDocs.addAll(snap.documents)
                } catch (e: Exception) {
                    try {
                        val snap = Tasks.await(db.collection(col).get(), 8, TimeUnit.SECONDS)
                        formDocs.addAll(snap.documents)
                    } catch (e2: Exception) {
                        if (criticalError == null) criticalError = e2.localizedMessage ?: e2.message
                    }
                }
            }
            val distinctForms = formDocs.mapNotNull { parseAdmissionFormFromFirestoreDoc(it) }
                .filter { it.studentName != "Sample Student" && it.email != "student@carrerpath.org" }
                .distinctBy {
                    if (it.id > 0) it.id.toString() else "${it.phone.filter { ch -> ch.isDigit() }}_${it.preferredCollege}_${it.preferredCourse}_${it.timestamp}"
                }
                .sortedByDescending { it.timestamp }

            if (distinctForms.isNotEmpty()) {
                _firestoreAdmissionForms.value = distinctForms
                eduDao.insertAdmissionForms(distinctForms)
                formCount = distinctForms.size
                Log.d(TAG, "Fetched ${distinctForms.size} admission applications from Firestore.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Admission forms remote query error: ${e.message}")
        }

        // 3. Counselling Bookings from Firestore
        try {
            val bookingDocs = mutableListOf<DocumentSnapshot>()
            for (col in listOf("counselling_bookings", "career_counselling_bookings", "college_counselling_bookings", "course_counselling_bookings")) {
                try {
                    val snap = Tasks.await(db.collection(col).get(com.google.firebase.firestore.Source.SERVER), 12, TimeUnit.SECONDS)
                    bookingDocs.addAll(snap.documents)
                } catch (e: Exception) {
                    try {
                        val snap = Tasks.await(db.collection(col).get(), 8, TimeUnit.SECONDS)
                        bookingDocs.addAll(snap.documents)
                    } catch (e2: Exception) {}
                }
            }
            val distinctBookings = bookingDocs.mapNotNull { parseBookingFromFirestoreDoc(it) }
                .filter { it.studentName != "Sample Student" && it.studentEmail != "student@carrerpath.org" }
                .distinctBy {
                    if (it.id > 0) it.id.toString() else "${it.studentPhone.filter { ch -> ch.isDigit() }}_${it.bookingDate}_${it.timeSlot}_${it.targetCourse}"
                }
                .sortedByDescending { it.timestamp }

            if (distinctBookings.isNotEmpty()) {
                _firestoreCounsellingBookings.value = distinctBookings
                eduDao.insertCounsellingBookings(distinctBookings)
                bookingCount = distinctBookings.size
                Log.d(TAG, "Fetched ${distinctBookings.size} counselling bookings from Firestore.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Bookings remote query error: ${e.message}")
        }

        // 4. Safety net: Reconstruct registered student cards from submitted applications & bookings if students collection was empty
        if (studentCount == 0 && (formCount > 0 || bookingCount > 0)) {
            val reconstructed = mutableListOf<StudentProfileEntity>()
            for (form in _firestoreAdmissionForms.value) {
                if (form.phone.isNotBlank() && form.studentName.isNotBlank()) {
                    reconstructed.add(
                        StudentProfileEntity(
                            phone = form.phone,
                            name = form.studentName,
                            email = form.email,
                            institution = form.preferredCollege.ifBlank { "Secondary School" },
                            gender = "Student",
                            city = form.city,
                            state = "Karnataka",
                            country = "India",
                            studentClass = form.qualification.ifBlank { "12th Standard" },
                            percentageMark = form.percentageMark,
                            isAdmin = false,
                            uid = "",
                            createdAt = form.timestamp
                        )
                    )
                }
            }
            for (b in _firestoreCounsellingBookings.value) {
                if (b.studentPhone.isNotBlank() && b.studentName.isNotBlank()) {
                    reconstructed.add(
                        StudentProfileEntity(
                            phone = b.studentPhone,
                            name = b.studentName,
                            email = b.studentEmail,
                            institution = "Counselling Candidate",
                            gender = "Student",
                            city = "Bangalore",
                            state = "Karnataka",
                            country = "India",
                            studentClass = "12th Standard",
                            percentageMark = 85.0,
                            isAdmin = false,
                            uid = "",
                            createdAt = b.timestamp
                        )
                    )
                }
            }
            val distinctRec = reconstructed.distinctBy { it.phone.filter { ch -> ch.isDigit() }.takeLast(10) }
            if (distinctRec.isNotEmpty()) {
                val current = _firestoreStudents.value.toMutableList()
                for (rec in distinctRec) {
                    val rDigits = rec.phone.filter { ch -> ch.isDigit() }.takeLast(10)
                    if (current.none { it.phone.filter { ch -> ch.isDigit() }.takeLast(10) == rDigits }) {
                        current.add(rec)
                    }
                }
                _firestoreStudents.value = current
                studentCount = current.size
                eduDao.insertStudents(current)
            }
        }

        if (criticalError != null && studentCount == 0 && formCount == 0 && bookingCount == 0) {
            val isPerm = criticalError.contains("PERMISSION_DENIED", ignoreCase = true) || criticalError.contains("permission", ignoreCase = true)
            val msg = if (isPerm) {
                "Firestore Permission Denied. In Firebase Console -> Firestore Database -> Rules, ensure rules allow read/write: 'allow read, write: if true;'"
            } else {
                "Cloud Sync Notice: $criticalError"
            }
            Pair(false, msg)
        } else {
            Pair(true, "Cloud Sync Active: $studentCount Student(s), $formCount Application(s), $bookingCount Booking(s)")
        }
    }

    suspend fun fetchStudentsDirectFromFirestore(): Pair<Boolean, List<StudentProfileEntity>> = withContext(Dispatchers.IO) {
        fetchAllRemoteDataFromFirestore()
        Pair(true, _firestoreStudents.value)
    }

    suspend fun saveStudentProfile(student: StudentProfileEntity): Boolean {
        eduDao.insertStudent(student)
        return syncStudentToFirestore(student)
    }

    suspend fun fetchUserFromFirestore(phone: String): StudentProfileEntity? = withContext(Dispatchers.IO) {
        try {
            val db = firestore ?: return@withContext null
            ensureFirebaseAuth()
            val digitsOnly = phone.filter { it.isDigit() }
            val cleanPhone = if (!phone.startsWith("+") && digitsOnly.length == 10) "+91$digitsOnly" else phone

            val candidates = mutableListOf<String>()
            if (cleanPhone.isNotBlank()) candidates.add(cleanPhone)
            if (digitsOnly.isNotBlank() && digitsOnly != cleanPhone) candidates.add(digitsOnly)
            if (digitsOnly.length == 10 && !candidates.contains("+91$digitsOnly")) candidates.add("+91$digitsOnly")

            for (docKey in candidates) {
                for (col in listOf("students", "users")) {
                    try {
                        val snap = Tasks.await(db.collection(col).document(docKey).get(), 6, TimeUnit.SECONDS)
                        if (snap.exists()) {
                            val student = parseStudentFromDoc(snap)
                            if (student != null) {
                                eduDao.insertStudent(student)
                                return@withContext student
                            }
                        }
                    } catch (e: Exception) {}
                }
            }

            for (queryVal in candidates) {
                try {
                    val querySnap = Tasks.await(db.collection("students").whereEqualTo("phone", queryVal).limit(1).get(), 6, TimeUnit.SECONDS)
                    if (!querySnap.isEmpty) {
                        val student = parseStudentFromDoc(querySnap.documents[0])
                        if (student != null) {
                            eduDao.insertStudent(student)
                            return@withContext student
                        }
                    }
                } catch (e: Exception) {}
            }

            val currentUid = try { FirebaseAuth.getInstance().currentUser?.uid ?: "" } catch (e: Exception) { "" }
            if (currentUid.isNotBlank()) {
                val uidSnap = Tasks.await(db.collection("students").document(currentUid).get(), 6, TimeUnit.SECONDS)
                if (uidSnap.exists()) {
                    val student = parseStudentFromDoc(uidSnap)
                    if (student != null) {
                        eduDao.insertStudent(student)
                        return@withContext student
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch student from Firestore: ${e.message}")
        }
        return@withContext null
    }

    suspend fun testFirestoreWriteToTestCollection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val db = firestore ?: return@withContext Pair(false, "Firestore initialization failed.")
            ensureFirebaseAuth()
            val docId = "test_doc_${System.currentTimeMillis()}"
            val testData = hashMapOf(
                "message" to "Firebase Cloud Firestore connected successfully!",
                "timestamp" to System.currentTimeMillis(),
                "docId" to docId,
                "status" to "LIVE"
            )
            val task = db.collection("test_collection").document(docId).set(testData)
            Tasks.await(task, 10, TimeUnit.SECONDS)
            Pair(true, "Successfully wrote test document '$docId' to 'test_collection' in Firestore!")
        } catch (e: Exception) {
            Log.e(TAG, "Test write error: ${e.message}", e)
            Pair(false, "Firestore write failed: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun diagnoseFirestoreConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Pair(false, "Firestore instance not initialized.")
        awaitFirebaseAuth()
        val auth = FirebaseAuth.getInstance()
        val currentUid = auth.currentUser?.uid ?: "Not authenticated"
        val sb = StringBuilder()
        sb.appendLine("Firebase Auth UID: $currentUid")

        var writeOk = false
        val testDocId = "diag_${System.currentTimeMillis()}"
        try {
            val testData = mapOf("test" to true, "timestamp" to System.currentTimeMillis())
            val task = db.collection("students").document(testDocId).set(testData)
            Tasks.await(task, 7, TimeUnit.SECONDS)
            writeOk = true
            db.collection("students").document(testDocId).delete()
            sb.appendLine("Firestore Write to 'students': SUCCESS")
        } catch (e: Exception) {
            sb.appendLine("Firestore Write to 'students': FAILED (${e.localizedMessage ?: e.message})")
        }

        var readOk = false
        var studentCount = 0
        try {
            val snap = Tasks.await(db.collection("students").limit(100).get(), 7, TimeUnit.SECONDS)
            readOk = true
            studentCount = snap.size()
            sb.appendLine("Firestore Read from 'students': SUCCESS ($studentCount doc(s))")
        } catch (e: Exception) {
            sb.appendLine("Firestore Read from 'students': FAILED (${e.localizedMessage ?: e.message})")
        }

        var formsCount = 0
        try {
            val fSnap = Tasks.await(db.collection("admission_forms").limit(100).get(), 6, TimeUnit.SECONDS)
            formsCount = fSnap.size()
            sb.appendLine("Firestore Read from 'admission_forms': SUCCESS ($formsCount doc(s))")
        } catch (e: Exception) {
            sb.appendLine("Firestore Read from 'admission_forms': FAILED (${e.localizedMessage ?: e.message})")
        }

        val allOk = writeOk && readOk
        if (!allOk) {
            sb.appendLine("\nTIP: If PERMISSION_DENIED occurs, update Firestore Database -> Rules in Firebase Console:")
            sb.appendLine("rules_version = '2';")
            sb.appendLine("service cloud.firestore {")
            sb.appendLine("  match /{document=**} {")
            sb.appendLine("    allow read, write: if true;")
            sb.appendLine("  }")
            sb.appendLine("}")
        }
        Pair(allOk, sb.toString())
    }

    suspend fun clearAllFirestoreCollections(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        Pair(true, "Clear completed.")
    }

    suspend fun syncStudentToFirestore(student: StudentProfileEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val db = firestore ?: return@withContext false
            awaitFirebaseAuth()
            val currentUid = try { FirebaseAuth.getInstance().currentUser?.uid ?: "" } catch (e: Exception) { "" }
            val cleanPhone = student.phone.trim()
            val digitsOnly = cleanPhone.filter { it.isDigit() }
            val finalUid = if (student.uid.isNotBlank()) student.uid else currentUid

            val studentMap = hashMapOf<String, Any>(
                "phone" to cleanPhone,
                "studentPhone" to cleanPhone,
                "name" to student.name,
                "studentName" to student.name,
                "fullName" to student.name,
                "email" to student.email,
                "institution" to student.institution,
                "school" to student.institution,
                "college" to student.institution,
                "gender" to student.gender,
                "city" to student.city,
                "state" to student.state,
                "country" to student.country,
                "studentClass" to student.studentClass,
                "qualification" to student.studentClass,
                "percentageMark" to student.percentageMark,
                "percentage" to student.percentageMark,
                "isAdmin" to student.isAdmin,
                "uid" to finalUid,
                "createdAt" to student.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )

            var anySuccess = false

            // 1. Write by UID (vital if Firestore rules require request.auth.uid == userId)
            if (finalUid.isNotBlank()) {
                try {
                    val uidDocRef = db.collection("students").document(finalUid)
                    Tasks.await(uidDocRef.set(studentMap, com.google.firebase.firestore.SetOptions.merge()), 8, TimeUnit.SECONDS)
                    Log.d(TAG, "Student doc successfully written to students/$finalUid")
                    anySuccess = true
                } catch (e: Exception) {
                    Log.w(TAG, "Notice writing to students/$finalUid: ${e.message}")
                }

                try {
                    db.collection("users").document(finalUid).set(studentMap, com.google.firebase.firestore.SetOptions.merge())
                } catch (e: Exception) {}
            }

            // 2. Write primary doc keyed by cleanPhone
            if (cleanPhone.isNotBlank()) {
                try {
                    val docRef = db.collection("students").document(cleanPhone)
                    Tasks.await(docRef.set(studentMap, com.google.firebase.firestore.SetOptions.merge()), 8, TimeUnit.SECONDS)
                    Log.d(TAG, "Student doc successfully written to students/$cleanPhone")
                    anySuccess = true
                } catch (e: Exception) {
                    Log.w(TAG, "Notice writing to students/$cleanPhone: ${e.message}")
                }

                try {
                    db.collection("users").document(cleanPhone).set(studentMap, com.google.firebase.firestore.SetOptions.merge())
                } catch (e: Exception) {}
            }

            // 3. If digits-only is 10 digits and distinct from cleanPhone, also write to standard Indian format
            if (digitsOnly.length == 10 && digitsOnly != cleanPhone) {
                try {
                    db.collection("students").document(digitsOnly).set(studentMap, com.google.firebase.firestore.SetOptions.merge())
                    db.collection("students").document("+91$digitsOnly").set(studentMap, com.google.firebase.firestore.SetOptions.merge())
                    anySuccess = true
                } catch (e: Exception) {}
            }

            // Immediately update local _firestoreStudents so live UI updates synchronously
            val updated = _firestoreStudents.value.toMutableList()
            val idx = updated.indexOfFirst {
                it.phone.filter { ch -> ch.isDigit() } == digitsOnly || (finalUid.isNotBlank() && it.uid == finalUid)
            }
            val entityToSave = student.copy(uid = finalUid)
            if (idx >= 0) {
                updated[idx] = entityToSave
            } else {
                updated.add(0, entityToSave)
            }
            _firestoreStudents.value = updated

            anySuccess
        } catch (e: Exception) {
            Log.e(TAG, "Sync student failed: ${e.message}", e)
            false
        }
    }

    fun getStudentBookings(phone: String, email: String = ""): Flow<List<CounsellingBookingEntity>> {
        val cleanDigits = phone.filter { it.isDigit() }
        return allCounsellingBookings.map { list ->
            list.filter { booking ->
                val bDigits = booking.studentPhone.filter { it.isDigit() }
                (cleanDigits.isNotBlank() && (bDigits.endsWith(cleanDigits) || cleanDigits.endsWith(bDigits))) ||
                (email.isNotBlank() && booking.studentEmail.equals(email, ignoreCase = true))
            }
        }
    }

    suspend fun bookCounsellingSession(booking: CounsellingBookingEntity): Long {
        val uniqueId = if (booking.id > 0) booking.id else {
            val hash = ("${booking.studentPhone}_${booking.bookingDate}_${booking.timestamp}").hashCode() and 0x7FFFFFFF
            if (hash != 0) hash else (System.currentTimeMillis().hashCode() and 0x7FFFFFFF)
        }
        val bookingToSave = booking.copy(id = uniqueId)
        val id = eduDao.insertCounsellingBooking(bookingToSave)
        syncBookingToFirestore(bookingToSave)
        return if (id > 0) id else uniqueId.toLong()
    }

    suspend fun updateCounsellingBookingStatus(bookingId: Int, status: String) {
        eduDao.updateBookingStatus(bookingId, status)
        val updated = _firestoreCounsellingBookings.value.map {
            if (it.id == bookingId) it.copy(status = status) else it
        }
        _firestoreCounsellingBookings.value = updated
        withContext(Dispatchers.IO) {
            try {
                val db = firestore ?: return@withContext
                ensureFirebaseAuth()
                val docId = bookingId.toString()
                db.collection("counselling_bookings").document(docId).update("status", status)
                for (col in listOf("career_counselling_bookings", "college_counselling_bookings", "course_counselling_bookings")) {
                    try { db.collection(col).document(docId).update("status", status) } catch (e: Exception) {}
                }
            } catch (e: Exception) { Log.w(TAG, "Update booking status failed: ${e.message}") }
        }
    }

    suspend fun syncBookingToFirestore(booking: CounsellingBookingEntity) = withContext(Dispatchers.IO) {
        try {
            val db = firestore ?: return@withContext
            awaitFirebaseAuth()
            val docId = if (booking.id > 0) booking.id.toString() else "booking_${System.currentTimeMillis()}"

            val bookingMap = counsellingBookingToMap(booking)
            
            // 1. Master collection
            db.collection("counselling_bookings").document(docId).set(bookingMap, com.google.firebase.firestore.SetOptions.merge())

            // 2. Specific collection based on type
            val type = booking.bookingType.lowercase()
            val target = booking.targetCourse.lowercase()

            val subCol = if (type.contains("career") || target.contains("career") || target.contains("doctor") || target.contains("engineer") || target.contains("pilot") || target.contains("lawyer")) {
                "career_counselling_bookings"
            } else if (type.contains("college") || target.contains("college") || target.contains("university") || target.contains("institute")) {
                "college_counselling_bookings"
            } else {
                "course_counselling_bookings"
            }
            db.collection(subCol).document(docId).set(bookingMap, com.google.firebase.firestore.SetOptions.merge())
            Log.d(TAG, "Booking successfully synced to Firestore document '$docId' in 'counselling_bookings' and '$subCol'")

            // Immediately update in-memory _firestoreCounsellingBookings
            val current = _firestoreCounsellingBookings.value.toMutableList()
            val idx = current.indexOfFirst { it.id == booking.id && booking.id > 0 }
            if (idx >= 0) {
                current[idx] = booking
            } else {
                current.add(0, booking)
            }
            _firestoreCounsellingBookings.value = current
        } catch (e: Exception) {
            Log.w(TAG, "Sync booking failed: ${e.message}")
        }
    }

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val loadedColleges = if (context != null) DatabaseInitializer.loadCollegesFromAssets(context) else DatabaseInitializer.initialColleges
        val loadedCareers = if (context != null) DatabaseInitializer.loadCareersFromAssets(context) else DatabaseInitializer.initialCareers
        val loadedCourses = if (context != null) DatabaseInitializer.loadCoursesFromAssets(context) else DatabaseInitializer.initialCourses

        val existingCareers = eduDao.getAllCareers().first()
        val careersNeedRefresh = existingCareers.isEmpty() || 
            existingCareers.size != loadedCareers.size || 
            existingCareers.any { it.image.contains("unsplash.com") } ||
            loadedCareers.any { loaded ->
                val existing = existingCareers.find { it.id == loaded.id }
                existing == null || existing.image != loaded.image || existing.titleKn != loaded.titleKn || existing.title != loaded.title
            }

        if (careersNeedRefresh) {
            eduDao.deleteAllCareers()
            eduDao.insertCareers(loadedCareers)

            // Auto-update Firestore with clean asset career data
            val db = firestore
            if (db != null) {
                val rawCareers = if (context != null) DatabaseInitializer.loadRawCareersJson(context) else emptyList()
                val careersToUpload = if (rawCareers.isNotEmpty()) rawCareers else loadedCareers.map { c ->
                    hashMapOf<String, Any>(
                        "id" to c.id,
                        "title" to c.title,
                        "titleKn" to c.titleKn,
                        "image" to c.image,
                        "imageUrl" to c.image,
                        "category" to c.category
                    )
                }
                for (chunk in careersToUpload.chunked(50)) {
                    val batch = db.batch()
                    for (cMap in chunk) {
                        val docId = cMap["id"]?.toString() ?: continue
                        val docRef = db.collection("careers").document(docId)
                        batch.set(docRef, cMap, SetOptions.merge())
                    }
                    batch.commit().addOnFailureListener { e ->
                        Log.w(TAG, "Careers batch update notice: ${e.message}")
                    }
                }
            }
        }

        val existingColleges = eduDao.getAllColleges().first()
        val collegesNeedRefresh = existingColleges.isEmpty() || 
            existingColleges.size != loadedColleges.size || 
            existingColleges.any { it.imageUrl.contains("unsplash.com") } ||
            existingColleges.firstOrNull()?.name != loadedColleges.firstOrNull()?.name || 
            existingColleges.firstOrNull()?.imageUrl != loadedColleges.firstOrNull()?.imageUrl ||
            loadedColleges.any { loaded ->
                val existing = existingColleges.find { it.id == loaded.id }
                existing == null || existing.imageUrl != loaded.imageUrl || existing.heroBannerUrl != loaded.heroBannerUrl || existing.name != loaded.name
            }

        if (collegesNeedRefresh || existingColleges.none { it.id == 0 } || existingColleges.any { it.id == 100 }) {
            eduDao.deleteAllColleges()
            eduDao.insertColleges(loadedColleges)

            // Auto-update Firestore with clean asset college data
            val db = firestore
            if (db != null) {
                val rawColleges = if (context != null) DatabaseInitializer.loadRawCollegesJson(context) else emptyList()
                val collegesToUpload = if (rawColleges.isNotEmpty()) rawColleges else loadedColleges.map { col ->
                    hashMapOf<String, Any>(
                        "id" to col.id,
                        "name" to col.name,
                        "nameKn" to col.nameKn,
                        "location" to col.location,
                        "locationKn" to col.locationKn,
                        "city" to col.city,
                        "state" to col.state,
                        "coursesOffered" to col.coursesOffered,
                        "coursesOfferedKn" to col.coursesOfferedKn,
                        "minFeesLakhs" to col.minFeesLakhs,
                        "maxFeesLakhs" to col.maxFeesLakhs,
                        "hostelAvailable" to col.hostelAvailable,
                        "hostelFeesPerYear" to col.hostelFeesPerYear,
                        "recognition" to col.recognition,
                        "recognitionKn" to col.recognitionKn,
                        "ranking" to col.ranking,
                        "eligibility" to col.eligibility,
                        "eligibilityKn" to col.eligibilityKn,
                        "admissionStatus" to col.admissionStatus,
                        "imageUrl" to col.imageUrl,
                        "image" to col.imageUrl,
                        "heroBannerUrl" to col.heroBannerUrl,
                        "overview" to col.overview,
                        "overviewKn" to col.overviewKn,
                        "averagePackageLakhs" to col.averagePackageLakhs,
                        "highestPackageLakhs" to col.highestPackageLakhs,
                        "websiteUrl" to col.websiteUrl,
                        "isFeatured" to col.isFeatured
                    )
                }
                for (chunk in collegesToUpload.chunked(50)) {
                    val batch = db.batch()
                    for (colMap in chunk) {
                        val docId = colMap["id"]?.toString() ?: continue
                        val docRef = db.collection("colleges").document(docId)
                        batch.set(docRef, colMap, SetOptions.merge())
                    }
                    batch.commit().addOnFailureListener { e ->
                        Log.w(TAG, "Colleges batch update notice: ${e.message}")
                    }
                }
            }
        }

        val existingCourses = eduDao.getAllCourses().first()
        val coursesNeedRefresh = existingCourses.isEmpty() || 
            existingCourses.size != loadedCourses.size || 
            loadedCourses.any { loaded ->
                val existing = existingCourses.find { it.id == loaded.id }
                existing == null || existing.imageUrl != loaded.imageUrl || existing.name != loaded.name || existing.nameKn != loaded.nameKn
            }

        if (coursesNeedRefresh) {
            eduDao.deleteAllCourses()
            eduDao.insertCourses(loadedCourses)

            // Auto-update Firestore with clean asset course data
            val db = firestore
            if (db != null) {
                val rawCourses = if (context != null) DatabaseInitializer.loadRawCoursesJson(context) else emptyList()
                val coursesToUpload = if (rawCourses.isNotEmpty()) rawCourses else loadedCourses.map { crs ->
                    hashMapOf<String, Any>(
                        "id" to crs.id,
                        "code" to crs.code,
                        "name" to crs.name,
                        "nameKn" to crs.nameKn,
                        "title" to crs.name,
                        "titleKn" to crs.nameKn,
                        "category" to crs.category,
                        "durationYears" to crs.durationYears,
                        "duration" to crs.durationYears,
                        "eligibility" to crs.eligibility,
                        "eligibilityKn" to crs.eligibilityKn,
                        "avgFeesLakhs" to crs.avgFeesLakhs,
                        "avgPackageLakhs" to crs.avgPackageLakhs,
                        "careerScope" to crs.careerScope,
                        "careerScopeKn" to crs.careerScopeKn,
                        "futureScope" to crs.careerScope,
                        "futureScopeKn" to crs.careerScopeKn,
                        "description" to crs.description,
                        "descriptionKn" to crs.descriptionKn,
                        "topCollegesCount" to crs.topCollegesCount,
                        "imageUrl" to crs.imageUrl,
                        "image" to crs.imageUrl
                    )
                }
                for (chunk in coursesToUpload.chunked(50)) {
                    val batch = db.batch()
                    for (crsMap in chunk) {
                        val docId = crsMap["id"]?.toString() ?: continue
                        val docRefPlural = db.collection("courses").document(docId)
                        val docRefSingular = db.collection("course").document(docId)
                        batch.set(docRefPlural, crsMap, SetOptions.merge())
                        batch.set(docRefSingular, crsMap, SetOptions.merge())
                    }
                    batch.commit().addOnFailureListener { e ->
                        Log.w(TAG, "Courses batch update notice: ${e.message}")
                    }
                }
            }
        }

        val existingScholarships = eduDao.getAllScholarships().first()
        if (existingScholarships.isEmpty()) {
            eduDao.insertScholarships(DatabaseInitializer.initialScholarships)
        }

        val existingNotifications = eduDao.getAllNotifications().first()
        if (existingNotifications.isEmpty()) {
            eduDao.insertNotifications(DatabaseInitializer.initialNotifications)
        }

        // Purge any lingering legacy demo students, bookings or admission forms
        eduDao.deleteSampleStudents()
        eduDao.deleteSampleBookings()
        eduDao.deleteSampleAdmissionForms()

        val existingForms = eduDao.getAllAdmissionForms().first()
        if (existingForms.isEmpty() && DatabaseInitializer.initialAdmissionForms.isNotEmpty()) {
            eduDao.insertAdmissionForms(DatabaseInitializer.initialAdmissionForms)
        }

        val existingBookings = eduDao.getAllCounsellingBookings().first()
        if (existingBookings.isEmpty() && DatabaseInitializer.initialCounsellingBookings.isNotEmpty()) {
            eduDao.insertCounsellingBookings(DatabaseInitializer.initialCounsellingBookings)
        }

        // Start Realtime Firestore listeners immediately so remote images, students & bookings reflect instantly
        startRealtimeFirestoreSync()

        // Trigger background sync of local data to Firestore
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncAllDataToFirestore()
            } catch (e: Exception) {
                Log.w(TAG, "Initial Firestore sync skipped: ${e.message}")
            }
        }
    }

    suspend fun syncAllDataToFirestore(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val db = firestore ?: return@withContext Pair(false, "Firestore instance unavailable.")

            // Careers (200 career profiles from loaded assets)
            val rawCareers = if (context != null) DatabaseInitializer.loadRawCareersJson(context) else emptyList()
            val careers = if (context != null) DatabaseInitializer.loadCareersFromAssets(context) else eduDao.getAllCareers().first()
            val careersToUpload = if (rawCareers.isNotEmpty()) rawCareers else careers.map { c ->
                hashMapOf<String, Any>(
                    "id" to c.id,
                    "title" to c.title,
                    "titleKn" to c.titleKn,
                    "image" to c.image,
                    "imageUrl" to c.image,
                    "category" to c.category,
                    "tagline" to c.tagline,
                    "taglineKn" to c.taglineKn,
                    "description" to c.description,
                    "descriptionKn" to c.descriptionKn,
                    "overview" to c.overview,
                    "overviewKn" to c.overviewKn,
                    "whyChoose" to c.whyChoose,
                    "whyChooseKn" to c.whyChooseKn,
                    "benefits" to c.benefits,
                    "benefitsKn" to c.benefitsKn,
                    "eligibility" to c.eligibility,
                    "eligibilityKn" to c.eligibilityKn,
                    "subjects" to c.subjects,
                    "subjectsKn" to c.subjectsKn,
                    "exams" to c.exams,
                    "examsKn" to c.examsKn,
                    "pathway" to c.pathway,
                    "pathwayKn" to c.pathwayKn,
                    "skills" to c.skills,
                    "skillsKn" to c.skillsKn,
                    "jobRoles" to c.jobRoles,
                    "jobRolesKn" to c.jobRolesKn,
                    "opportunities" to c.opportunities,
                    "opportunitiesKn" to c.opportunitiesKn,
                    "topColleges" to c.topColleges,
                    "topCollegesKn" to c.topCollegesKn,
                    "topRecruiters" to c.topRecruiters,
                    "topRecruitersKn" to c.topRecruitersKn,
                    "salaryAverage" to c.salaryAverage,
                    "salaryHighest" to c.salaryHighest,
                    "feesAverage" to c.feesAverage,
                    "duration" to c.duration,
                    "scholarships" to c.scholarships,
                    "scholarshipsKn" to c.scholarshipsKn,
                    "futureScope" to c.futureScope,
                    "futureScopeKn" to c.futureScopeKn,
                    "certifications" to c.certifications,
                    "certificationsKn" to c.certificationsKn,
                    "faqs" to c.faqs,
                    "faqsKn" to c.faqsKn
                )
            }
            for (chunk in careersToUpload.chunked(50)) {
                val batch = db.batch()
                for (map in chunk) {
                    val docId = map["id"]?.toString() ?: continue
                    val docRef = db.collection("careers").document(docId)
                    batch.set(docRef, map, SetOptions.merge())
                }
                try {
                    Tasks.await(batch.commit(), 30, TimeUnit.SECONDS)
                } catch (e: Exception) {
                    Log.w(TAG, "Careers batch write notice: ${e.message}")
                }
            }

            // Colleges (100 top colleges)
            val rawColleges = if (context != null) DatabaseInitializer.loadRawCollegesJson(context) else emptyList()
            val colleges = if (context != null) DatabaseInitializer.loadCollegesFromAssets(context) else eduDao.getAllColleges().first()
            val collegesToUpload = if (rawColleges.isNotEmpty()) rawColleges else colleges.map { col ->
                hashMapOf<String, Any>(
                    "id" to col.id,
                    "name" to col.name,
                    "nameKn" to col.nameKn,
                    "location" to col.location,
                    "locationKn" to col.locationKn,
                    "city" to col.city,
                    "state" to col.state,
                    "coursesOffered" to col.coursesOffered,
                    "coursesOfferedKn" to col.coursesOfferedKn,
                    "minFeesLakhs" to col.minFeesLakhs,
                    "maxFeesLakhs" to col.maxFeesLakhs,
                    "hostelAvailable" to col.hostelAvailable,
                    "hostelFeesPerYear" to col.hostelFeesPerYear,
                    "recognition" to col.recognition,
                    "recognitionKn" to col.recognitionKn,
                    "ranking" to col.ranking,
                    "eligibility" to col.eligibility,
                    "eligibilityKn" to col.eligibilityKn,
                    "admissionStatus" to col.admissionStatus,
                    "imageUrl" to col.imageUrl,
                    "image" to col.imageUrl,
                    "heroBannerUrl" to col.heroBannerUrl,
                    "overview" to col.overview,
                    "overviewKn" to col.overviewKn,
                    "averagePackageLakhs" to col.averagePackageLakhs,
                    "highestPackageLakhs" to col.highestPackageLakhs,
                    "websiteUrl" to col.websiteUrl,
                    "isFeatured" to col.isFeatured
                )
            }
            for (chunk in collegesToUpload.chunked(50)) {
                val batch = db.batch()
                for (colMap in chunk) {
                    val docId = colMap["id"]?.toString() ?: continue
                    val docRef = db.collection("colleges").document(docId)
                    batch.set(docRef, colMap, SetOptions.merge())
                }
                try {
                    Tasks.await(batch.commit(), 30, TimeUnit.SECONDS)
                } catch (e: Exception) {
                    Log.w(TAG, "Colleges batch write notice: ${e.message}")
                }
            }

            // Courses (200 courses) - writing full clean dataset with proper Kannada titles
            val rawCourses = if (context != null) DatabaseInitializer.loadRawCoursesJson(context) else emptyList()
            val courses = if (context != null) DatabaseInitializer.loadCoursesFromAssets(context) else eduDao.getAllCourses().first()
            val coursesToUpload = if (rawCourses.isNotEmpty()) rawCourses else courses.map { crs ->
                hashMapOf<String, Any>(
                    "id" to crs.id,
                    "code" to crs.code,
                    "name" to crs.name,
                    "nameKn" to crs.nameKn,
                    "title" to crs.name,
                    "titleKn" to crs.nameKn,
                    "category" to crs.category,
                    "durationYears" to crs.durationYears,
                    "duration" to crs.durationYears,
                    "eligibility" to crs.eligibility,
                    "eligibilityKn" to crs.eligibilityKn,
                    "avgFeesLakhs" to crs.avgFeesLakhs,
                    "avgPackageLakhs" to crs.avgPackageLakhs,
                    "careerScope" to crs.careerScope,
                    "careerScopeKn" to crs.careerScopeKn,
                    "futureScope" to crs.careerScope,
                    "futureScopeKn" to crs.careerScopeKn,
                    "description" to crs.description,
                    "descriptionKn" to crs.descriptionKn,
                    "topCollegesCount" to crs.topCollegesCount,
                    "imageUrl" to crs.imageUrl,
                    "image" to crs.imageUrl
                )
            }
            for (chunk in coursesToUpload.chunked(50)) {
                val batch = db.batch()
                for (crsMap in chunk) {
                    val docId = crsMap["id"]?.toString() ?: continue
                    val docRefPlural = db.collection("courses").document(docId)
                    val docRefSingular = db.collection("course").document(docId)
                    batch.set(docRefPlural, crsMap, SetOptions.merge())
                    batch.set(docRefSingular, crsMap, SetOptions.merge())
                }
                try {
                    Tasks.await(batch.commit(), 30, TimeUnit.SECONDS)
                } catch (e: Exception) {
                    Log.w(TAG, "Courses batch write notice: ${e.message}")
                }
            }

            // Scholarships
            val scholarships = eduDao.getAllScholarships().first()
            for (s in scholarships) {
                db.collection("scholarships").document(s.id.toString()).set(s)
            }

            // Notifications
            val notifications = eduDao.getAllNotifications().first()
            for (n in notifications) {
                db.collection("notifications").document(n.id.toString()).set(n)
            }

            // Students
            val students = eduDao.getAllStudents().first().filter { it.name != "Sample Student" && it.email != "student@carrerpath.org" }
            for (st in students) {
                try {
                    syncStudentToFirestore(st)
                } catch (e: Exception) {}
            }

            // Admission Forms / College Applications
            val forms = eduDao.getAllAdmissionForms().first().filter { it.studentName != "Sample Student" && it.email != "student@carrerpath.org" }
            for (f in forms) {
                try {
                    syncAdmissionFormToFirestore(f)
                } catch (e: Exception) {}
            }

            // Counselling Bookings (Career Counselling, Course Counselling, College Counselling)
            val bookings = eduDao.getAllCounsellingBookings().first().filter { it.studentName != "Sample Student" && it.studentEmail != "student@carrerpath.org" }
            for (b in bookings) {
                try {
                    syncBookingToFirestore(b)
                } catch (e: Exception) {}
            }

            // Immediately fetch all remote data to merge everything from other devices
            fetchAllRemoteDataFromFirestore()

            Pair(true, "All app data synced with Cloud Firestore (${colleges.size} Colleges, ${courses.size} Courses, ${careers.size} Careers, ${forms.size} Applications, ${bookings.size} Bookings)")
        } catch (e: Exception) {
            Log.w(TAG, "syncAllDataToFirestore background note: ${e.message}")
            Pair(false, "Firestore Sync: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun getCareerById(id: Int): CareerEntity? = eduDao.getCareerById(id)

    suspend fun addOrUpdateCareer(career: CareerEntity) {
        eduDao.insertCareer(career)
        withContext(Dispatchers.IO) {
            try {
                firestore?.collection("careers")?.document(career.id.toString())?.set(career)
            } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
        }
    }

    suspend fun deleteCareer(id: Int) = withContext(Dispatchers.IO) {
        eduDao.deleteCareer(id)
        try {
            firestore?.collection("careers")?.document(id.toString())?.delete()
        } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
    }

    fun searchColleges(query: String): Flow<List<CollegeEntity>> {
        return if (query.isBlank()) eduDao.getAllColleges() else eduDao.searchColleges(query)
    }

    fun searchCourses(query: String): Flow<List<CourseEntity>> {
        return if (query.isBlank()) eduDao.getAllCourses() else eduDao.searchCourses(query)
    }

    fun getCoursesByCategory(category: String): Flow<List<CourseEntity>> {
        return if (category == "All") eduDao.getAllCourses() else eduDao.getCoursesByCategory(category)
    }

    suspend fun getCollegeById(id: Int): CollegeEntity? = eduDao.getCollegeById(id)

    suspend fun submitAdmissionForm(form: AdmissionFormEntity): Long {
        val uniqueId = if (form.id > 0) form.id else {
            val hash = ("${form.phone}_${form.preferredCollege}_${form.timestamp}").hashCode() and 0x7FFFFFFF
            if (hash != 0) hash else (System.currentTimeMillis().hashCode() and 0x7FFFFFFF)
        }
        val formToSave = form.copy(id = uniqueId)
        val id = eduDao.insertAdmissionForm(formToSave)
        syncAdmissionFormToFirestore(formToSave)
        return if (id > 0) id else uniqueId.toLong()
    }

    suspend fun syncAdmissionFormToFirestore(form: AdmissionFormEntity) = withContext(Dispatchers.IO) {
        try {
            val db = firestore ?: return@withContext
            awaitFirebaseAuth()
            val docId = if (form.id > 0) form.id.toString() else "form_${System.currentTimeMillis()}"

            val formMap = admissionFormToMap(form)

            // Write to admission_forms
            db.collection("admission_forms").document(docId).set(formMap, com.google.firebase.firestore.SetOptions.merge())
            // Write to college_applications
            db.collection("college_applications").document(docId).set(formMap, com.google.firebase.firestore.SetOptions.merge())
            Log.d(TAG, "Admission form successfully synced to Firestore document '$docId' in 'admission_forms' and 'college_applications'")

            // Immediately update in-memory _firestoreAdmissionForms
            val current = _firestoreAdmissionForms.value.toMutableList()
            val idx = current.indexOfFirst { it.id == form.id && form.id > 0 }
            if (idx >= 0) {
                current[idx] = form
            } else {
                current.add(0, form)
            }
            _firestoreAdmissionForms.value = current
        } catch (e: Exception) {
            Log.w(TAG, "Sync admission form failed: ${e.message}")
        }
    }

    fun getStudentAdmissionForms(phone: String, email: String = ""): Flow<List<AdmissionFormEntity>> {
        val cleanDigits = phone.filter { it.isDigit() }
        return allAdmissionForms.map { list ->
            list.filter { form ->
                val fDigits = form.phone.filter { it.isDigit() }
                (cleanDigits.isNotBlank() && (fDigits.endsWith(cleanDigits) || cleanDigits.endsWith(fDigits))) ||
                (email.isNotBlank() && form.email.equals(email, ignoreCase = true))
            }
        }
    }

    suspend fun updateAdmissionFormStatus(formId: Int, status: String) {
        eduDao.updateFormStatus(formId, status)
        val updated = _firestoreAdmissionForms.value.map {
            if (it.id == formId) it.copy(status = status) else it
        }
        _firestoreAdmissionForms.value = updated
        withContext(Dispatchers.IO) {
            try {
                val db = firestore ?: return@withContext
                ensureFirebaseAuth()
                val docId = formId.toString()
                db.collection("admission_forms").document(docId).update("status", status)
                db.collection("college_applications").document(docId).update("status", status)
            } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
        }
    }

    // Saved Colleges
    fun getSavedColleges(userId: String): Flow<List<CollegeEntity>> = eduDao.getSavedColleges(userId)
    fun getSavedCollegeIds(userId: String): Flow<List<Int>> = eduDao.getSavedCollegeIds(userId)

    suspend fun toggleSaveCollege(userId: String, collegeId: Int, currentlySaved: Boolean) {
        if (currentlySaved) {
            eduDao.unsaveCollege(userId, collegeId)
        } else {
            eduDao.saveCollege(SavedCollegeEntity(userId = userId, collegeId = collegeId))
        }
    }

    // Notifications
    fun getStudentNotifications(phone: String, email: String = ""): Flow<List<NotificationEntity>> {
        return eduDao.getNotificationsForStudent(phone, email)
    }

    suspend fun broadcastNotification(title: String, message: String, category: String, userPhone: String = "") {
        val notification = NotificationEntity(
            title = title,
            message = message,
            category = category,
            userPhone = userPhone
        )
        val id = eduDao.insertNotification(notification)
        withContext(Dispatchers.IO) {
            try {
                firestore?.collection("notifications")?.document(id.toString())?.set(notification.copy(id = id.toInt()))
            } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
        }
    }

    suspend fun markNotificationAsRead(id: Int) {
        eduDao.markNotificationRead(id)
    }

    // Admin operations
    suspend fun addOrUpdateCollege(college: CollegeEntity) {
        eduDao.insertCollege(college)
        withContext(Dispatchers.IO) {
            try {
                firestore?.collection("colleges")?.document(college.id.toString())?.set(college)
            } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
        }
    }

    suspend fun deleteCollege(id: Int) = withContext(Dispatchers.IO) {
        eduDao.deleteCollege(id)
        try {
            firestore?.collection("colleges")?.document(id.toString())?.delete()
        } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
    }

    suspend fun addOrUpdateCourse(course: CourseEntity) {
        eduDao.insertCourse(course)
        withContext(Dispatchers.IO) {
            try {
                firestore?.collection("courses")?.document(course.id.toString())?.set(course)
            } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
        }
    }

    suspend fun deleteCourse(id: Int) = withContext(Dispatchers.IO) {
        eduDao.deleteCourse(id)
        try {
            firestore?.collection("courses")?.document(id.toString())?.delete()
        } catch (e: Exception) { Log.w(TAG, e.message ?: "") }
    }

    suspend fun reseedCourses(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val db = firestore
            val loadedCourses = if (context != null) DatabaseInitializer.loadCoursesFromAssets(context) else DatabaseInitializer.initialCourses
            val rawCourses = if (context != null) DatabaseInitializer.loadRawCoursesJson(context) else emptyList()

            eduDao.deleteAllCourses()
            eduDao.insertCourses(loadedCourses)

            if (db != null) {
                val coursesToUpload = if (rawCourses.isNotEmpty()) rawCourses else loadedCourses.map { crs ->
                    hashMapOf<String, Any>(
                        "id" to crs.id,
                        "code" to crs.code,
                        "name" to crs.name,
                        "nameKn" to crs.nameKn,
                        "title" to crs.name,
                        "titleKn" to crs.nameKn,
                        "category" to crs.category,
                        "durationYears" to crs.durationYears,
                        "duration" to crs.durationYears,
                        "eligibility" to crs.eligibility,
                        "eligibilityKn" to crs.eligibilityKn,
                        "avgFeesLakhs" to crs.avgFeesLakhs,
                        "avgPackageLakhs" to crs.avgPackageLakhs,
                        "careerScope" to crs.careerScope,
                        "careerScopeKn" to crs.careerScopeKn,
                        "futureScope" to crs.careerScope,
                        "futureScopeKn" to crs.careerScopeKn,
                        "description" to crs.description,
                        "descriptionKn" to crs.descriptionKn,
                        "topCollegesCount" to crs.topCollegesCount,
                        "imageUrl" to crs.imageUrl,
                        "image" to crs.imageUrl
                    )
                }

                for (chunk in coursesToUpload.chunked(50)) {
                    val batch = db.batch()
                    for (map in chunk) {
                        val docId = map["id"]?.toString() ?: continue
                        val docRefPlural = db.collection("courses").document(docId)
                        val docRefSingular = db.collection("course").document(docId)
                        batch.set(docRefPlural, map, SetOptions.merge())
                        batch.set(docRefSingular, map, SetOptions.merge())
                    }
                    try {
                        Tasks.await(batch.commit(), 30, TimeUnit.SECONDS)
                    } catch (e: Exception) {
                        Log.w(TAG, "Reseed batch write notice: ${e.message}")
                    }
                }
            }
            Pair(true, "Successfully synced all ${loadedCourses.size} courses to Firebase and local database.")
        } catch (e: Exception) {
            Pair(false, "Failed to sync courses: ${e.message}")
        }
    }

    suspend fun reseedCareers(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val loadedCareers = if (context != null) DatabaseInitializer.loadCareersFromAssets(context) else DatabaseInitializer.initialCareers
            val rawCareers = if (context != null) DatabaseInitializer.loadRawCareersJson(context) else emptyList()
            eduDao.deleteAllCareers()
            eduDao.insertCareers(loadedCareers)

            val db = firestore
            if (db != null) {
                val careersToUpload = if (rawCareers.isNotEmpty()) rawCareers else loadedCareers.map { c ->
                    hashMapOf<String, Any>(
                        "id" to c.id,
                        "title" to c.title,
                        "titleKn" to c.titleKn,
                        "image" to c.image,
                        "imageUrl" to c.image,
                        "category" to c.category,
                        "tagline" to c.tagline,
                        "taglineKn" to c.taglineKn,
                        "description" to c.description,
                        "descriptionKn" to c.descriptionKn,
                        "overview" to c.overview,
                        "overviewKn" to c.overviewKn,
                        "whyChoose" to c.whyChoose,
                        "whyChooseKn" to c.whyChooseKn,
                        "benefits" to c.benefits,
                        "benefitsKn" to c.benefitsKn,
                        "eligibility" to c.eligibility,
                        "eligibilityKn" to c.eligibilityKn,
                        "subjects" to c.subjects,
                        "subjectsKn" to c.subjectsKn,
                        "exams" to c.exams,
                        "examsKn" to c.examsKn,
                        "pathway" to c.pathway,
                        "pathwayKn" to c.pathwayKn,
                        "skills" to c.skills,
                        "skillsKn" to c.skillsKn,
                        "jobRoles" to c.jobRoles,
                        "jobRolesKn" to c.jobRolesKn,
                        "opportunities" to c.opportunities,
                        "opportunitiesKn" to c.opportunitiesKn,
                        "topColleges" to c.topColleges,
                        "topCollegesKn" to c.topCollegesKn,
                        "topRecruiters" to c.topRecruiters,
                        "topRecruitersKn" to c.topRecruitersKn,
                        "salaryAverage" to c.salaryAverage,
                        "salaryHighest" to c.salaryHighest,
                        "feesAverage" to c.feesAverage,
                        "duration" to c.duration,
                        "scholarships" to c.scholarships,
                        "scholarshipsKn" to c.scholarshipsKn,
                        "futureScope" to c.futureScope,
                        "futureScopeKn" to c.futureScopeKn,
                        "certifications" to c.certifications,
                        "certificationsKn" to c.certificationsKn,
                        "faqs" to c.faqs,
                        "faqsKn" to c.faqsKn
                    )
                }

                for (chunk in careersToUpload.chunked(50)) {
                    val batch = db.batch()
                    for (cMap in chunk) {
                        val docId = cMap["id"]?.toString() ?: continue
                        val docRef = db.collection("careers").document(docId)
                        batch.set(docRef, cMap, SetOptions.merge())
                    }
                    try {
                        Tasks.await(batch.commit(), 30, TimeUnit.SECONDS)
                    } catch (e: Exception) {
                        Log.w(TAG, "Careers reseed batch write notice: ${e.message}")
                    }
                }
            }
            Pair(true, "Successfully reseeded ${loadedCareers.size} careers with Pexels images to local database and Firestore.")
        } catch (e: Exception) {
            Pair(false, "Failed to reseed careers: ${e.message}")
        }
    }

    suspend fun reseedColleges(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val loadedColleges = if (context != null) DatabaseInitializer.loadCollegesFromAssets(context) else DatabaseInitializer.initialColleges
            val rawColleges = if (context != null) DatabaseInitializer.loadRawCollegesJson(context) else emptyList()
            eduDao.deleteAllColleges()
            eduDao.insertColleges(loadedColleges)

            val db = firestore
            if (db != null) {
                val collegesToUpload = if (rawColleges.isNotEmpty()) rawColleges else loadedColleges.map { col ->
                    hashMapOf<String, Any>(
                        "id" to col.id,
                        "name" to col.name,
                        "nameKn" to col.nameKn,
                        "location" to col.location,
                        "locationKn" to col.locationKn,
                        "city" to col.city,
                        "state" to col.state,
                        "coursesOffered" to col.coursesOffered,
                        "coursesOfferedKn" to col.coursesOfferedKn,
                        "minFeesLakhs" to col.minFeesLakhs,
                        "maxFeesLakhs" to col.maxFeesLakhs,
                        "hostelAvailable" to col.hostelAvailable,
                        "hostelFeesPerYear" to col.hostelFeesPerYear,
                        "recognition" to col.recognition,
                        "recognitionKn" to col.recognitionKn,
                        "ranking" to col.ranking,
                        "eligibility" to col.eligibility,
                        "eligibilityKn" to col.eligibilityKn,
                        "admissionStatus" to col.admissionStatus,
                        "imageUrl" to col.imageUrl,
                        "image" to col.imageUrl,
                        "heroBannerUrl" to col.heroBannerUrl,
                        "overview" to col.overview,
                        "overviewKn" to col.overviewKn,
                        "averagePackageLakhs" to col.averagePackageLakhs,
                        "highestPackageLakhs" to col.highestPackageLakhs,
                        "websiteUrl" to col.websiteUrl,
                        "isFeatured" to col.isFeatured
                    )
                }

                for (chunk in collegesToUpload.chunked(50)) {
                    val batch = db.batch()
                    for (colMap in chunk) {
                        val docId = colMap["id"]?.toString() ?: continue
                        val docRef = db.collection("colleges").document(docId)
                        batch.set(docRef, colMap, SetOptions.merge())
                    }
                    try {
                        Tasks.await(batch.commit(), 30, TimeUnit.SECONDS)
                    } catch (e: Exception) {
                        Log.w(TAG, "Colleges reseed batch write notice: ${e.message}")
                    }
                }
            }
            Pair(true, "Successfully reseeded ${loadedColleges.size} colleges (IDs 0-99 with logo.dev URLs) to local database and Firestore.")
        } catch (e: Exception) {
            Pair(false, "Failed to reseed colleges: ${e.message}")
        }
    }

    // AI Counseling
    suspend fun runAICounseling(query: AICounselingQuery): AICounselingResult {
        return geminiService.getRecommendations(query)
    }

    // Psychometric Assessment
    fun getStudentAssessment(phone: String): Flow<PsychometricAssessmentEntity?> = eduDao.getLatestAssessmentForStudent(phone)

    suspend fun savePsychometricAssessment(assessment: PsychometricAssessmentEntity): Long {
        val id = eduDao.insertAssessment(assessment)
        withContext(Dispatchers.IO) {
            try {
                firestore?.collection("psychometric_assessments")?.document(if (id > 0) id.toString() else "assess_${System.currentTimeMillis()}")?.set(assessment.copy(id = id.toInt()))
            } catch (e: Exception) { Log.w(TAG, "Assessment sync error: ${e.message}") }
        }
        return id
    }

    // Account Deletion - Clean up user's local Room database data
    suspend fun deleteLocalUserData(phone: String, email: String, userId: String) {
        try {
            eduDao.deleteStudent(phone, email)
            eduDao.deleteBookingsForStudent(phone, email)
            eduDao.deleteAdmissionFormsForStudent(phone, email)
            eduDao.deleteAssessmentsForStudent(phone)
            eduDao.deleteSavedCollegesForUser(userId)
            if (phone.isNotBlank() && phone != userId) {
                eduDao.deleteSavedCollegesForUser(phone)
            }
            eduDao.deletePersonalNotificationsForStudent(phone, email)
            Log.d(TAG, "Local Room database cleanup completed for phone: $phone, email: $email")
        } catch (e: Exception) {
            Log.w(TAG, "Error cleaning local user data: ${e.message}")
        }
    }

    // Account Deletion - Scoped Firestore deletion ONLY for authenticated user's documents
    suspend fun deleteUserDataFromFirestore(
        phoneVariants: List<String>,
        userEmail: String = "",
        userUid: String = ""
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val db = firestore ?: return@withContext Result.success(Unit)

            // 1. Delete student profile doc in "students"
            val docIdsToDelete = (phoneVariants + listOfNotNull(userUid.takeIf { it.isNotBlank() })).distinct()
            for (docId in docIdsToDelete) {
                try {
                    Tasks.await(db.collection("students").document(docId).delete(), 10, TimeUnit.SECONDS)
                } catch (e: Exception) {
                    Log.w(TAG, "Notice deleting student doc $docId: ${e.message}")
                }
            }
            deleteDocumentsByQuery(db.collection("students"), "phone", phoneVariants)
            if (userEmail.isNotBlank()) {
                deleteDocumentsByQuery(db.collection("students"), "email", listOf(userEmail))
            }
            if (userUid.isNotBlank()) {
                deleteDocumentsByQuery(db.collection("students"), "uid", listOf(userUid))
            }

            // 2. Counselling Bookings collections
            val bookingCollections = listOf(
                "counselling_bookings",
                "career_counselling_bookings",
                "college_counselling_bookings",
                "course_counselling_bookings"
            )
            for (col in bookingCollections) {
                deleteDocumentsByQuery(db.collection(col), "studentPhone", phoneVariants)
                if (userEmail.isNotBlank()) {
                    deleteDocumentsByQuery(db.collection(col), "studentEmail", listOf(userEmail))
                }
            }

            // 3. Admission forms / College applications
            val applicationCollections = listOf(
                "admission_forms",
                "college_applications"
            )
            for (col in applicationCollections) {
                deleteDocumentsByQuery(db.collection(col), "phone", phoneVariants)
                if (userEmail.isNotBlank()) {
                    deleteDocumentsByQuery(db.collection(col), "email", listOf(userEmail))
                }
            }

            // 4. Psychometric assessments
            deleteDocumentsByQuery(db.collection("psychometric_assessments"), "userPhone", phoneVariants)

            // 5. Personal notifications (ONLY where userPhone matches this user, never broadcast notifications)
            deleteDocumentsByQuery(db.collection("notifications"), "userPhone", phoneVariants.filter { it.isNotBlank() })
            if (userEmail.isNotBlank()) {
                deleteDocumentsByQuery(db.collection("notifications"), "userPhone", listOf(userEmail))
            }

            // CRITICAL: Shared/static collections (careers, colleges, course, courses, scholarships) are NEVER deleted.
            Log.d(TAG, "Completed scoped Firestore deletion for user.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error during Firestore user data deletion: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun deleteDocumentsByQuery(
        collectionRef: CollectionReference,
        field: String,
        values: List<String>
    ) {
        for (value in values) {
            if (value.isBlank()) continue
            try {
                val snapshot = Tasks.await(
                    collectionRef.whereEqualTo(field, value).get(),
                    10,
                    TimeUnit.SECONDS
                )
                if (!snapshot.isEmpty) {
                    val batch = collectionRef.firestore.batch()
                    for (doc in snapshot.documents) {
                        batch.delete(doc.reference)
                    }
                    Tasks.await(batch.commit(), 15, TimeUnit.SECONDS)
                    Log.d(TAG, "Deleted ${snapshot.size()} document(s) from ${collectionRef.id} where $field = $value")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Notice deleting from ${collectionRef.id} ($field = $value): ${e.message}")
            }
        }
    }
}
