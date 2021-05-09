package com.reachfree.timetable.ui.task

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.recyclerview.widget.GridLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.FragmentTaskBinding
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.util.SpacesItemDecoration
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

        view.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = requireActivity().windowManager.currentWindowMetrics
                val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
                val height = metrics.bounds.height

                val typedArray = requireContext().obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
                val toolbarHeight = typedArray.getDimensionPixelSize(0, -1);
                typedArray.recycle()

                val dayHeight = binding.dayOfWeekLayout.dayOfWeekLayout.height

                Timber.d("DEBUG: Height is $height")
                Timber.d("DEBUG: toolbarHeight is $toolbarHeight")
                Timber.d("DEBUG: dayHeight is $dayHeight")

                itemHeight = height - dayHeight - toolbarHeight * 2
            } else {
                @Suppress("DEPRECATION")
                val display = requireActivity().windowManager.defaultDisplay
                @Suppress("DEPRECATION") val height = display.height
                val rectangle = Rect()
                requireActivity().window.decorView.getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                val dayHeight = binding.dayOfWeekLayout.dayOfWeekLayout.height

                Timber.d("DEBUG: dayHeight is $dayHeight")

                itemHeight = height - statusBarHeight - dayHeight
            }

            Timber.d("DEBUG: itemHeight is $itemHeight")

            calendarAdapter = CalendarAdapter(dateList, calendar, itemHeight)
            binding.recyclerCalendar.adapter = calendarAdapter
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerCalendar.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
            addItemDecoration(SpacesItemDecoration(0))
        }
    }

    private fun setupCalendar() {
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

    companion object {
        private const val MAX_CALENDAR_DAYS = 35
        private const val SPAN_COUNT = 7
        fun newInstance() = TaskFragment()
    }

}