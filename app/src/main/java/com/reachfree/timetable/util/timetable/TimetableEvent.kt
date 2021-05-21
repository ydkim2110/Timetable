package com.reachfree.timetable.util.timetable

import android.os.Build
import androidx.annotation.RequiresApi
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

sealed class TimetableEvent {

    abstract val id: Long
    abstract val category: Int
    abstract val date: LocalDate
    abstract val title: String
    abstract val shortTitle: String

    data class Single(
        override val id: Long,
        override val category: Int,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val credit: Int? = 3,
        val classroom: String? = null,
        val building: String? = null,
        val subTitle: String? = null,
        val startDate: Long? = 0,
        val endDate: Long? = 0,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val upperText: String? = null,
        val lowerText: String? = null,
        val textColor: Int,
        val backgroundColor: Int
    ) : TimetableEvent() {
        @RequiresApi(Build.VERSION_CODES.O)
        val duration: Duration = Duration.between(startTime, endTime)
    }

    data class AllDay(
        override val id: Long,
        override val category: Int,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val credit: Int? = 3,
    ) : TimetableEvent()

    data class MultiDay(
        override val id: Long,
        override val category: Int,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val credit: Int? = 3,
        val lastDate: LocalDate
    ) : TimetableEvent()
}