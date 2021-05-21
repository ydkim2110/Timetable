package com.reachfree.timetable.data.response

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
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
    @ColumnInfo(name = "start_date") var startDate: Long? = 0,
    @ColumnInfo(name = "end_date") var endDate: Long? = 0,
    var classroom: String = "",
    @ColumnInfo(name = "building_name") var buildingName: String = "",
    @ColumnInfo(name = "background_color") var backgroundColor: Int = android.R.color.darker_gray,
)

data class GradeListResponse(
    var id: Long?,
    var title: String = "",
    var days: List<Subject.Days>,
    var classroom: String = "",
    var description: String = "",
    @ColumnInfo(name = "building_name") var buildingName: String = "",
    @ColumnInfo(name = "professor_name") var professorName: String = "",
    @ColumnInfo(name = "book_name") var book_name: String = "",
    var credit: Int = 3,
    var grade: String = "",
    var type: Int = SubjectType.MANDATORY.ordinal,
    @ColumnInfo(name = "background_color") var backgroundColor: Int = ColorTag.COLOR_1.resId,
    @ColumnInfo(name = "semester_id") var semesterId: Long,
    @ColumnInfo(name = "semester_title") var semesterTitle: String = "",
    @ColumnInfo(name = "semester_start_date") var semesterStartDate: Long = 0L,
    @ColumnInfo(name = "semester_end_date") var semesterEndDate: Long = 0L
)