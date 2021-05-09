package com.reachfree.timetable.ui.task

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.DatePickerBinding
import com.reachfree.timetable.databinding.FragmentTaskBinding
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class TaskFragment : BaseFragment<FragmentTaskBinding>() {

    private lateinit var calendarAdapter: CalendarAdapter

    private val dateList = ArrayList<Date>()
    private var calendar = Calendar.getInstance()

    private var itemHeight = 0

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
                .setNegativeButton("취소", null)
                .setPositiveButton("OK") { dialog, which ->
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
        calendarAdapter = CalendarAdapter(dateList, calendar, itemHeight)
        binding.recyclerCalendar.adapter = calendarAdapter
    }

    private fun subscribeToObserver() {
        binding.recyclerCalendar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                itemHeight = binding.recyclerCalendar.height
                calendarAdapter = CalendarAdapter(dateList, calendar, itemHeight)
                binding.recyclerCalendar.adapter = calendarAdapter

                if (binding.recyclerCalendar.height != 0) {
                    binding.recyclerCalendar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    companion object {
        private const val MAX_CALENDAR_DAYS = 42
        private const val SPAN_COUNT = 7
        fun newInstance() = TaskFragment()
    }

}