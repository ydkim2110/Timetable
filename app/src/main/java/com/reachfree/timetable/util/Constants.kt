package com.reachfree.timetable.util

const val LOCAL_DATABASE_NAME = "reachfree_timetable_db"
const val LIST_CLICK_BROADCAST = "list_click_broadcast"
val DARK_GREY = 0xFF333333.toInt()

// SharedPreferences
const val START_TIME = "start_time"
const val END_TIME = "end_time"
const val REGISTER_SEMESTER = "register_semester"
const val MANDATORY_CREDIT = "mandatory_credit"
const val ELECTIVE_CREDIT = "elective_credit"
const val GRADUATION_CREDIT = "graduation_credit"


enum class StartTime(
    val hour: Int
) {
    AM_SIX(6),
    AM_SEVEN(7),
    AM_EIGHT(8),
    AM_NINE(9),
    AM_TEN(10)
}

enum class EndTime(
    val hour: Int
) {
    PM_FIVE(17),
    PM_SIX(18),
    PM_SEVEN(19),
    PM_EIGHT(20),
    PM_NINE(21),
    PM_TEN(22),
    PM_ELEVEN(23)
}