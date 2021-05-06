package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Semester

interface TimetableRepository {

    suspend fun insertSemester(semester: Semester)

    fun getAllSemesters(): LiveData<List<Semester>>

}