package com.reachfree.timetable.data.repository

import com.reachfree.timetable.data.dao.PartTimeJobDao
import com.reachfree.timetable.data.model.PartTimeJob
import javax.inject.Inject

class PartTimeJobRepositoryImpl @Inject constructor(
    private val partTimeJobDao: PartTimeJobDao
) : PartTimeJobRepository {

    override suspend fun insertPartTimeJob(partTimeJob: PartTimeJob) =
        partTimeJobDao.insertPartTimeJob(partTimeJob)

    override suspend fun getAllPartTimeJobs(currentDate: Long) =
        partTimeJobDao.getAllPartTimeJobs(currentDate)


}