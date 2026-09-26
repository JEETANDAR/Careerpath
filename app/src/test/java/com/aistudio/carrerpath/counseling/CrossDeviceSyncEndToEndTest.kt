package com.aistudio.carrerpath.counseling

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aistudio.carrerpath.counseling.data.local.EduDao
import com.aistudio.carrerpath.counseling.data.local.EduDatabase
import com.aistudio.carrerpath.counseling.data.model.AdmissionFormEntity
import com.aistudio.carrerpath.counseling.data.model.CounsellingBookingEntity
import com.aistudio.carrerpath.counseling.data.model.StudentProfileEntity
import com.aistudio.carrerpath.counseling.data.repository.EduRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CrossDeviceSyncEndToEndTest {

    private lateinit var context: Context

    // Phone A: Student Device Database & Repository
    private lateinit var phoneADb: EduDatabase
    private lateinit var phoneADao: EduDao
    private lateinit var phoneARepository: EduRepository

    // Phone B: Admin Device Database & Repository
    private lateinit var phoneBDb: EduDatabase
    private lateinit var phoneBDao: EduDao
    private lateinit var phoneBRepository: EduRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        // Initialize Phone A (Student's isolated local device storage)
        phoneADb = Room.inMemoryDatabaseBuilder(context, EduDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        phoneADao = phoneADb.eduDao()
        phoneARepository = EduRepository(phoneADao, context)

        // Initialize Phone B (Admin's separate isolated device storage - completely empty initially)
        phoneBDb = Room.inMemoryDatabaseBuilder(context, EduDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        phoneBDao = phoneBDb.eduDao()
        phoneBRepository = EduRepository(phoneBDao, context)
    }

    @After
    fun tearDown() {
        phoneADb.close()
        phoneBDb.close()
    }

    @Test
    fun test_studentSignsUpOnPhoneA_adminSeesStudentAndAllDetailsOnPhoneB_PASS() = runBlocking {
        // =========================================================================
        // STEP 1: Student on Phone A signs up and enters complete details
        // =========================================================================
        val studentOnPhoneA = StudentProfileEntity(
            phone = "+919663926665",
            name = "Rohan Kumar Sharma",
            email = "rohan.sharma@example.com",
            institution = "National Public PU College",
            gender = "Male",
            city = "Bengaluru",
            state = "Karnataka",
            country = "India",
            studentClass = "12th Science (PCMB)",
            percentageMark = 93.4,
            isAdmin = false,
            uid = "firebase_uid_rohan_phone_a_9663926665",
            createdAt = System.currentTimeMillis()
        )

        // Phone A saves to its local database
        phoneARepository.saveStudentProfile(studentOnPhoneA)

        // Phone A serializes student data for Firestore cloud persistence
        val cloudFirestorePayload: Map<String, Any> = phoneARepository.studentProfileToMap(studentOnPhoneA)

        // Verify the payload contains all necessary cross-device sync fields
        assertEquals("+919663926665", cloudFirestorePayload["phone"])
        assertEquals("Rohan Kumar Sharma", cloudFirestorePayload["name"])
        assertEquals("rohan.sharma@example.com", cloudFirestorePayload["email"])
        assertEquals("National Public PU College", cloudFirestorePayload["institution"])
        assertEquals("12th Science (PCMB)", cloudFirestorePayload["studentClass"])
        assertEquals("Male", cloudFirestorePayload["gender"])
        assertEquals("Bengaluru", cloudFirestorePayload["city"])
        assertEquals("Karnataka", cloudFirestorePayload["state"])
        assertEquals("India", cloudFirestorePayload["country"])
        assertEquals(93.4, cloudFirestorePayload["percentageMark"])
        assertEquals("firebase_uid_rohan_phone_a_9663926665", cloudFirestorePayload["uid"])

        // =========================================================================
        // STEP 2: Phone B (Admin Device) initially has 0 students in its storage
        // =========================================================================
        val initialPhoneBStudents = phoneBRepository.allStudents.first()
        assertTrue("Phone B should start with zero students", initialPhoneBStudents.isEmpty())

        // =========================================================================
        // STEP 3: Admin on Phone B logs in and fetches/receives data from Firestore
        // =========================================================================
        // Phone B receives the cloud document payload from Firestore
        val studentParsedOnPhoneB = phoneBRepository.parseStudentFromMap(
            docId = "+919663926665",
            data = cloudFirestorePayload
        )

        assertNotNull("Phone B must successfully parse the Firestore student payload", studentParsedOnPhoneB)

        // Phone B ingests the remote student data into its state and local database
        phoneBRepository.ingestRemoteStudents(listOf(studentParsedOnPhoneB!!))

        // =========================================================================
        // STEP 4: Verify Admin on Phone B sees the new student and ALL their details
        // =========================================================================
        val phoneBStudents = phoneBRepository.allStudents.first()
        assertEquals(1, phoneBStudents.size)

        val adminViewedStudent = phoneBStudents.first()

        // 1. Phone number
        assertEquals("+919663926665", adminViewedStudent.phone)
        // 2. Full Name
        assertEquals("Rohan Kumar Sharma", adminViewedStudent.name)
        // 3. Email
        assertEquals("rohan.sharma@example.com", adminViewedStudent.email)
        // 4. Institution / College / School
        assertEquals("National Public PU College", adminViewedStudent.institution)
        // 5. Academic Standard / Class / Stream
        assertEquals("12th Science (PCMB)", adminViewedStudent.studentClass)
        // 6. Gender
        assertEquals("Male", adminViewedStudent.gender)
        // 7. Academic Marks / Percentage
        assertEquals(93.4, adminViewedStudent.percentageMark, 0.01)
        // 8. Location: City, State, Country
        assertEquals("Bengaluru", adminViewedStudent.city)
        assertEquals("Karnataka", adminViewedStudent.state)
        assertEquals("India", adminViewedStudent.country)
        // 9. Firebase Auth UID
        assertEquals("firebase_uid_rohan_phone_a_9663926665", adminViewedStudent.uid)
        // 10. Role
        assertFalse(adminViewedStudent.isAdmin)

        // =========================================================================
        // STEP 5: Verify Admin Search & Filtering on Phone B locates this student
        // =========================================================================
        // Search by partial name
        val matchedByName = phoneBStudents.filter { it.name.contains("Rohan", ignoreCase = true) }
        assertEquals(1, matchedByName.size)

        // Search by phone digits
        val matchedByPhone = phoneBStudents.filter { it.phone.contains("9663926665") }
        assertEquals(1, matchedByPhone.size)

        // Search by stream/class
        val matchedByClass = phoneBStudents.filter { it.studentClass.contains("Science", ignoreCase = true) }
        assertEquals(1, matchedByClass.size)
    }

    @Test
    fun test_studentSubmitsAdmissionFormOnPhoneA_adminSeesItOnPhoneB_PASS() = runBlocking {
        // Student on Phone A submits an admission application form
        val admissionFormOnPhoneA = AdmissionFormEntity(
            id = 101,
            studentName = "Priya V. Rao",
            phone = "+919887766554",
            email = "priya.rao@example.com",
            qualification = "12th Standard",
            percentageMark = 96.2,
            preferredCollege = "RV College of Engineering",
            preferredCourse = "B.Tech Computer Science & Engineering",
            city = "Bengaluru",
            notes = "Interested in AI & Data Science specialization",
            status = "Pending",
            timestamp = System.currentTimeMillis()
        )

        // Save on Phone A
        phoneADao.insertAdmissionForm(admissionFormOnPhoneA)

        // Serialized via admissionFormToMap
        val payload = phoneARepository.admissionFormToMap(admissionFormOnPhoneA)
        assertEquals("Priya V. Rao", payload["studentName"])
        assertEquals("+919887766554", payload["phone"])
        assertEquals("RV College of Engineering", payload["preferredCollege"])

        // Phone B Admin receives and parses this application from Firestore
        val parsedForm = phoneBRepository.parseAdmissionFormFromMap("101", payload)
        assertNotNull("Admin must parse admission form", parsedForm)
        phoneBRepository.ingestRemoteAdmissionForms(listOf(parsedForm!!))

        val adminAdmissionForms = phoneBRepository.allAdmissionForms.first()
        assertEquals(1, adminAdmissionForms.size)

        val adminForm = adminAdmissionForms.first()
        assertEquals("Priya V. Rao", adminForm.studentName)
        assertEquals("+919887766554", adminForm.phone)
        assertEquals("RV College of Engineering", adminForm.preferredCollege)
        assertEquals("B.Tech Computer Science & Engineering", adminForm.preferredCourse)
        assertEquals(96.2, adminForm.percentageMark, 0.01)
        assertEquals("Pending", adminForm.status)

        // Admin updates status on Phone B to "Shortlisted"
        phoneBRepository.updateAdmissionFormStatus(adminForm.id, "Shortlisted")
        val updatedAdminForms = phoneBRepository.allAdmissionForms.first()
        assertEquals("Shortlisted", updatedAdminForms.first().status)
    }

    @Test
    fun test_studentBooksCounsellingOnPhoneA_adminSeesBookingOnPhoneB_PASS() = runBlocking {
        // Student on Phone A books a counselling session
        val bookingOnPhoneA = CounsellingBookingEntity(
            id = 201,
            counsellorName = "Dr. Ananya Sharma",
            studentName = "Kavya Murthy",
            studentPhone = "+919112233445",
            studentEmail = "kavya@example.com",
            studentClass = "12th Standard",
            bookingDate = "2026-09-25",
            timeSlot = "04:00 PM - 05:00 PM",
            status = "Pending",
            targetCourse = "MBBS / Medical Sciences",
            timestamp = System.currentTimeMillis()
        )

        // Save on Phone A
        phoneADao.insertCounsellingBooking(bookingOnPhoneA)

        // Serialize to Firestore map
        val bookingPayload = phoneARepository.counsellingBookingToMap(bookingOnPhoneA)
        assertEquals("Kavya Murthy", bookingPayload["studentName"])
        assertEquals("+919112233445", bookingPayload["studentPhone"])
        assertEquals("Dr. Ananya Sharma", bookingPayload["counsellorName"])

        // Phone B Admin receives and parses this booking from Firestore
        val parsedBooking = phoneBRepository.parseBookingFromMap("201", bookingPayload)
        assertNotNull("Admin must parse booking", parsedBooking)
        phoneBRepository.ingestRemoteBookings(listOf(parsedBooking!!))

        val adminBookings = phoneBRepository.allCounsellingBookings.first()
        assertEquals(1, adminBookings.size)

        val adminBooking = adminBookings.first()
        assertEquals("Kavya Murthy", adminBooking.studentName)
        assertEquals("+919112233445", adminBooking.studentPhone)
        assertEquals("Dr. Ananya Sharma", adminBooking.counsellorName)
        assertEquals("2026-09-25", adminBooking.bookingDate)
        assertEquals("04:00 PM - 05:00 PM", adminBooking.timeSlot)
        assertEquals("MBBS / Medical Sciences", adminBooking.targetCourse)
        assertEquals("Pending", adminBooking.status)

        // Admin on Phone B confirms the booking
        phoneBRepository.updateCounsellingBookingStatus(adminBooking.id, "Confirmed")
        val updatedBookings = phoneBRepository.allCounsellingBookings.first()
        assertEquals("Confirmed", updatedBookings.first().status)
    }

    @Test
    fun test_multipleStudentsFromDifferentDevices_doNotOverwriteEachOther_PASS() = runBlocking {
        // Device 1: Student Rohan Sharma
        val student1 = StudentProfileEntity(
            phone = "+919663926665",
            name = "Rohan Kumar Sharma",
            email = "rohan.sharma@example.com",
            institution = "National Public PU College",
            gender = "Male",
            city = "Bengaluru",
            state = "Karnataka",
            country = "India",
            studentClass = "12th Science (PCMB)",
            percentageMark = 93.4,
            isAdmin = false,
            uid = "uid_device_1_rohan",
            createdAt = 1000001L
        )

        // Device 2: Student Sneha Kulkarni
        val student2 = StudentProfileEntity(
            phone = "+918123456789",
            name = "Sneha Kulkarni",
            email = "sneha.k@example.com",
            institution = "St. Joseph's PU College",
            gender = "Female",
            city = "Hubballi",
            state = "Karnataka",
            country = "India",
            studentClass = "12th Commerce (EBAC)",
            percentageMark = 91.5,
            isAdmin = false,
            uid = "uid_device_2_sneha",
            createdAt = 1000002L
        )

        // Device 3: Student Aditya Verma
        val student3 = StudentProfileEntity(
            phone = "+917001122334",
            name = "Aditya Verma",
            email = "aditya.v@example.com",
            institution = "Delhi Public School",
            gender = "Male",
            city = "Mysuru",
            state = "Karnataka",
            country = "India",
            studentClass = "10th Standard",
            percentageMark = 88.0,
            isAdmin = false,
            uid = "uid_device_3_aditya",
            createdAt = 1000003L
        )

        // Simulate each device writing to Firestore
        val payload1 = phoneARepository.studentProfileToMap(student1)
        val payload2 = phoneARepository.studentProfileToMap(student2)
        val payload3 = phoneARepository.studentProfileToMap(student3)

        // Admin Device (Phone B) fetches all documents from Firestore
        val parsed1 = phoneBRepository.parseStudentFromMap("+919663926665", payload1)!!
        val parsed2 = phoneBRepository.parseStudentFromMap("+918123456789", payload2)!!
        val parsed3 = phoneBRepository.parseStudentFromMap("+917001122334", payload3)!!

        // Ingest into Phone B
        phoneBRepository.ingestRemoteStudents(listOf(parsed1, parsed2, parsed3))

        val allAdminStudents = phoneBRepository.allStudents.first()

        // Verify exactly 3 students exist and NONE were overwritten
        assertEquals(3, allAdminStudents.size)

        val foundRohan = allAdminStudents.firstOrNull { it.phone == "+919663926665" }
        assertNotNull("Rohan must be present", foundRohan)
        assertEquals("Rohan Kumar Sharma", foundRohan!!.name)
        assertEquals("National Public PU College", foundRohan.institution)
        assertEquals(93.4, foundRohan.percentageMark, 0.01)

        val foundSneha = allAdminStudents.firstOrNull { it.phone == "+918123456789" }
        assertNotNull("Sneha must be present", foundSneha)
        assertEquals("Sneha Kulkarni", foundSneha!!.name)
        assertEquals("St. Joseph's PU College", foundSneha.institution)
        assertEquals(91.5, foundSneha.percentageMark, 0.01)

        val foundAditya = allAdminStudents.firstOrNull { it.phone == "+917001122334" }
        assertNotNull("Aditya must be present", foundAditya)
        assertEquals("Aditya Verma", foundAditya!!.name)
        assertEquals("Delhi Public School", foundAditya.institution)
        assertEquals(88.0, foundAditya.percentageMark, 0.01)
    }

    @Test
    fun test_multipleAdmissionFormsFromDifferentDevices_doNotOverwriteEachOther_PASS() = runBlocking {
        val form1 = AdmissionFormEntity(
            id = 501,
            studentName = "Priya Rao",
            phone = "+919887766554",
            email = "priya@example.com",
            qualification = "12th Standard",
            percentageMark = 96.2,
            preferredCollege = "RV College of Engineering",
            preferredCourse = "B.Tech Computer Science",
            city = "Bengaluru",
            notes = "AI branch",
            status = "Pending",
            timestamp = 1000100L
        )

        val form2 = AdmissionFormEntity(
            id = 502,
            studentName = "Amit Patel",
            phone = "+919822334455",
            email = "amit@example.com",
            qualification = "12th Standard",
            percentageMark = 94.0,
            preferredCollege = "BMS College of Engineering",
            preferredCourse = "B.Tech AI & ML",
            city = "Bengaluru",
            notes = "Cybersecurity interest",
            status = "Pending",
            timestamp = 1000200L
        )

        val form3 = AdmissionFormEntity(
            id = 503,
            studentName = "Deepa Nair",
            phone = "+919733445566",
            email = "deepa@example.com",
            qualification = "12th Standard",
            percentageMark = 98.1,
            preferredCollege = "St. John's Medical College",
            preferredCourse = "MBBS",
            city = "Bengaluru",
            notes = "NEET Score 685",
            status = "Pending",
            timestamp = 1000300L
        )

        val map1 = phoneARepository.admissionFormToMap(form1)
        val map2 = phoneARepository.admissionFormToMap(form2)
        val map3 = phoneARepository.admissionFormToMap(form3)

        val parsedList = listOf(
            phoneBRepository.parseAdmissionFormFromMap("501", map1)!!,
            phoneBRepository.parseAdmissionFormFromMap("502", map2)!!,
            phoneBRepository.parseAdmissionFormFromMap("503", map3)!!
        )

        phoneBRepository.ingestRemoteAdmissionForms(parsedList)

        val adminForms = phoneBRepository.allAdmissionForms.first()
        assertEquals(3, adminForms.size)

        // Check each student's distinct application details
        val f1 = adminForms.first { it.id == 501 }
        assertEquals("Priya Rao", f1.studentName)
        assertEquals("RV College of Engineering", f1.preferredCollege)

        val f2 = adminForms.first { it.id == 502 }
        assertEquals("Amit Patel", f2.studentName)
        assertEquals("BMS College of Engineering", f2.preferredCollege)

        val f3 = adminForms.first { it.id == 503 }
        assertEquals("Deepa Nair", f3.studentName)
        assertEquals("St. John's Medical College", f3.preferredCollege)

        // Admin updates status of form 501 to "Shortlisted" and form 502 to "Approved"
        phoneBRepository.updateAdmissionFormStatus(501, "Shortlisted")
        phoneBRepository.updateAdmissionFormStatus(502, "Approved")

        val updatedForms = phoneBRepository.allAdmissionForms.first()
        assertEquals("Shortlisted", updatedForms.first { it.id == 501 }.status)
        assertEquals("Approved", updatedForms.first { it.id == 502 }.status)
        assertEquals("Pending", updatedForms.first { it.id == 503 }.status)
    }

    @Test
    fun test_multipleCounsellingBookingsAcrossDevices_allPreservedInAdminPanel_PASS() = runBlocking {
        val b1 = CounsellingBookingEntity(
            id = 601,
            counsellorName = "Dr. Ananya Sharma",
            studentName = "Kavya Murthy",
            studentPhone = "+919112233445",
            studentEmail = "kavya@example.com",
            studentClass = "12th Standard",
            bookingDate = "2026-09-25",
            timeSlot = "04:00 PM - 05:00 PM",
            status = "Pending",
            targetCourse = "MBBS / Medical Sciences",
            bookingType = "Course Counselling",
            timestamp = 2000100L
        )

        val b2 = CounsellingBookingEntity(
            id = 602,
            counsellorName = "Nimi",
            studentName = "Rahul Jain",
            studentPhone = "+919223344556",
            studentEmail = "rahul@example.com",
            studentClass = "12th Standard",
            bookingDate = "2026-09-26",
            timeSlot = "11:00 AM - 12:00 PM",
            status = "Pending",
            targetCourse = "B.Tech Cyber Security",
            bookingType = "Career Counselling",
            timestamp = 2000200L
        )

        val b3 = CounsellingBookingEntity(
            id = 603,
            counsellorName = "Dr. Rajesh Rao",
            studentName = "Meera S.",
            studentPhone = "+919334455667",
            studentEmail = "meera@example.com",
            studentClass = "Graduate",
            bookingDate = "2026-09-27",
            timeSlot = "02:00 PM - 03:00 PM",
            status = "Pending",
            targetCourse = "MBA International Business",
            bookingType = "College Counselling",
            timestamp = 2000300L
        )

        val m1 = phoneARepository.counsellingBookingToMap(b1)
        val m2 = phoneARepository.counsellingBookingToMap(b2)
        val m3 = phoneARepository.counsellingBookingToMap(b3)

        val parsedBookings = listOf(
            phoneBRepository.parseBookingFromMap("601", m1)!!,
            phoneBRepository.parseBookingFromMap("602", m2)!!,
            phoneBRepository.parseBookingFromMap("603", m3)!!
        )

        phoneBRepository.ingestRemoteBookings(parsedBookings)

        val adminBookings = phoneBRepository.allCounsellingBookings.first()
        assertEquals(3, adminBookings.size)

        // Verify distinct fields
        val adminB1 = adminBookings.first { it.id == 601 }
        assertEquals("Kavya Murthy", adminB1.studentName)
        assertEquals("Course Counselling", adminB1.bookingType)

        val adminB2 = adminBookings.first { it.id == 602 }
        assertEquals("Rahul Jain", adminB2.studentName)
        assertEquals("Career Counselling", adminB2.bookingType)

        val adminB3 = adminBookings.first { it.id == 603 }
        assertEquals("Meera S.", adminB3.studentName)
        assertEquals("College Counselling", adminB3.bookingType)

        // Admin confirms b1 and marks b3 completed
        phoneBRepository.updateCounsellingBookingStatus(601, "Confirmed")
        phoneBRepository.updateCounsellingBookingStatus(603, "Completed")

        val updated = phoneBRepository.allCounsellingBookings.first()
        assertEquals("Confirmed", updated.first { it.id == 601 }.status)
        assertEquals("Pending", updated.first { it.id == 602 }.status)
        assertEquals("Completed", updated.first { it.id == 603 }.status)
    }

    @Test
    fun test_firestorePayloadFallbackParsing_preservesAllFieldVariations_PASS() = runBlocking {
        // Raw map with alternative naming schemas commonly sent by different clients
        val irregularPayload = mapOf<String, Any?>(
            "fullName" to "Divya Prakash",
            "mobile" to "+919445566778",
            "studentEmail" to "divya.p@example.com",
            "school" to "Bishop Cotton Girls' School",
            "qualification" to "12th Arts",
            "percentage" to 95.8,
            "gender" to "Female",
            "city" to "Bengaluru",
            "state" to "Karnataka",
            "country" to "India",
            "uid" to "firebase_uid_divya_778",
            "timestamp" to 1700000000000L
        )

        val parsed = phoneBRepository.parseStudentFromMap("+919445566778", irregularPayload)
        assertNotNull("Must parse irregular map schema successfully", parsed)
        assertEquals("Divya Prakash", parsed!!.name)
        assertEquals("+919445566778", parsed.phone)
        assertEquals("Bishop Cotton Girls' School", parsed.institution)
        assertEquals("12th Arts", parsed.studentClass)
        assertEquals(95.8, parsed.percentageMark, 0.01)
        assertEquals("Female", parsed.gender)
        assertEquals("firebase_uid_divya_778", parsed.uid)
    }
}
