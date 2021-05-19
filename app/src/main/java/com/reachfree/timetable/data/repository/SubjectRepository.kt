package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.response.GradeListResponse
import com.reachfree.timetable.data.response.SubjectTypeResponse
import com.reachfree.timetable.data.response.TimetableResponse
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun insertSubject(subject: Subject)

    suspend fun insertSubjects(subjects: List<Subject>)

    suspend fun updateSubject(subject: Subject)

    suspend fun deleteSubject(subject: Subject)

    suspend fun deleteAllSubjects()

    suspend fun getSubjectById(subjectId: Long): Subject

    fun getSubjectByIdLiveData(subjectId: Long): LiveData<Subject>

    fun getAllSubjects(): LiveData<List<Subject>>

    fun getAllSubjectsWithSemesterInfo(): LiveData<List<GradeListResponse>>

    fun getAllSubjectsForWidgetService(semesterId: Long): List<Subject>

    fun getAllSubjectsByFlow(semesterId: Long): Flow<List<Subject>>

    fun getTotalCreditByType(): LiveData<List<SubjectTypeResponse>>

    fun getAllSubjectBySemester(semesterId: Long): LiveData<List<Subject>>

    fun getTotalCreditBySemester(semesterId: Long): LiveData<Int>

    fun getAllTimetableList(semesterId: Long, currentDate: Long): LiveData<List<TimetableResponse>>

}