package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.SemesterResponse
import kotlinx.coroutines.flow.Flow

interface SemesterRepository {

    suspend fun insertSemester(semester: Semester)

    suspend fun deleteAllSemesters()

    fun getAllSemesters(): LiveData<List<Semester>>

    fun getAllSemestersWithTotalCount(): LiveData<List<SemesterResponse>>

    fun getSemester(date: Long): LiveData<Semester>

    fun getSemesterForWidgetService(date: Long): Semester

    fun getSemesterByFlow(date: Long): Flow<Semester>


}