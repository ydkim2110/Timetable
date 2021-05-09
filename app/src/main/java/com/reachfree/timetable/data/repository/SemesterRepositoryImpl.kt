package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.SemesterDao
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.SemesterTotalCreditResponse
import javax.inject.Inject

class SemesterRepositoryImpl @Inject constructor(
    private val semesterDao: SemesterDao
) : SemesterRepository {

    override suspend fun insertSemester(semester: Semester) {
        semesterDao.insertSemester(semester)
    }

    override suspend fun deleteAllSemesters() {
        semesterDao.deleteAllSemesters()
    }

    override fun getAllSemesters(): LiveData<List<Semester>> {
        return semesterDao.getAllSemesters()
    }

    override fun getAllSemestersWithTotalCount(): LiveData<List<SemesterTotalCreditResponse>> {
        return semesterDao.getAllSemestersWithTotalCount()
    }

    override fun getSemester(date: Long): LiveData<Semester> {
        return semesterDao.getSemester(date)
    }

}