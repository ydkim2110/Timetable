package com.reachfree.timetable.ui.task

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.databinding.CalendarDayDialogBinding
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.beVisible
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.extension.toMillis
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class CalendarDayDialog(
    private val date: Date
) : DialogFragment() {

    private val timetableViewModel: TimetableViewModel by viewModels()
    private lateinit var calendarDayDialogAdapter: CalendarDayDialogAdapter

    private var _binding: CalendarDayDialogBinding? = null
    private val binding get() = _binding!!

    interface CalendarDayDialogListener {
        fun onAddButtonClicked(date: Date)
        fun onDetailTaskClicked(data: CalendarTaskResponse)
    }

    private lateinit var calendarDayDialogListener: CalendarDayDialogListener

    fun setOnSelectTypeListener(calendarDayDialogListener: CalendarDayDialogListener) {
        this.calendarDayDialogListener = calendarDayDialogListener
    }

    override fun onStart() {
        super.onStart()
        val outMetrics = DisplayMetrics()
        val display: Display?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getRealMetrics(outMetrics)
        }
        val size = Point()
        display?.getRealSize(size)

        dialog?.window?.setLayout((size.x * 0.7).toInt(), (size.y * 0.5).toInt())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  CalendarDayDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupView() {
        binding.txtDay.text = DateUtils.dayDateFormat.format(date)
    }

    private fun setupRecyclerView() {
        binding.recyclerDayItem.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun setupViewHandler() {
        binding.btnAdd.setOnSingleClickListener {
            calendarDayDialogListener.onAddButtonClicked(date)
            dismiss()
        }
    }

    private fun subscribeToObserver() {
        fetchAllSubjects()
    }

    private fun fetchAllSubjects() {
        val startDay = DateUtils.calculateStartOfDay(DateUtils.convertDateToLocalDate(date)).toMillis()
        val endDay = DateUtils.calculateEndOfDay(DateUtils.convertDateToLocalDate(date)).toMillis()

        timetableViewModel.getAllSubjects().observe(viewLifecycleOwner) { subjects ->
            if (!subjects.isNullOrEmpty()) {
                try {
                    val subjectIdArray = LongArray(subjects.size)
                    for (i in subjects.indices) {
                        subjectIdArray[i] = subjects[i].id!!
                    }
                    timetableViewModel.getAllTaskMediator(startDay!!, endDay!!, subjectIdArray)
                } catch (e: IndexOutOfBoundsException) {
                    Timber.d("ERROR: IndexOutOfBoundsException")
                }
            }
        }

        timetableViewModel.calendarTaskList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.recyclerDayItem.beVisible()
                binding.layoutEmpty.beGone()
            } else {
                binding.recyclerDayItem.beGone()
                binding.layoutEmpty.beVisible()
            }
            calendarDayDialogAdapter = CalendarDayDialogAdapter(it)
            binding.recyclerDayItem.adapter = calendarDayDialogAdapter
            calendarDayDialogAdapter.setOnItemClickListener { data ->
                calendarDayDialogListener.onDetailTaskClicked(data)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CalendarDayDialog"
    }
}