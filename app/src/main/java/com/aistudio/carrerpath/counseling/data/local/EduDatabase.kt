package com.aistudio.carrerpath.counseling.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aistudio.carrerpath.counseling.data.model.*

@Database(
    entities = [
        CareerEntity::class,
        CollegeEntity::class,
        CourseEntity::class,
        AdmissionFormEntity::class,
        NotificationEntity::class,
        ScholarshipEntity::class,
        SavedCollegeEntity::class,
        StudentProfileEntity::class,
        CounsellingBookingEntity::class,
        PsychometricAssessmentEntity::class
    ],
    version = 12,
    exportSchema = false
)
abstract class EduDatabase : RoomDatabase() {
    abstract fun eduDao(): EduDao

    companion object {
        @Volatile
        private var INSTANCE: EduDatabase? = null

        fun getDatabase(context: Context): EduDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduDatabase::class.java,
                    "eduverse_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
