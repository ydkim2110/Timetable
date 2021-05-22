package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.timetable.data.model.PartTimeJob

@Dao
interface PartTimeJobDao {

    @Insert
    suspend fun insertPartTimeJob(partTimeJob: PartTimeJob)

    @Update
    suspend fun updatePartTimeJob(partTimeJob: PartTimeJob)

    @Delete
    fun deletePartTimeJob(partTimeJob: PartTimeJob)

    @Query("SELECT * FROM part_time_jobs WHERE end_date >= :currentDate AND start_date <= :currentDate")
    suspend fun getAllPartTimeJobs(currentDate: Long): List<PartTimeJob>

    @Query("SELECT * FROM part_time_jobs WHERE id LIKE :partTimeJobId LIMIT 1")
    suspend fun getPartTimeJobById(partTimeJobId: Long): PartTimeJob

    @Query("SELECT * FROM part_time_jobs")
    fun getAllPartTimeJobs(): LiveData<List<PartTimeJob>>
}