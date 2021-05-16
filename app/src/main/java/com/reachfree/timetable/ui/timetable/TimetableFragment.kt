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
import androidx.fragment.app.activityViewModels
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
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
    private val timetableViewModel: TimetableViewModel by activityViewModels()

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

    @RequiresApi(Build.VERSION_CODES.N)
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
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
            when (key) {
                START_TIME, END_TIME, INCLUDE_WEEKEND -> { subscribeToObserver() }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViewHandler() {
        binding.weekView.setLessonClickListener {
            timetableDetailDialog = TimetableDetailDialog(it)
            timetableDetailDialog.isCancelable = true
            timetableDetailDialog.show(childFragmentManager, TimetableDetailDialog.TAG)

            timetableDetailDialog.setOnSelectTypeListener(object : TimetableDetailDialog.TimetableDetailDialogListener {
                override fun onEditButtonClicked(timetableEventView: TimetableEventView) {
                    timetableFragmentListener.onEditButtonClicked(timetableEventView)
                }
            })
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
        Timber.d("DEBUG: subscribeToObserver!!")
        timetableViewModel.getSemester(Calendar.getInstance().time.time)
        timetableViewModel.thisSemester.observe(viewLifecycleOwner) { semester ->
            Timber.d("DEBUG: thisSemester!!")
            semester?.id?.let {
                timetableFragmentListener.onSemesterTitle(semester.title)
                fetchSubjectsBySemester(it)
            } ?: run {
                timetableViewModel.getLatestSemester().observe(viewLifecycleOwner) { semester ->
                    Timber.d("DEBUG: getLatestSemester!!")
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
        timetableViewModel.getAllSubjectBySemester(id).observe(viewLifecycleOwner) { subjects ->
            removeAllEvents()
            if (!subjects.isNullOrEmpty()) {
                showThisSemesterSubjects(subjects)
            }
        }
    }

    private fun removeAllEvents() {
        binding.weekView.forEach { it.clearAnimation() }
        binding.weekView.removeViews(1, binding.weekView.childCount - 1)
    }

    private fun showThisSemesterSubjects(subjects: List<Subject>) {
        val config = TimetableEventConfig()
        binding.weekView.timetableConfig = config

        val timetableData: TimetableData = TimetableData(sessionManager).apply {
            for ((index, value) in subjects.withIndex()) {
                for (i in value.days.indices) {
                    val event = TimetableEvent.Single(
                            id = value.id!!,
                            date = DateUtils.calculateDay(value.days[i].day),
                            title = value.title,
                            shortTitle = value.title,
                            classroom = value.classroom,
                            building = value.buildingName,
                            credit = value.credit,
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

    override fun onSemesterChanged(semester: Semester) {
        Timber.d("DEBUG: onSemesterChanged $semester")
        semester.id?.let { id ->
            timetableFragmentListener.onSemesterTitle(semester.title)
            timetableViewModel.getAllSubjectBySemester(id).observe(viewLifecycleOwner) { subjects ->
                removeAllEvents()
                if (!subjects.isNullOrEmpty()) {
                    showThisSemesterSubjects(subjects)
                }
            }
        }
    }

    override fun onSemesterDeleted() {
        timetableViewModel.getSemester(Calendar.getInstance().time.time)
    }

    companion object {
        fun newInstance() = TimetableFragment()
    }

}