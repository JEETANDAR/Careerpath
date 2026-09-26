package com.aistudio.carrerpath.counseling.data.local

import androidx.room.*
import com.aistudio.carrerpath.counseling.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EduDao {

    // Careers
    @Query("SELECT * FROM careers ORDER BY id ASC")
    fun getAllCareers(): Flow<List<CareerEntity>>

    @Query("SELECT * FROM careers WHERE id = :id")
    suspend fun getCareerById(id: Int): CareerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareer(career: CareerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareers(careers: List<CareerEntity>)

    @Query("DELETE FROM careers WHERE id = :id")
    suspend fun deleteCareer(id: Int)

    @Query("DELETE FROM careers")
    suspend fun deleteAllCareers()

    // Colleges
    @Query("SELECT * FROM colleges ORDER BY ranking ASC")
    fun getAllColleges(): Flow<List<CollegeEntity>>

    @Query("SELECT * FROM colleges WHERE isFeatured = 1 ORDER BY ranking ASC")
    fun getFeaturedColleges(): Flow<List<CollegeEntity>>

    @Query("SELECT * FROM colleges WHERE id = :id")
    suspend fun getCollegeById(id: Int): CollegeEntity?

    @Query("""
        SELECT * FROM colleges 
        WHERE name LIKE '%' || :query || '%' 
           OR location LIKE '%' || :query || '%' 
           OR coursesOffered LIKE '%' || :query || '%'
           OR state LIKE '%' || :query || '%'
        ORDER BY ranking ASC
    """)
    fun searchColleges(query: String): Flow<List<CollegeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollege(college: CollegeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColleges(colleges: List<CollegeEntity>)

    @Update
    suspend fun updateCollege(college: CollegeEntity)

    @Query("DELETE FROM colleges WHERE id = :id")
    suspend fun deleteCollege(id: Int)

    @Query("DELETE FROM colleges")
    suspend fun deleteAllColleges()

    // Courses
    @Query("SELECT * FROM courses ORDER BY name ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE category = :category ORDER BY name ASC")
    fun getCoursesByCategory(category: String): Flow<List<CourseEntity>>

    @Query("""
        SELECT * FROM courses 
        WHERE name LIKE '%' || :query || '%' 
           OR code LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchCourses(query: String): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteCourse(id: Int)

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()

    // Admission Forms
    @Query("SELECT * FROM admission_forms ORDER BY timestamp DESC")
    fun getAllAdmissionForms(): Flow<List<AdmissionFormEntity>>

    @Query("""
        SELECT * FROM admission_forms 
        WHERE (length(:email) > 0 AND email = :email) 
           OR (length(:phone) > 0 AND phone = :phone)
           OR (length(:phone) > 5 AND REPLACE(REPLACE(phone, '+', ''), ' ', '') LIKE '%' || REPLACE(REPLACE(:phone, '+', ''), ' ', '') || '%')
           OR (length(:email) > 0 AND phone = :email)
        ORDER BY timestamp DESC
    """)
    fun getAdmissionFormsForStudent(phone: String, email: String): Flow<List<AdmissionFormEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmissionForm(form: AdmissionFormEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmissionForms(forms: List<AdmissionFormEntity>)

    @Query("UPDATE admission_forms SET status = :status WHERE id = :id")
    suspend fun updateFormStatus(id: Int, status: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("""
        SELECT * FROM notifications 
        WHERE length(userPhone) = 0 
           OR userPhone = 'ALL' 
           OR (length(:phone) > 0 AND userPhone = :phone)
           OR (length(:email) > 0 AND userPhone = :email)
           OR (length(:phone) > 5 AND REPLACE(REPLACE(userPhone, '+', ''), ' ', '') = REPLACE(REPLACE(:phone, '+', ''), ' ', ''))
        ORDER BY timestamp DESC
    """)
    fun getNotificationsForStudent(phone: String, email: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Int)

    // Scholarships
    @Query("SELECT * FROM scholarships ORDER BY id ASC")
    fun getAllScholarships(): Flow<List<ScholarshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScholarships(scholarships: List<ScholarshipEntity>)

    // Saved Colleges
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCollege(saved: SavedCollegeEntity)

    @Query("DELETE FROM saved_colleges WHERE userId = :userId AND collegeId = :collegeId")
    suspend fun unsaveCollege(userId: String, collegeId: Int)

    @Query("SELECT collegeId FROM saved_colleges WHERE userId = :userId")
    fun getSavedCollegeIds(userId: String): Flow<List<Int>>

    @Query("SELECT c.* FROM colleges c INNER JOIN saved_colleges s ON c.id = s.collegeId WHERE s.userId = :userId ORDER BY c.ranking ASC")
    fun getSavedColleges(userId: String): Flow<List<CollegeEntity>>

    // Student Profiles
    @Query("SELECT * FROM students WHERE phone = :phone OR email = :phone LIMIT 1")
    fun getStudentByPhone(phone: String): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM students WHERE phone = :phone OR email = :phone LIMIT 1")
    suspend fun getStudentByPhoneOneShot(phone: String): StudentProfileEntity?

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    fun getStudentByEmail(email: String): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    suspend fun getStudentByEmailOneShot(email: String): StudentProfileEntity?

    @Query("SELECT * FROM students ORDER BY createdAt DESC")
    fun getAllStudents(): Flow<List<StudentProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentProfileEntity>)

    // Counselling Bookings
    @Query("SELECT * FROM counselling_bookings ORDER BY timestamp DESC")
    fun getAllCounsellingBookings(): Flow<List<CounsellingBookingEntity>>

    @Query("""
        SELECT * FROM counselling_bookings 
        WHERE (length(:email) > 0 AND studentEmail = :email) 
           OR (length(:phone) > 0 AND studentPhone = :phone)
           OR (length(:phone) > 5 AND REPLACE(REPLACE(studentPhone, '+', ''), ' ', '') LIKE '%' || REPLACE(REPLACE(:phone, '+', ''), ' ', '') || '%')
           OR (length(:email) > 0 AND studentPhone = :email)
        ORDER BY timestamp DESC
    """)
    fun getBookingsForStudent(phone: String, email: String): Flow<List<CounsellingBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounsellingBooking(booking: CounsellingBookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounsellingBookings(bookings: List<CounsellingBookingEntity>)

    @Query("UPDATE counselling_bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    // Psychometric Assessments
    @Query("SELECT * FROM psychometric_assessments WHERE userPhone = :phone OR userPhone LIKE '%' || :phone || '%' ORDER BY timestamp DESC LIMIT 1")
    fun getLatestAssessmentForStudent(phone: String): Flow<PsychometricAssessmentEntity?>

    @Query("SELECT * FROM psychometric_assessments ORDER BY timestamp DESC")
    fun getAllAssessments(): Flow<List<PsychometricAssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: PsychometricAssessmentEntity): Long

    // Account Deletion - Local user data cleanup
    @Query("DELETE FROM students WHERE phone = :phone OR email = :phone OR (length(:email) > 0 AND email = :email)")
    suspend fun deleteStudent(phone: String, email: String)

    @Query("DELETE FROM counselling_bookings WHERE studentPhone = :phone OR (length(:email) > 0 AND studentEmail = :email)")
    suspend fun deleteBookingsForStudent(phone: String, email: String)

    @Query("DELETE FROM admission_forms WHERE phone = :phone OR (length(:email) > 0 AND email = :email)")
    suspend fun deleteAdmissionFormsForStudent(phone: String, email: String)

    @Query("DELETE FROM psychometric_assessments WHERE userPhone = :phone OR userPhone LIKE '%' || :phone || '%'")
    suspend fun deleteAssessmentsForStudent(phone: String)

    @Query("DELETE FROM saved_colleges WHERE userId = :userId")
    suspend fun deleteSavedCollegesForUser(userId: String)

    @Query("DELETE FROM notifications WHERE length(userPhone) > 0 AND (userPhone = :phone OR (length(:email) > 0 AND userPhone = :email))")
    suspend fun deletePersonalNotificationsForStudent(phone: String, email: String)

    @Query("DELETE FROM counselling_bookings WHERE studentName = 'Sample Student' OR studentEmail = 'student@carrerpath.org' OR couponCode = 'EARLYBIRD'")
    suspend fun deleteSampleBookings()

    @Query("DELETE FROM admission_forms WHERE studentName = 'Sample Student' OR email = 'student@carrerpath.org'")
    suspend fun deleteSampleAdmissionForms()

    @Query("DELETE FROM students WHERE phone = '+919876543210' OR email = 'student@carrerpath.org' OR name = 'Student User'")
    suspend fun deleteSampleStudents()
}
