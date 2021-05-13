package com.reachfree.timetable.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue

internal fun Context.dipToPixelF(dip: Float): Float {
    val metrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics)
}

internal fun Context.dipToPixelI(dip: Float): Int {
    return dipToPixelF(dip).toInt()
}

fun runDelayed(millis: Long, function: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(function, millis)
}