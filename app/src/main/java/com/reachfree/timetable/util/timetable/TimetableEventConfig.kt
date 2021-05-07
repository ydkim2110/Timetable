package com.reachfree.timetable.util.timetable

data class TimetableEventConfig(
    val useShortNames: Boolean = true,
    val showUpperText: Boolean = true,
    val showSubtitle: Boolean = true,
    val showLowerText: Boolean = true,
    val showTimeStart: Boolean = true,
    val showTimeEnd: Boolean = true
)
