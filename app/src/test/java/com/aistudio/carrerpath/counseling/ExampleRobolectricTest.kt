package com.aistudio.carrerpath.counseling

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aistudio.carrerpath.counseling.data.local.DatabaseInitializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CareerPath", appName)
  }

  @Test
  fun `verify all 200 careers loaded with rich fields and kannada content`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val careers = DatabaseInitializer.loadCareersFromAssets(context)
    assertEquals(200, careers.size)

    val rawCareers = DatabaseInitializer.loadRawCareersJson(context)
    assertEquals(200, rawCareers.size)

    careers.forEach { career ->
      assertTrue("Career ID must be positive", career.id > 0)
      assertTrue("Career title should not be blank: ${career.id}", career.title.isNotBlank())
      assertTrue("Career Kannada title should not be blank: ${career.title}", career.titleKn.isNotBlank())
      assertTrue("Career image should not be blank: ${career.title}", career.image.isNotBlank())
    }
  }

  @Test
  fun `verify all 200 courses loaded with rich fields and kannada content`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val courses = DatabaseInitializer.loadCoursesFromAssets(context)
    assertEquals(200, courses.size)

    val rawCourses = DatabaseInitializer.loadRawCoursesJson(context)
    assertEquals(200, rawCourses.size)

    courses.forEach { course ->
      assertTrue("Course ID must be positive", course.id > 0)
      assertTrue("Course name should not be blank: ${course.id}", course.name.isNotBlank())
      assertTrue("Course Kannada name should not be blank: ${course.name}", course.nameKn.isNotBlank())
      assertTrue("Course image should not be blank: ${course.name}", course.imageUrl.isNotBlank())
    }
  }

  @Test
  fun `verify pathway json and text data for careers and courses`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val rawCareers = DatabaseInitializer.loadRawCareersJson(context)
    val firstCareer = rawCareers.first()
    assertTrue(firstCareer.containsKey("pathway"))
    val pathway = firstCareer["pathway"] as String
    assertTrue(pathway.contains("Step 1"))

    val rawCourses = DatabaseInitializer.loadRawCoursesJson(context)
    val firstCourse = rawCourses.first()
    assertTrue(firstCourse.containsKey("pathway"))
    val coursePathway = firstCourse["pathway"] as String
    assertTrue(coursePathway.isNotBlank())
  }
}
