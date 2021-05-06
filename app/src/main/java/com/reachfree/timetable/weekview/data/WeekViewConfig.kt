package com.reachfree.timetable.weekview.data

import android.content.SharedPreferences

class WeekViewConfig(
    val prefs: SharedPreferences
) {

    private val SCALING_FACTOR = "scaling_factor"

    var scalingFactor: Float
        get() = prefs.getFloat(SCALING_FACTOR, 1f)
        set(value) = prefs.edit().putFloat(SCALING_FACTOR, value).apply()

}