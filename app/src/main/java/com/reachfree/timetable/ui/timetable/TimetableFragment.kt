package com.reachfree.timetable.ui.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.FragmentWeekBinding
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.timetable.TimetableData
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.util.timetable.TimetableEvent
import com.reachfree.timetable.util.timetable.TimetableEventConfig
import com.reachfree.timetable.util.timetable.TimetableEventView
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class TimetableFragment : BaseFragment<FragmentWeekBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var timetableDetailDialog: TimetableDetailDialog

    interface TimetableFragmentListener {
        fun onEditButtonClicked(timetableEventView: TimetableEventView)
    }

    private lateinit var timetableFragmentListener: TimetableFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is HomeActivity) {
            timetableFragmentListener = activity as HomeActivity
        }
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

        setupViewHandler()
        subscribeToObserver()

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
        timetableViewModel.thisSemester.observe(viewLifecycleOwner) { semester ->
            semester?.id?.let {
                timetableViewModel.getAllSubjectBySemester(it).observe(viewLifecycleOwner) { subjects ->
                    if (!subjects.isNullOrEmpty()) {
                        removeAllEvents()
                        showThisSemesterSubjects(subjects)
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

        binding.weekView.addEvents(timetableData)
    }

    companion object {
        fun newInstance() = TimetableFragment()
    }
}