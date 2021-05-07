package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.SubjectDao
import com.reachfree.timetable.data.model.Subject
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao
) : SubjectRepository {

    override suspend fun insertSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
    }

    override suspend fun insertSubjects(subjects: List<Subject>) {
        subjectDao.insertSubjects(subjects)
    }

    override suspend fun deleteAllSubjects() {
        subjectDao.deleteAllSubjects()
    }

    override fun getAllSubjectBySemester(semesterId: Long): LiveData<List<Subject>> {
        return subjectDao.getAllSubjectsBySemester(semesterId)
    }

}