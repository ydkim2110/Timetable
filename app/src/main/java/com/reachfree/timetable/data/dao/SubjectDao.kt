package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.SubjectTypeResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertSubject(subject: Subject)

    @Insert
    suspend fun insertSubjects(subjects: List<Subject>)

    @Update
    fun updateSubject(subject: Subject)

    @Delete
    fun deleteSubject(subject: Subject)

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()

    @Query("SELECT * FROM subjects WHERE id LIKE :subjectId")
    fun getSubjectByIdLiveData(subjectId: Long): LiveData<Subject>

    @Query("SELECT * FROM subjects WHERE id LIKE :subjectId")
    suspend fun getSubjectById(subjectId: Long): Subject

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

}