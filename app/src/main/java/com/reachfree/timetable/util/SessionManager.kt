package com.reachfree.timetable.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.reachfree.timetable.util.PreferenceHelper.set
import com.reachfree.timetable.util.PreferenceHelper.get

class SessionManager(
    context: Context
) {

    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)

    fun getResisterSemester(): Boolean {
        return prefs[REGISTER_SEMESTER, false]
    }

    fun setRegisterSemester() {
        prefs.edit { putBoolean(REGISTER_SEMESTER, true) }
    }

}