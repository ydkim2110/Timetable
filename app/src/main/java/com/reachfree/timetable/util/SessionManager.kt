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

    fun setRegisterSemester(value: Boolean) {
        prefs.edit { putBoolean(REGISTER_SEMESTER, value) }
    }

    fun getMandatoryTotalCredit(): Int {
        return prefs[MANDATORY_CREDIT, 60]
    }

    fun setMandatoryTotalCredit(value: Int) {
        prefs.edit { putInt(MANDATORY_CREDIT, value) }
    }

    fun getElectiveTotalCredit(): Int {
        return prefs[ELECTIVE_CREDIT, 40]
    }

    fun setElectiveTotalCredit(value: Int) {
        prefs.edit { putInt(ELECTIVE_CREDIT, value) }
    }

    fun getGraduationTotalCredit(): Int {
        return prefs[GRADUATION_CREDIT, 135]
    }

    fun setGraduationTotalCredit(value: Int) {
        prefs.edit { putInt(GRADUATION_CREDIT, value) }
    }

}