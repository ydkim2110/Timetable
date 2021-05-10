package com.reachfree.timetable.data.response

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.reachfree.timetable.util.ColorTag

data class CalendarTaskResponse(
    var id: Long? = null,
    var title: String = "",
    var description: String = "",
    var date: Long? = 0L,
    @ColumnInfo(name = "subject_id") var subjectId: Long,
    @ColumnInfo(name = "background_color") var backgroundColor: Int = ColorTag.COLOR_1.resId
)