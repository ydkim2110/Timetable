package com.reachfree.timetable.data.response

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class SemesterResponse(
    var id: Long?,
    var title: String = "",
    var description: String = "",
    @ColumnInfo(name = "start_date") var startDate: Long = 0L,
    @ColumnInfo(name = "end_date") var endDate: Long = 0L,
    @ColumnInfo(name = "total_count")var totalCredit: Int = 0
) : Parcelable