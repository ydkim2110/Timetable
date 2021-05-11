package com.reachfree.timetable.ui.task

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.databinding.CalendarDayDialogBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.extension.toMillis
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class CalendarDayDialog(
    private val date: Date,
    private val semester: Semester
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
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

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
        val startDay = DateUtils.calculateStartOfDay(DateUtils.convertDateToLocalDate(date)).toMillis()
        val endDay = DateUtils.calculateEndOfDay(DateUtils.convertDateToLocalDate(date)).toMillis()

        timetableViewModel.getAllSubjectBySemester(semester.id!!).observe(viewLifecycleOwner) { subjects ->
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