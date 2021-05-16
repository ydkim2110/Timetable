package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.response.CalendarTaskResponse

interface TaskRepository {

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun getTaskById(taskId: Long): Task

    fun getTaskByIdLiveData(taskId: Long): LiveData<Task>

    fun getAllTasksBySubject(subjectId: Long): LiveData<List<Task>>

    fun getAllTaskBySubject(subjectId: Long): LiveData<List<CalendarTaskResponse>>

    fun getAllTaskBySubjectForWidgetService(subjectId: Long, startDate: Long): List<CalendarTaskResponse>

    fun getAllTaskBySubject(subjectIds: LongArray): LiveData<List<CalendarTaskResponse>>

    fun getAllTaskBySubject(
        startDate: Long,
        endDate: Long,
        subjectId: LongArray
    ): LiveData<List<CalendarTaskResponse>>

    fun getAllTasks(): LiveData<List<Task>>
}