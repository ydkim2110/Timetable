package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.SubjectTypeResponse

interface SubjectRepository {

    suspend fun insertSubject(subject: Subject)

    suspend fun insertSubjects(subjects: List<Subject>)

    suspend fun deleteAllSubjects()

    fun getAllSubjects(): LiveData<List<Subject>>

    fun getTotalCreditByType(): LiveData<List<SubjectTypeResponse>>

    fun getAllSubjectBySemester(semesterId: Long): LiveData<List<Subject>>

    fun getTotalCreditBySemester(semesterId: Long): LiveData<Int>
}