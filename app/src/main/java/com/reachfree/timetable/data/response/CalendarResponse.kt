package com.reachfree.timetable.data.response

import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.model.Test
import java.util.*

data class CalendarResponse(
    var dateList: List<Date>,
    var testList: List<Test>? = null,
    var taskList: List<CalendarTaskResponse>? = null
)