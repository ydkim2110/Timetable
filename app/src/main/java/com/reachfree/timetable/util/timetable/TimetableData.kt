package com.reachfree.timetable.util.timetable

import com.reachfree.timetable.util.SessionManager
import org.threeten.bp.LocalTime
import timber.log.Timber

class TimetableData(
    private val sessionManager: SessionManager
) {

    private val singleEvents: MutableList<TimetableEvent.Single> = mutableListOf()

    private val allDays: MutableList<TimetableEvent.AllDay> = mutableListOf()

    var earliestStart: LocalTime = LocalTime.of(this.sessionManager.getStartTime(), 0)
    var latestEnd: LocalTime = LocalTime.of(this.sessionManager.getEndTime(), 0)

    init {
        Timber.d("DEBUG: getStartTime ${sessionManager.getStartTime()}")
        Timber.d("DEBUG: getEndTime ${sessionManager.getEndTime()}")
    }

    fun add(item: TimetableEvent.AllDay) {
        allDays.add(item)
    }

    fun add(item: TimetableEvent.Single) {
        singleEvents.add(item)

        if (item.startTime.isBefore(earliestStart)) {
            earliestStart = item.startTime
        }

        if (item.endTime.isAfter(latestEnd)) {
            latestEnd = item.endTime
        }
    }

    fun getSingleEvents(): List<TimetableEvent.Single> = singleEvents.toList()

    fun getAllDayEvents(): List<TimetableEvent.AllDay> = allDays.toList()

    fun isEmpty() = singleEvents.isEmpty() && allDays.isEmpty()

    fun clear() {
        singleEvents.clear()
        allDays.clear()
    }

}