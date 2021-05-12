package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.TaskDao
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.response.CalendarTaskResponse
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    override suspend fun getTaskById(taskId: Long): Task {
        return taskDao.getTaskById(taskId)
    }

    override fun getTaskByIdLiveData(taskId: Long): LiveData<Task> {
        return taskDao.getTaskByIdLiveData(taskId)
    }

    override fun getAllTasksBySubject(subjectId: Long): LiveData<List<Task>> {
        return taskDao.getAllTasksBySubject(subjectId)
    }

    override fun getAllTaskBySubject(subjectId: Long): LiveData<List<CalendarTaskResponse>> {
        return taskDao.getAllTaskBySubject(subjectId)
    }

    override fun getAllTaskBySubject(subjectIds: LongArray): LiveData<List<CalendarTaskResponse>> {
        return taskDao.getAllTaskBySubject(subjectIds)
    }

    override fun getAllTaskBySubject(
        startDate: Long,
        endDate: Long,
        subjectId: LongArray
    ): LiveData<List<CalendarTaskResponse>> {
        return taskDao.getAllTaskBySubject(startDate, endDate, subjectId)
    }

    override fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks()
    }
}