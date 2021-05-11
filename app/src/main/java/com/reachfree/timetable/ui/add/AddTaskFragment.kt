package com.reachfree.timetable.ui.add

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.*
import com.reachfree.timetable.databinding.FragmentAddTaskBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.bottomsheet.SelectSemesterBottomSheet
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.ui.setup.DatePickerFragment
import com.reachfree.timetable.ui.setup.SetupActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddTaskFragment : BaseDialogFragment<FragmentAddTaskBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var selectedSemester: Semester
    private lateinit var selectedSubject: Subject
    private lateinit var selectSemesterBottomSheet: SelectSemesterBottomSheet

    private var selectedSemesterId: Long? = null
    private var selectedSubjectId: Long? = null
    private var selectedDate = Calendar.getInstance()
    private var selectedTaskType = TaskType.TASK.ordinal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        selectedDate.time = Date(requireArguments().getLong(DATE, Date().time))
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddTaskBinding {
        return FragmentAddTaskBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        setupToolbar()
        setupView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupCalendar() {
        val startHour = binding.btnTime.text.split(":")[0].toInt()
        val startMinute = binding.btnTime.text.split(":")[1].toInt()

        selectedDate.set(Calendar.HOUR_OF_DAY, startHour)
        selectedDate.set(Calendar.MINUTE, startMinute)
        selectedDate.set(Calendar.SECOND, 0)
        selectedDate.set(Calendar.MILLISECOND, 0)
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.text = "과제 등록"
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupView() {
        binding.btnTaskTypeToggleGroup.check(binding.btnTask.id)
        binding.btnDate.text = DateUtils.defaultDateFormat.format(selectedDate.time.time)
    }

    private fun setupViewHandler() {
        binding.btnSemester.setOnSingleClickListener {
            selectSemesterBottomSheet = SelectSemesterBottomSheet(SelectType.SEMESTER)
            selectSemesterBottomSheet.isCancelable = true
            selectSemesterBottomSheet.show(childFragmentManager, SelectSemesterBottomSheet.TAG)

            setupBottomSheetListener()
        }
        binding.btnSubject.setOnSingleClickListener {
            selectedSemesterId?.let {
                selectSemesterBottomSheet = SelectSemesterBottomSheet(SelectType.SUBJECT, it)
                selectSemesterBottomSheet.isCancelable = true
                selectSemesterBottomSheet.show(childFragmentManager, SelectSemesterBottomSheet.TAG)

                setupBottomSheetListener()
            }
        }
        binding.btnTaskTypeToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonId == -1) group.check(checkedId)

            if (isChecked) {
                when (binding.btnTaskTypeToggleGroup.checkedButtonId) {
                    binding.btnTask.id -> selectedTaskType = TaskType.TASK.ordinal
                    binding.btnTest.id -> selectedTaskType = TaskType.TEST.ordinal
                }
            }
        }
        binding.btnDate.setOnSingleClickListener {
            showDatePicker(SetupActivity.START_TIME)
        }

        binding.btnTime.setOnSingleClickListener {
            openTimePicker { h, min ->
                binding.btnTime.text = updateHourAndMinute(h, min)
                selectedDate.set(Calendar.HOUR_OF_DAY, h)
                selectedDate.set(Calendar.MINUTE, min)
                selectedDate.set(Calendar.SECOND, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)
            }
        }

        binding.deleteSaveBtnLayout.btnSave.setOnSingleClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        val taskTitle = binding.edtTaskTitle.text.toString().trim()
        val taskDescription = binding.edtTaskDescription.text.toString().trim()

        selectedSemesterId?.let {
            selectedSubjectId?.let { id ->
                val task = Task(
                    id = null,
                    title = taskTitle,
                    description = taskDescription,
                    date = selectedDate.time.time,
                    type = selectedTaskType,
                    subjectId = id
                )

                timetableViewModel.insertTask(task)

                dismiss()
            }
        } ?: return
    }

    private fun showDatePicker(typeName: String) {
        DatePickerFragment.newInstance(selectedDate.time.time, typeName).apply {
            dateSelected = { year, month, dayOfMonth, type -> dateSet(year, month, dayOfMonth, type) }
        }.show(childFragmentManager, DatePickerFragment.TAG)
    }

    private fun dateSet(year: Int, month: Int, dayOfMonth: Int, type: String) {
        when (type) {
            SetupActivity.START_TIME -> {
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.btnDate.text = DateUtils.defaultDateFormat.format(selectedDate.time)
            }
        }
    }

    private fun setupBottomSheetListener() {
        selectSemesterBottomSheet.setOnSelectSemesterListener(object : SelectSemesterBottomSheet.SelectSemesterListener {
            override fun onSemesterSelected(semester: Semester) {
                selectedSemester = semester
                selectedSemesterId = selectedSemester.id
                binding.btnSemester.text = selectedSemester.title

                getAllSubjects()
            }

            override fun onSubjectSelected(subject: Subject) {
                selectedSubject = subject
                selectedSubjectId = selectedSubject.id
                binding.btnSubject.text = selectedSubject.title
            }
        })
    }


    private fun subscribeToObserver() {
        timetableViewModel.thisSemester.observe(viewLifecycleOwner) { semester ->
            if (semester != null) {
                selectedSemester = semester
                selectedSemesterId = selectedSemester.id
                binding.btnSemester.text = selectedSemester.title
                getAllSubjects()
            } else {
                timetableViewModel.getAllSemesters().observe(viewLifecycleOwner) { semesters ->
                    if (!semesters.isNullOrEmpty()) {
                        //TODO: 날짜 비교하여 해당 학기로 세팅
                        selectedSemester = semesters[0]
                        selectedSemesterId = selectedSemester.id
                        binding.btnSemester.text = selectedSemester.title
                        getAllSubjects()
                    }
                }
            }
        }
    }

    private fun getAllSubjects() {
        selectedSemesterId?.let {
            timetableViewModel.getAllSubjectBySemester(it).observe(viewLifecycleOwner) { subjects ->
                if (!subjects.isNullOrEmpty()) {
                    selectedSubject = subjects[0]
                    selectedSubjectId = selectedSubject.id
                    binding.btnSubject.text = subjects[0].title
                } else {
                    binding.btnSubject.text = "과목명"
                }
            }
        }
    }

    private fun updateHourAndMinute(h: Int, min: Int): String {
        return "${if (h < 10) "0$h" else h}:${if (min < 10) "0$min" else min}"
    }

    private fun openTimePicker(action: (Int, Int) -> Unit) {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clickFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clickFormat)
            .setHour(9)
            .setMinute(0)
            .setTitleText("시간을 입력하세요.")
            .build()
        picker.show(childFragmentManager, TIME_PICKER_TAG)

        picker.addOnPositiveButtonClickListener {
            val h = picker.hour
            val min = picker.minute
            action(h, min)
        }
        picker.addOnNegativeButtonClickListener {

        }
        picker.addOnCancelListener {

        }
        picker.addOnDismissListener {

        }
    }

    companion object {
        private const val TIME_PICKER_TAG = "add_task_fragment_time_picker_tag"
        private const val DATE = "date"

        fun newInstance(date: Long) = AddTaskFragment().apply {
            arguments = bundleOf(DATE to date)
        }
    }

}