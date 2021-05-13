package com.reachfree.timetable.extension

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle


fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
    atZone(zone)?.toInstant()?.toEpochMilli()

fun LocalDate.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
    atStartOfDay(zone).toInstant().toEpochMilli()

fun LocalTime.toLocalString(): String {
    return localTimeFormat.format(this)
}

private val localTimeFormat: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)