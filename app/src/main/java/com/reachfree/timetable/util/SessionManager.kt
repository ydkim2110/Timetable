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

    fun getPrefs(): SharedPreferences = prefs

    fun getResisterSemester(): Boolean {
        return prefs[REGISTER_SEMESTER, false]
    }

    fun setRegisterSemester(value: Boolean) {
        prefs.edit { putBoolean(REGISTER_SEMESTER, value) }
    }

    fun getStartTime(): Int {
        return prefs[START_TIME, StartTime.AM_EIGHT.hour]
    }

    fun setStartTime(value: Int) {
        prefs.edit { putInt(START_TIME, value) }
    }

    fun getEndTime(): Int {
        return prefs[END_TIME, EndTime.PM_SIX.hour]
    }

    fun setEndTime(value: Int) {
        prefs.edit { putInt(END_TIME, value) }
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