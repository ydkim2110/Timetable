package com.reachfree.timetable.ui.home

import com.reachfree.timetable.data.model.Semester

interface SemesterChangedListener {
    fun onSemesterChanged(semester: Semester)
    fun onSemesterDeleted()
}