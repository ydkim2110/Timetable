package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.reachfree.timetable.data.model.Subject

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertSubject(subject: Subject)

    @Insert
    suspend fun insertSubjects(subjects: List<Subject>)

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()

    @Query("SELECT * FROM subjects WHERE semester_id LIKE :semesterId")
    fun getAllSubjectsBySemester(semesterId: Long): LiveData<List<Subject>>

    @Query("SELECT SUM(credit) FROM subjects WHERE semester_id LIKE :semesterId")
    fun getTotalCreditBySemester(semesterId: Long): LiveData<Int>

}