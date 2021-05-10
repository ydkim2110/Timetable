package com.reachfree.timetable.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.timetable.data.model.Test

interface TestRepository {

    suspend fun insertTest(test: Test)

    fun getAllTests(): LiveData<List<Test>>

}