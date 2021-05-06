package com.reachfree.timetable.ui.week

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.databinding.FragmentWeekBinding
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.weekview.data.Event
import com.reachfree.timetable.weekview.data.EventConfig
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat

@AndroidEntryPoint
class WeekFragment : BaseFragment<FragmentWeekBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeekBinding {
        return FragmentWeekBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2021)
        cal.set(Calendar.MONTH, 3)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        Timber.d("DEBUG: start date: ${df.format(cal.time)}")

        val cal2 = Calendar.getInstance()
        cal2.set(Calendar.YEAR, 2021)
        cal2.set(Calendar.MONTH, 6)
        cal2.set(Calendar.DAY_OF_MONTH, 30)
        Timber.d("DEBUG: end date: ${df.format(cal2.time)}")

        val semester = Semester(
            id = null,
            title = "2021학년 1학기",
            description = "대학교 1학년",
            startDate = cal.timeInMillis,
            endDate = cal2.timeInMillis
        )

        timetableViewModel.insertSemester(semester)

        setupData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupData() {
        val config = EventConfig(
//                showSubtitle = false,
//                showTimeEnd = false
        )
        binding.weekView.eventConfig = config

        val nowEvent = Event.Single(
                id = 1,
                date = LocalDate.now().minusDays(12),
                title = "거시경제학",
                shortTitle = "미시경제학",
                location = "연희관303호",
                startTime = LocalTime.of(9, 30, 0),
                endTime = LocalTime.of(11, 10, 0),
                backgroundColor = Color.RED,
                textColor = Color.WHITE
        )

        val tomorrowEvent = Event.Single(
            id = 1,
            date = LocalDate.now(),
            title = "수",
            shortTitle = "수",
            location = "대우관201호",
            startTime = LocalTime.of(9, 30, 0),
            endTime = LocalTime.of(11, 10, 0),
            backgroundColor = Color.BLUE,
            textColor = Color.WHITE
        )

        binding.weekView.addEvent(nowEvent)
        binding.weekView.addEvent(tomorrowEvent)

        binding.weekView.setOnTouchListener { v, event ->
            when (event.pointerCount) {
                1 -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
                2 -> {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            false
        }
    }

    companion object {
        fun newInstance() = WeekFragment()
    }
}