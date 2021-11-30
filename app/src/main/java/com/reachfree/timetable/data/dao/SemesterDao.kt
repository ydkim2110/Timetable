package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.SemesterResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester)

    @Update
    suspend fun updateSemester(semester: Semester)

    @Query("DELETE FROM semesters WHERE id LIKE :semesterId")
    suspend fun deleteSemesterById(semesterId: Long)

    @Query("DELETE FROM semesters")
    suspend fun deleteAllSemesters()

    @Query("SELECT * FROM semesters WHERE id LIKE :semesterId LIMIT 1")
    suspend fun getSemesterById(semesterId: Long): Semester

    @Query("SELECT * FROM semesters ORDER BY end_date DESC")
    suspend fun getAllSemesters(): List<Semester>

    @Query("SELECT * FROM semesters WHERE start_date <= :date AND end_date >= :date LIMIT 1")
    suspend fun getSemester(date: Long): Semester

    @Query("SELECT * FROM semesters ORDER BY end_date DESC LIMIT 1")
    fun getLatestSemester(): LiveData<Semester>

    @Query("""
        SELECT *
        FROM semesters
        WHERE id LIKE (SELECT semester_id
                        FROM subjects
                       WHERE id LIKE (SELECT subject_id
                                        FROM tasks WHERE id LIKE :taskId))
    """)
    suspend fun getSemesterByTaskId(taskId: Long): Semester

    @Query("SELECT * FROM semesters ORDER BY end_date DESC")
    fun getAllSemestersLiveData(): LiveData<List<Semester>>

    @Query("""
        SELECT
            x.id,
            x.title,
            x.description,
            x.start_date,
            x.end_date,
            (SELECT SUM(credit) FROM subjects WHERE semester_id = x.id)  AS total_count
        FROM semesters AS x
        ORDER BY x.end_date DESC
    """)
    fun getAllSemestersWithTotalCount(): LiveData<List<SemesterResponse>>

    @Query("SELECT * FROM semesters WHERE start_date <= :date AND end_date >= :date LIMIT 1")
    fun getSemesterLiveData(date: Long): LiveData<Semester>

    @Query("SELECT * FROM semesters WHERE start_date <= :date AND end_date >= :date LIMIT 1")
    fun getSemesterForWidgetService(date: Long): Semester?

    @Query("SELECT * FROM semesters WHERE start_date <= :date AND end_date >= :date LIMIT 1")
    fun getSemesterByFlow(date: Long): Flow<Semester>

    @Query("SELECT * FROM semesters WHERE id LIKE :semesterId LIMIT 1")
    fun getSemesterByIdLiveData(semesterId: Long): LiveData<Semester>
}