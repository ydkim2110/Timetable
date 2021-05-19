package com.reachfree.timetable.data.response

import androidx.room.ColumnInfo
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.SubjectType
import com.reachfree.timetable.util.ColorTag

data class SubjectTypeResponse(
    var type: Int = SubjectType.MANDATORY.ordinal,
    @ColumnInfo(name = "total_credit") var totalCredit: Int = 0
)

data class TimetableResponse(
    var id: Long?,
    var category: Int = 0,
    var title: String = "",
    var description: String = "",
    var days: List<Subject.Days>,
    var classroom: String = "",
    @ColumnInfo(name = "building_name") var buildingName: String = "",
    @ColumnInfo(name = "background_color") var backgroundColor: Int = ColorTag.COLOR_1.resId,
)