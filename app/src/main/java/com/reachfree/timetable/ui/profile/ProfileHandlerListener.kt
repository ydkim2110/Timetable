package com.reachfree.timetable.ui.profile

import com.reachfree.timetable.data.response.SemesterTotalCreditResponse

interface ProfileHandlerListener {
    fun onDetailSubjectClicked(semesterTotalCreditResponse: SemesterTotalCreditResponse)
}