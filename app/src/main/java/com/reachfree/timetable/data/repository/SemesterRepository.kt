package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.SemesterResponse
import kotlinx.coroutines.flow.Flow

interface SemesterRepository {

    suspend fun insertSemester(semester: Semester)

    suspend fun updateSemester(semester: Semester)

    suspend fun deleteSemesterById(semesterId: Long)

    suspend fun deleteAllSemesters()

    suspend fun getSemesterById(semesterId: Long): Semester

    suspend fun getAllSemesters(): List<Semester>

    suspend fun getSemesterByTaskId(taskId: Long): Semester

    fun getAllSemestersLiveData(): LiveData<List<Semester>>

    fun getAllSemestersWithTotalCount(): LiveData<List<SemesterResponse>>

    fun getSemester(date: Long): LiveData<Semester>

    fun getSemesterForWidgetService(date: Long): Semester

    fun getSemesterByFlow(date: Long): Flow<Semester>

    fun getSemesterByIdLiveData(semesterId: Long): LiveData<Semester>

}