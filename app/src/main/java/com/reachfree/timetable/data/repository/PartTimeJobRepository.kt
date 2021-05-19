package com.reachfree.timetable.data.repository

import com.reachfree.timetable.data.model.PartTimeJob

interface PartTimeJobRepository {

    suspend fun insertPartTimeJob(partTimeJob: PartTimeJob)

    suspend fun getAllPartTimeJobs(currentDate: Long): List<PartTimeJob>

}