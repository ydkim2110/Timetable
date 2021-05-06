package com.reachfree.timetable.weekview.data

import android.os.Build
import androidx.annotation.RequiresApi
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

sealed class Event {

    abstract val id: Long
    abstract val date: LocalDate
    abstract val title: String
    abstract val shortTitle: String

    data class Single(
        override val id: Long,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val location: String? = null,
        val subTitle: String? = null,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val upperText: String? = null,
        val lowerText: String? = null,
        val textColor: Int,
        val backgroundColor: Int
    ) : Event() {
        @RequiresApi(Build.VERSION_CODES.O)
        val duration: Duration = Duration.between(startTime, endTime)
    }

    data class AllDay(
        override val id: Long,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String
    ) : Event()

    data class MultiDay(
        override val id: Long,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val lastDate: LocalDate
    ) : Event()
}