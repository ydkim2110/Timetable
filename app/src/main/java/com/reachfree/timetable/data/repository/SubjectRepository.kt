package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Subject

interface SubjectRepository {

    suspend fun insertSubject(subject: Subject)

    suspend fun insertSubjects(subjects: List<Subject>)

    suspend fun deleteAllSubjects()

    fun getAllSubjectBySemester(semesterId: Long): LiveData<List<Subject>>
}