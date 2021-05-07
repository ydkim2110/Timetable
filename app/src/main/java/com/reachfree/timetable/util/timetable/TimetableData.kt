package com.reachfree.timetable.util.timetable

import org.threeten.bp.LocalTime

class TimetableData {

    private val singleEvents: MutableList<TimetableEvent.Single> = mutableListOf()

    private val allDays: MutableList<TimetableEvent.AllDay> = mutableListOf()

    var earliestStart: LocalTime = LocalTime.MAX
    var latestEnd: LocalTime = LocalTime.MIN

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