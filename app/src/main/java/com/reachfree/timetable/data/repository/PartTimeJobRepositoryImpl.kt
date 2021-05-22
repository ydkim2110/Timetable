package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.PartTimeJobDao
import com.reachfree.timetable.data.model.PartTimeJob
import javax.inject.Inject

class PartTimeJobRepositoryImpl @Inject constructor(
    private val partTimeJobDao: PartTimeJobDao
) : PartTimeJobRepository {

    override suspend fun insertPartTimeJob(partTimeJob: PartTimeJob) =
        partTimeJobDao.insertPartTimeJob(partTimeJob)

    override suspend fun updatePartTimeJob(partTimeJob: PartTimeJob) =
        partTimeJobDao.updatePartTimeJob(partTimeJob)

    override suspend fun deletePartTimeJob(partTimeJob: PartTimeJob) {
        partTimeJobDao.deletePartTimeJob(partTimeJob)
    }

    override suspend fun getAllPartTimeJobs(currentDate: Long) =
        partTimeJobDao.getAllPartTimeJobs(currentDate)

    override suspend fun getPartTimeJobById(partTimeJobId: Long): PartTimeJob =
        partTimeJobDao.getPartTimeJobById(partTimeJobId)


    override fun getAllPartTimeJobs(): LiveData<List<PartTimeJob>> =
        partTimeJobDao.getAllPartTimeJobs()
}