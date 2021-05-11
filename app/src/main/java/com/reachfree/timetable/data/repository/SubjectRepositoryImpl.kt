package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.SubjectDao
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.SubjectTypeResponse
import kotlinx.coroutines.flow.Flow
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

    override fun getAllSubjects(): LiveData<List<Subject>> {
        return subjectDao.getAllSubjects()
    }

    override fun getAllSubjectsForWidgetService(semesterId: Long): List<Subject> {
        return subjectDao.getAllSubjectsForWidgetService(semesterId)
    }

    override fun getAllSubjectsByFlow(semesterId: Long): Flow<List<Subject>> {
        return subjectDao.getAllSubjectsByFlow(semesterId)
    }

    override fun getTotalCreditByType(): LiveData<List<SubjectTypeResponse>> {
        return subjectDao.getTotalCreditByType()
    }

    override fun getAllSubjectBySemester(semesterId: Long): LiveData<List<Subject>> {
        return subjectDao.getAllSubjectsBySemester(semesterId)
    }

    override fun getTotalCreditBySemester(semesterId: Long): LiveData<Int> {
        return subjectDao.getTotalCreditBySemester(semesterId)
    }

}