package com.reachfree.timetable.util

import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.util.*

internal object DayOfWeekUtil {

    fun createList(
        firstDay: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek.plus(1)
    ): List<DayOfWeek> {
        return (0..6L).toList().map { firstDay.plus(it) }
    }

    fun mapDayToColumn(
        day: DayOfWeek,
        saturdayEnabled: Boolean,
        sundayEnabled: Boolean
    ): Int {
        val firstDayOfTheWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek.plus(1)

        if (day == DayOfWeek.SATURDAY && !saturdayEnabled) {
            throw IllegalStateException("Passed saturday although it is disabled")
        }

        if (day == DayOfWeek.SUNDAY && !sundayEnabled) {
            throw IllegalStateException("Passed sunday although it is disabled")
        }

        when (firstDayOfTheWeek) {
            DayOfWeek.MONDAY -> {
                val column = day.value
                return if (!saturdayEnabled && day == DayOfWeek.SUNDAY) {
                    5
                } else {
                    column - 1
                }
            }

            DayOfWeek.SATURDAY -> {
                if (saturdayEnabled) {
                    return if (sundayEnabled) {
                        when (day) {
                            DayOfWeek.SATURDAY -> 0
                            DayOfWeek.SUNDAY -> 1
                            else -> day.value + 1
                        }
                    } else {
                        return when (day) {
                            DayOfWeek.SATURDAY -> 0
                            else -> day.value
                        }
                    }
                } else {
                    return if (sundayEnabled) {
                        when (day) {
                            DayOfWeek.SUNDAY -> 0
                            else -> day.value
                        }
                    } else {
                        return day.value - 1
                    }
                }
            }

            DayOfWeek.SUNDAY -> {
                return if (sundayEnabled) {
                    if (day == DayOfWeek.SUNDAY) {
                        0
                    } else {
                        day.value
                    }
                } else {
                    day.value - 1
                }
            }

            else -> throw IllegalStateException("$firstDayOfTheWeek das is not supported as start day")
        }
    }

}