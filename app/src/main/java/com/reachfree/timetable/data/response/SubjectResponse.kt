package com.reachfree.timetable.data.response

import androidx.room.ColumnInfo
import com.reachfree.timetable.data.model.SubjectType

data class SubjectTypeResponse(
    var type: Int = SubjectType.MANDATORY.ordinal,
    @ColumnInfo(name = "total_credit") var totalCredit: Int = 0
)