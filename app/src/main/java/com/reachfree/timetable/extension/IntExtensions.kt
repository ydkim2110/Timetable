package com.reachfree.timetable.extension

import android.graphics.Color
import com.reachfree.timetable.util.DARK_GREY
import java.util.*

fun Int.getContrastColor(): Int {
    val y = (299 * Color.red(this) + 587 * Color.green(this) + 114 * Color.blue(this)) / 1000
    return if (y >= 149 && this != Color.BLACK) DARK_GREY else Color.WHITE
}

fun Int.toHex() = String.format("#%06X", 0xFFFFFF and this).toUpperCase(Locale.getDefault())