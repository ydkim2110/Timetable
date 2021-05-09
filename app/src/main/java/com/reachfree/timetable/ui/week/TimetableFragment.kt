package com.reachfree.timetable.ui.week

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.FragmentWeekBinding
import com.reachfree.timetable.extension.toMillis
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.timetable.TimetableData
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.util.timetable.TimetableEvent
import com.reachfree.timetable.util.timetable.TimetableEventConfig
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class TimetableFragment : BaseFragment<FragmentWeekBinding>() {

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

//        setupData()
//        timetableViewModel.deleteSemesters()
//        timetableViewModel.deleteSubjects()
//        createSemester()
        setupViewHandler()
        subscribeToObserver()

    }

    private fun setupViewHandler() {
        binding.weekView.setLessonClickListener {
            Timber.d("DEBUG: clicked item is ${it.event.title}")
        }

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

    private fun createSemester() {
        val startDate = DateUtils.calculateStartOfDay(LocalDate.of(2020, 9, 1)).toMillis()
        val endDate = DateUtils.calculateEndOfDay(LocalDate.of(2020, 12, 18)).toMillis()
        val startDate2 = DateUtils.calculateStartOfDay(LocalDate.of(2021, 3, 1)).toMillis()
        val endDate2 = DateUtils.calculateEndOfDay(LocalDate.of(2021, 6, 25)).toMillis()
        val startDate3 = DateUtils.calculateStartOfDay(LocalDate.of(2021, 9, 1)).toMillis()
        val endDate3 = DateUtils.calculateEndOfDay(LocalDate.of(2021, 12, 31)).toMillis()

        val semester = Semester(
                id = null,
                title = "2020학년 2학기",
                description = "1학년 2학기",
                startDate = startDate!!,
                endDate = endDate!!
        )
        val semester2 = Semester(
                id = null,
                title = "2021학년 1학기",
                description = "2학년 1학기",
                startDate = startDate2!!,
                endDate = endDate2!!
        )
        val semester3 = Semester(
                id = null,
                title = "2021학년 2학기",
                description = "2학년 2학기",
                startDate = startDate3!!,
                endDate = endDate3!!
        )
        timetableViewModel.insertSemester(semester)
        timetableViewModel.insertSemester(semester2)
        timetableViewModel.insertSemester(semester3)
    }

    private fun createSubject(semesterId: Long) {

        val subjectDays = arrayListOf(
                Subject.Days(1, 9, 0, 10, 30),
                Subject.Days(3, 10, 20, 11, 40),
        )

        val subjectDays2 = arrayListOf(
                Subject.Days(0, 14, 0, 15, 20),
                Subject.Days(2, 13, 0, 14, 30),
        )

        val subjectDays3 = arrayListOf(
                Subject.Days(0, 10, 0, 12, 20),
                Subject.Days(2, 8, 0, 19, 30),
        )

        val subject = Subject(
                id = null,
                title = "미시경제학",
                days = subjectDays,
                classroom = "303호",
                buildingName = "연희관",
                description = "이번학기에는 A를 맞아야 한다!!",
                professorName = "홍길동",
                book_name = "(만점)미시경제학",
                credit = 3,
                type = 0,
                semesterId = 2
        )

        val subject2 = Subject(
                id = null,
                title = "경제학원론",
                days = subjectDays2,
                classroom = "202호",
                buildingName = "대우관",
                description = "원론부터 다시 시작!!",
                professorName = "세종대왕",
                book_name = "맨큐의 경제학",
                credit = 2,
                type = 1,
                semesterId = 2
        )

        val subject3 = Subject(
                id = null,
                title = "거시경제학",
                days = subjectDays3,
                classroom = "102호",
                buildingName = "대우관",
                description = "거시적으로!!",
                professorName = "이순신",
                book_name = "맨큐의 경제학",
                credit = 3,
                type = 0,
                semesterId = 3
        )

        timetableViewModel.insertSubject(subject)
        timetableViewModel.insertSubject(subject2)
        timetableViewModel.insertSubject(subject3)
    }

    private fun subscribeToObserver() {
        timetableViewModel.thisSemester.observe(viewLifecycleOwner) { semester ->
            semester?.id?.let {
                Timber.d("DEBUG: semesterId ${semester.id}")
//                createSubject(it)
                timetableViewModel.getAllSubjectBySemester(it).observe(viewLifecycleOwner) { subjects ->
                    if (!subjects.isNullOrEmpty()) {
                        removeAllEvents()
                        showThisSemesterSubjects(subjects)
//                        for (i in 0 until subjects.size) {
//                            for (j in 0 until subjects[i].days.size) {
//                                Timber.d("DEBUG: startHour ${subjects[i].days[j].startHour}")
//                                Timber.d("DEBUG: startMin ${subjects[i].days[j].startMinute}")
//                            }
//                        }
                    }
                }
            }
        }
    }

    private fun removeAllEvents() {
        binding.weekView.removeViews(1, binding.weekView.childCount - 1)
    }

    private fun showThisSemesterSubjects(subjects: List<Subject>) {
        val config = TimetableEventConfig()
        binding.weekView.timetableConfig = config

        val timetableData: TimetableData = TimetableData().apply {
            for ((index, value) in subjects.withIndex()) {
                for (i in value.days.indices) {
                    val event = TimetableEvent.Single(
                            id = 1,
                            date = DateUtils.calculateDay(value.days[i].day),
                            title = value.title,
                            shortTitle = value.title,
                            location = value.classroom,
                            startTime = LocalTime.of(value.days[i].startHour, value.days[i].startMinute),
                            endTime = LocalTime.of(value.days[i].endHour, value.days[i].endMinute),
                            backgroundColor = randomColor(),
                            textColor = Color.WHITE
                    )
                    this.add(event)
                }
            }
        }

        binding.weekView.addEvents(timetableData)
    }

    private val random = Random()
    private fun randomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupData() {
        val config = TimetableEventConfig(
//                showSubtitle = false,
//                showTimeEnd = false
        )
        binding.weekView.eventConfig = config

        val nowEvent = TimetableEvent.Single(
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

        val tomorrowEvent = TimetableEvent.Single(
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
        fun newInstance() = TimetableFragment()
    }
}