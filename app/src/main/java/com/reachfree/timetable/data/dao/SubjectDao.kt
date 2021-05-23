package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.GradeListResponse
import com.reachfree.timetable.data.response.SubjectTypeResponse
import com.reachfree.timetable.data.response.TimetableResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertSubject(subject: Subject)

    @Insert
    suspend fun insertSubjects(subjects: List<Subject>)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()

    @Query("SELECT * FROM subjects WHERE id LIKE :subjectId")
    suspend fun getSubjectById(subjectId: Long): Subject

    @Query("""
        UPDATE
            subjects
        SET 
            grade = CASE 
            WHEN grade = 'A-' THEN 'A'
            WHEN grade = 'B-' THEN 'B'
            WHEN grade = 'C-' THEN 'C'
            WHEN grade = 'D-' THEN 'D'
            ELSE grade
            END
        WHERE grade IN ('A-', 'B-', 'C-', 'D-')
    """)
    suspend fun updateGradeToConvert()

    @Query("SELECT * FROM subjects WHERE id LIKE :subjectId")
    fun getSubjectByIdLiveData(subjectId: Long): LiveData<Subject>

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Query("SELECT * FROM subjects WHERE semester_id LIKE :semesterId")
    fun getAllSubjectsForWidgetService(semesterId: Long): List<Subject>

    @Query("SELECT * FROM subjects WHERE semester_id LIKE :semesterId")
    fun getAllSubjectsByFlow(semesterId: Long): Flow<List<Subject>>

    @Query("SELECT type, SUM(credit) AS total_credit FROM subjects GROUP BY type")
    fun getTotalCreditByType(): LiveData<List<SubjectTypeResponse>>

    @Query("SELECT * FROM subjects WHERE semester_id LIKE :semesterId")
    fun getAllSubjectsBySemester(semesterId: Long): LiveData<List<Subject>>

    @Query("SELECT SUM(credit) FROM subjects WHERE semester_id LIKE :semesterId")
    fun getTotalCreditBySemester(semesterId: Long): LiveData<Int>

    @Query("""
        SELECT
            x.id,
            x.title,
            x.description,
            x.classroom,
            x.building_name,
            x.credit,
            x.type,
            x.grade,
            x.professor_name,
            x.book_name,
            x.background_color,
            x.days,
            x.semester_id,
            y.title AS semester_title,
            y.start_date AS semester_start_date,
            y.end_date AS semester_end_date
        FROM subjects x
        INNER JOIN semesters y ON x.semester_id = y.id
    """)
    fun getAllSubjectsWithSemesterInfo(): LiveData<List<GradeListResponse>>

    @Query("""
        SELECT 
            id,
            0 AS category,
            title,
            description,
            days,
            0 AS start_date,
            0 AS end_date,
            classroom,
            building_name,
            background_color
        FROM subjects 
        WHERE semester_id LIKE :semesterId
        UNION
        SELECT
            id,
            1 AS category,
            title,
            '' AS description,
            days,
            start_date,
            end_date,
            '' AS classroom,
            '' AS building_name,
            background_color
        FROM part_time_jobs
        WHERE end_date >= :currentDate AND start_date <= :currentDate
    """)
    fun getAllTimetableList(semesterId: Long, currentDate: Long): LiveData<List<TimetableResponse>>

}