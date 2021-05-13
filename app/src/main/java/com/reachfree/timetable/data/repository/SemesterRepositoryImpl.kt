package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.SemesterDao
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.SemesterResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SemesterRepositoryImpl @Inject constructor(
    private val semesterDao: SemesterDao
) : SemesterRepository {

    override suspend fun insertSemester(semester: Semester) {
        semesterDao.insertSemester(semester)
    }

    override suspend fun deleteSemesterById(semesterId: Long) {
        semesterDao.deleteSemesterById(semesterId)
    }

    override suspend fun deleteAllSemesters() {
        semesterDao.deleteAllSemesters()
    }

    override suspend fun getSemesterById(semesterId: Long): Semester {
        return semesterDao.getSemesterById(semesterId)
    }

    override suspend fun getAllSemesters(): List<Semester> {
        return semesterDao.getAllSemesters()
    }

    override suspend fun getSemesterByTaskId(taskId: Long): Semester {
        return semesterDao.getSemesterByTaskId(taskId)
    }

    override fun getAllSemestersLiveData(): LiveData<List<Semester>> {
        return semesterDao.getAllSemestersLiveData()
    }

    override fun getAllSemestersWithTotalCount(): LiveData<List<SemesterResponse>> {
        return semesterDao.getAllSemestersWithTotalCount()
    }

    override fun getSemester(date: Long): LiveData<Semester> {
        return semesterDao.getSemester(date)
    }

    override fun getSemesterForWidgetService(date: Long): Semester {
        return semesterDao.getSemesterForWidgetService(date)
    }

    override fun getSemesterByFlow(date: Long): Flow<Semester> {
        return semesterDao.getSemesterByFlow(date)
    }

}