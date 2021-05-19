package com.reachfree.timetable.ui.home

import com.reachfree.timetable.data.model.Semester

interface SemesterChangedListener {
    fun onSemesterSelected(semester: Semester)
    fun onSemesterChanged()
    fun onSemesterDeleted()
}