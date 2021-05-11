package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.SubjectTypeResponse

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertSubject(subject: Subject)

    @Insert
    suspend fun insertSubjects(subjects: List<Subject>)

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Query("SELECT type, SUM(credit) AS total_credit FROM subjects GROUP BY type")
    fun getTotalCreditByType(): LiveData<List<SubjectTypeResponse>>

    @Query("SELECT * FROM subjects WHERE semester_id LIKE :semesterId")
    fun getAllSubjectsBySemester(semesterId: Long): LiveData<List<Subject>>

    @Query("SELECT SUM(credit) FROM subjects WHERE semester_id LIKE :semesterId")
    fun getTotalCreditBySemester(semesterId: Long): LiveData<Int>

}