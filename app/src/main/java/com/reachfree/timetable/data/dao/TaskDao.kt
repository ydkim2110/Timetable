package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.response.CalendarTaskResponse

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id LIKE :taskId")
    suspend fun getTaskById(taskId: Long): Task

    @Query("SELECT * FROM tasks WHERE id LIKE :taskId")
    fun getTaskByIdLiveData(taskId: Long): LiveData<Task>

    @Query("SELECT * FROM tasks WHERE subject_id LIKE :subjectId")
    fun getAllTasksBySubject(subjectId: Long): LiveData<List<Task>>

    @Query("""
        SELECT
            t.id,
            s.title AS semester_title,
            t.title,
            t.description,
            t.date,
            t.type,
            t.subject_id,
            s.background_color
        FROM tasks t
        INNER JOIN subjects s ON t.subject_id = s.id
        WHERE subject_id LIKE :subjectId
    """)
    fun getAllTaskBySubject(subjectId: Long): LiveData<List<CalendarTaskResponse>>

    @Query("""
        SELECT
            t.id,
            s.title AS semester_title,
            t.title,
            t.description,
            t.date,
            t.type,
            t.subject_id,
            s.background_color
        FROM tasks t
        INNER JOIN subjects s ON t.subject_id = s.id
        WHERE t.subject_id LIKE :subjectId
            AND t.date >= :startDate
        ORDER BY t.date ASC
    """)
    fun getAllTaskBySubjectForWidgetService(subjectId: Long, startDate: Long): List<CalendarTaskResponse>

    @Query("""
        SELECT
            t.id,
            s.title AS semester_title,
            t.title,
            t.description,
            t.date,
            t.type,
            t.subject_id,
            s.background_color
        FROM tasks t
        INNER JOIN subjects s ON t.subject_id = s.id
        WHERE subject_id IN (:subjectIds)
        ORDER BY t.date
    """)
    fun getAllTaskBySubject(subjectIds: LongArray): LiveData<List<CalendarTaskResponse>>

    @Query("""
        SELECT
            t.id,
            s.title AS semester_title,
            t.title,
            t.description,
            t.date,
            t.type,
            t.subject_id,
            s.background_color
        FROM tasks t
        INNER JOIN subjects s ON t.subject_id = s.id
        WHERE subject_id IN (:subjectId)
        AND t.date BETWEEN :startDate AND :endDate
        ORDER BY t.date
    """)
    fun getAllTaskBySubject(
        startDate: Long,
        endDate: Long,
        subjectId: LongArray
    ): LiveData<List<CalendarTaskResponse>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>
}