package com.reachfree.timetable.ui.task

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.CalendarResponse
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.databinding.DatePickerBinding
import com.reachfree.timetable.databinding.FragmentTaskBinding
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.DividerItemDecoration
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class TaskFragment : BaseFragment<FragmentTaskBinding>(),
    CalendarAdapter.OnItemClickListener {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var calendarDayDialog: CalendarDayDialog

    private lateinit var calendarResponse: CalendarResponse
    private val dateList = ArrayList<Date>()
    private var calendar = Calendar.getInstance()

    private var itemHeight = 0

    interface TaskFragmentListener {
        fun onAddButtonClicked(date: Date)
        fun onDetailTaskClicked(data: CalendarTaskResponse)
    }

    private lateinit var taskFragmentListener: TaskFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is HomeActivity) {
            taskFragmentListener = activity as HomeActivity
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskBinding {
        return FragmentTaskBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCalendar()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupRecyclerView() {
        binding.recyclerCalendar.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
            addItemDecoration(DividerItemDecoration(ContextCompat.getDrawable(requireActivity(), R.drawable.recyclerview_divider)))
        }
    }

    private fun setupCalendar() {
        goToDateTime()
    }

    private fun setupViewHandler() {
        binding.imgLeftArrow.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            goToDateTime()
            updateRecyclerViewAdapter()
        }

        binding.imgRightArrow.setOnClickListener {
            calendar.add(Calendar.MONTH, +1)
            goToDateTime()
            updateRecyclerViewAdapter()
        }

        binding.txtMonth.setOnSingleClickListener {
            val datePickerBinding = DatePickerBinding.inflate(LayoutInflater.from(requireContext()))
            datePickerBinding.datePicker.findViewById<View>(Resources.getSystem()
                .getIdentifier("day", "id", "android")).beGone()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            datePickerBinding.datePicker.init(year, month, 1, null)

            AlertDialog.Builder(requireContext())
                .setView(datePickerBinding.root)
                .setNegativeButton(getString(R.string.text_alert_button_cancel), null)
                .setPositiveButton(getString(R.string.text_alert_button_ok)) { dialog, which ->
                    calendar.set(Calendar.YEAR, datePickerBinding.datePicker.year)
                    calendar.set(Calendar.MONTH, datePickerBinding.datePicker.month)
                    goToDateTime()
                    updateRecyclerViewAdapter()
                }
                .create()
                .show()
        }
    }

    private fun goToDateTime() {
        binding.txtMonth.text = updateTextMonth()

        dateList.clear()
        val monthCalendar: Calendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while (dateList.size < MAX_CALENDAR_DAYS) {
            dateList.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun updateTextMonth(): String {
        return if (calendar.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)) {
            DateUtils.yearMonthDateFormat.format(calendar.time.time)
        } else {
            DateUtils.monthDateFormat.format(calendar.time.time)
        }
    }

    private fun updateRecyclerViewAdapter() {
        calendarAdapter = CalendarAdapter(calendarResponse, calendar, itemHeight)
        binding.recyclerCalendar.adapter = calendarAdapter
        calendarAdapter.setOnItemClickListener(this)
    }

    private fun subscribeToObserver() {
        binding.recyclerCalendar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                itemHeight = binding.recyclerCalendar.height
                calendarResponse = CalendarResponse(
                    dateList = dateList
                )
                updateRecyclerViewAdapter()

                timetableViewModel.calendarTaskList.observe(viewLifecycleOwner) {
                    calendarResponse = CalendarResponse(
                        dateList = dateList,
                        taskList = it
                    )
                    updateRecyclerViewAdapter()
                }

                if (binding.recyclerCalendar.height != 0) {
                    binding.recyclerCalendar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

        // 모든 과목 가져오기
        fetchAllSubjects()

        // 해당 학기 과목만 가져옴
//        fetchAllSubjectsByThisSemester()


    }

    private fun fetchAllSubjectsByThisSemester() {
        timetableViewModel.thisSemester.observe(viewLifecycleOwner) { semester ->
            if (semester != null) {
                timetableViewModel.getAllSubjectBySemester(semester.id!!).observe(viewLifecycleOwner) { subjects ->
                    if (!subjects.isNullOrEmpty()) {
                        try {
                            val subjectIdArray = LongArray(subjects.size)
                            for (i in subjects.indices) {
                                subjectIdArray[i] = subjects[i].id!!
                            }
                            timetableViewModel.getAllTaskMediator(subjectIdArray)
                        } catch (e: IndexOutOfBoundsException) {
                            Timber.d("ERROR: IndexOutOfBoundsException")
                        }
                    }
                }
            }
        }
    }

    private fun fetchAllSubjects() {
        timetableViewModel.getAllSubjects().observe(viewLifecycleOwner) { subjects ->
            if (!subjects.isNullOrEmpty()) {
                try {
                    val subjectIdArray = LongArray(subjects.size)
                    for (i in subjects.indices) {
                        subjectIdArray[i] = subjects[i].id!!
                    }
                    timetableViewModel.getAllTaskMediator(subjectIdArray)
                } catch (e: IndexOutOfBoundsException) {
                    Timber.d("ERROR: IndexOutOfBoundsException")
                }
            }
        }
    }

    override fun onItemClick(date: Date) {
        showSelectedDayDialog(date)
    }

    private fun showSelectedDayDialog(date: Date) {
        calendarDayDialog = CalendarDayDialog(date)
        calendarDayDialog.isCancelable = true
        calendarDayDialog.show(childFragmentManager, CalendarDayDialog.TAG)
        calendarDayDialog.setOnSelectTypeListener(object : CalendarDayDialog.CalendarDayDialogListener {
            override fun onAddButtonClicked(date: Date) {
                taskFragmentListener.onAddButtonClicked(date)
            }

            override fun onDetailTaskClicked(data: CalendarTaskResponse) {
                taskFragmentListener.onDetailTaskClicked(data)
            }
        })
    }

    companion object {
        private const val MAX_CALENDAR_DAYS = 42
        private const val SPAN_COUNT = 7
        fun newInstance() = TaskFragment()
    }

}