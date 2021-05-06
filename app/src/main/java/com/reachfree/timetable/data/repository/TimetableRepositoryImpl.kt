package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.TimetableDao
import com.reachfree.timetable.data.model.Semester
import javax.inject.Inject

class TimetableRepositoryImpl @Inject constructor(
    private val timetableDao: TimetableDao
) : TimetableRepository {

    override suspend fun insertSemester(semester: Semester) {
        timetableDao.insertSemester(semester)
    }

    override fun getAllSemesters(): LiveData<List<Semester>> {
        return timetableDao.getAllSemesters()
    }

}