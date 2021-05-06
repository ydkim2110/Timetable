package com.reachfree.timetable.weekview

import android.content.Context
import android.util.TypedValue
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

internal fun Context.dipToPixelF(dip: Float): Float {
    val metrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics)
}

internal fun Context.dipToPixelI(dip: Float): Int {
    return dipToPixelF(dip).toInt()
}

internal fun LocalTime.toLocalString(): String {
    return localTimeFormat.format(this)
}

private val localTimeFormat: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)