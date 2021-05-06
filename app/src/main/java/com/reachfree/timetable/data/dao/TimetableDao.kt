package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reachfree.timetable.data.model.Semester

@Dao
interface TimetableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester)

    @Query("SELECT * FROM semesters")
    fun getAllSemesters(): LiveData<List<Semester>>

}