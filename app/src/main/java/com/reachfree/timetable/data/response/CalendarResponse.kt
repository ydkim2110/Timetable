package com.reachfree.timetable.data.response

import androidx.room.ColumnInfo
import com.reachfree.timetable.data.model.TaskType
import com.reachfree.timetable.util.ColorTag
import java.util.*

data class CalendarResponse(
    var dateList: List<Date>,
    var taskList: List<CalendarTaskResponse>? = null
)

data class CalendarTaskResponse(
    var id: Long? = null,
    var title: String = "",
    var description: String = "",
    var date: Long? = 0L,
    var type: Int = TaskType.TASK.ordinal,
    @ColumnInfo(name = "subject_id") var subjectId: Long,
    @ColumnInfo(name = "background_color") var backgroundColor: Int = ColorTag.COLOR_1.resId
)