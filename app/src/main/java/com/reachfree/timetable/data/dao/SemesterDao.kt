package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.SemesterTotalCreditResponse

@Dao
interface SemesterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester)

    @Query("DELETE FROM semesters")
    suspend fun deleteAllSemesters()

    @Query("SELECT * FROM semesters ORDER BY end_date DESC")
    fun getAllSemesters(): LiveData<List<Semester>>

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
    fun getAllSemestersWithTotalCount(): LiveData<List<SemesterTotalCreditResponse>>

    @Query("SELECT * FROM semesters WHERE start_date <= :date AND end_date >= :date LIMIT 1")
    fun getSemester(date: Long): LiveData<Semester>
}