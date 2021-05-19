package com.reachfree.timetable.ui.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.TimetableResponse
import com.reachfree.timetable.databinding.FragmentWeekBinding
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.ui.home.SemesterChangedListener
import com.reachfree.timetable.util.*
import com.reachfree.timetable.util.timetable.TimetableData
import com.reachfree.timetable.util.timetable.TimetableEvent
import com.reachfree.timetable.util.timetable.TimetableEventConfig
import com.reachfree.timetable.util.timetable.TimetableEventView
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TimetableFragment : BaseFragment<FragmentWeekBinding>(), SemesterChangedListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var timetableDetailDialog: TimetableDetailDialog

    private var semesterId: Long? = null

    interface TimetableFragmentListener {
        fun onEditButtonClicked(timetableEventView: TimetableEventView)
        fun onSemesterTitle(title: String)
    }

    private lateinit var timetableFragmentListener: TimetableFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is HomeActivity) {
            timetableFragmentListener = activity as HomeActivity
        }

        (activity as HomeActivity).setOnSemesterChangedListener(this)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeekBinding {
        return FragmentWeekBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: 주말 과목 삭제시 주말이 안보여지게 안됨

        setupViewHandler()
        subscribeToObserver()

        sessionManager.getPrefs().registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sessionManager.getPrefs().unregisterOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    private val sharedPrefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                START_TIME, END_TIME, INCLUDE_WEEKEND -> { subscribeToObserver() }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViewHandler() {
        binding.weekView.setLessonClickListener {
            if (it.event.category == SUBJECT) {
                timetableDetailDialog = TimetableDetailDialog(it)
                timetableDetailDialog.isCancelable = true
                timetableDetailDialog.show(childFragmentManager, TimetableDetailDialog.TAG)

                timetableDetailDialog.setOnSelectTypeListener(object : TimetableDetailDialog.TimetableDetailDialogListener {
                    override fun onEditButtonClicked(timetableEventView: TimetableEventView) {
                        timetableFragmentListener.onEditButtonClicked(timetableEventView)
                    }
                })
            } else {
                Timber.d("DEBUG: PART_TIME_JOB Clicked!!")
            }
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

    private fun subscribeToObserver() {
        timetableViewModel.getSemester(Calendar.getInstance().time.time)

        timetableViewModel.thisSemester.observe(viewLifecycleOwner) { semester ->
            semester?.id?.let {
                timetableFragmentListener.onSemesterTitle(semester.title)
                fetchSubjectsBySemester(it)
            } ?: run {
                timetableViewModel.getLatestSemester().observe(viewLifecycleOwner) { semester ->
                    semester?.id?.let {
                        timetableFragmentListener.onSemesterTitle(semester.title)
                        fetchSubjectsBySemester(it)
                    } ?: run {
                        timetableFragmentListener.onSemesterTitle(getString(R.string.app_name))
                    }
                }
            }
        }
    }


    private fun fetchSubjectsBySemester(id: Long) {
        semesterId = id
        timetableViewModel.getAllTimetableList(id, Calendar.getInstance().time.time).observe(viewLifecycleOwner) { result ->
            removeAllEvents()
            if (!result.isNullOrEmpty()) {
                showTimetableList(result)
            }
        }
    }

    private fun showTimetableList(result: List<TimetableResponse>) {
        val config = TimetableEventConfig()
        binding.weekView.timetableConfig = config

        val timetableData: TimetableData = TimetableData(sessionManager).apply {
            for ((index, value) in result.withIndex()) {
                for (i in value.days.indices) {
                    val event = TimetableEvent.Single(
                        id = value.id!!,
                        category = value.category,
                        date = DateUtils.calculateDay(value.days[i].day),
                        title = value.title,
                        shortTitle = value.title,
                        classroom = value.classroom,
                        building = value.buildingName,
                        startTime = LocalTime.of(value.days[i].startHour, value.days[i].startMinute),
                        endTime = LocalTime.of(value.days[i].endHour, value.days[i].endMinute),
                        backgroundColor = value.backgroundColor,
                        textColor = Color.WHITE
                    )
                    this.add(event)
                }
            }
        }

        binding.weekView.addEvents(timetableData, sessionManager.getStartTime(), sessionManager.getEndTime())
    }

    private fun removeAllEvents() {
        binding.weekView.forEach { it.clearAnimation() }
        binding.weekView.removeViews(1, binding.weekView.childCount - 1)
    }

    override fun onSemesterSelected(semester: Semester) {
        semester.id?.let { id ->
            timetableFragmentListener.onSemesterTitle(semester.title)
            timetableViewModel.getAllTimetableList(id, Calendar.getInstance().time.time).observe(viewLifecycleOwner) { result ->
                removeAllEvents()
                if (!result.isNullOrEmpty()) {
                    showTimetableList(result)
                }
            }
        }
    }

    override fun onSemesterChanged() {
        subscribeToObserver()
    }

    override fun onSemesterDeleted() {
        timetableViewModel.getSemester(Calendar.getInstance().time.time)
    }

    companion object {
        private const val SUBJECT = 0
        private const val PART_TIME_JOB = 1
        fun newInstance() = TimetableFragment()
    }

}