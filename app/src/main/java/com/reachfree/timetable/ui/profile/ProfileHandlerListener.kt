package com.reachfree.timetable.ui.profile

import com.reachfree.timetable.data.response.SemesterResponse

interface ProfileHandlerListener {
    fun onDetailSubjectClicked(semesterTotalCreditResponse: SemesterResponse)
}