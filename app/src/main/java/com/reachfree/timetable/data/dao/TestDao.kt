package com.reachfree.timetable.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.reachfree.timetable.data.model.Test

@Dao
interface TestDao {

    @Insert
    suspend fun insertTest(test: Test)

    @Query("SELECT * FROM tests")
    fun getAllTests(): LiveData<List<Test>>

}