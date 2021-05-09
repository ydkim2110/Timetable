package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.SemesterTotalCreditResponse

interface SemesterRepository {

    suspend fun insertSemester(semester: Semester)

    suspend fun deleteAllSemesters()

    fun getAllSemesters(): LiveData<List<Semester>>

    fun getAllSemestersWithTotalCount(): LiveData<List<SemesterTotalCreditResponse>>

    fun getSemester(date: Long): LiveData<Semester>

}