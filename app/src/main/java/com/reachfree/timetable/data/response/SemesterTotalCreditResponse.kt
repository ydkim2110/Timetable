package com.reachfree.timetable.data.response

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.reachfree.timetable.data.model.Semester

data class SemesterTotalCreditResponse(
    var id: Long?,
    var title: String = "",
    var description: String = "",
    @ColumnInfo(name = "start_date") var startDate: Long = 0L,
    @ColumnInfo(name = "end_date") var endDate: Long = 0L,
    @ColumnInfo(name = "total_count")var totalCredit: Int = 0
)