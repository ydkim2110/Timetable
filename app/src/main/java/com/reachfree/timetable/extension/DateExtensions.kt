package com.reachfree.timetable.extension

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
        atZone(zone)?.toInstant()?.toEpochMilli()

fun LocalDate.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
        atStartOfDay(zone).toInstant().toEpochMilli()