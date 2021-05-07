package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.timetable.data.model.Semester

@Dao
interface SemesterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester)

    @Query("DELETE FROM semesters")
    suspend fun deleteAllSemesters()

    @Query("SELECT * FROM semesters")
    fun getAllSemesters(): LiveData<List<Semester>>

    @Query("SELECT * FROM semesters WHERE start_date <= :date AND end_date >= :date")
    fun getSemester(date: Long): LiveData<Semester>
}