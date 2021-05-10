package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.dao.TestDao
import com.reachfree.timetable.data.model.Test
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val testDao: TestDao
) : TestRepository {
    override suspend fun insertTest(test: Test) {
        testDao.insertTest(test)
    }

    override fun getAllTests(): LiveData<List<Test>> {
        return testDao.getAllTests()
    }
}