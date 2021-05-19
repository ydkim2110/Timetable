package com.reachfree.timetable.ui.add

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.model.TaskType
import com.reachfree.timetable.databinding.FragmentAddTaskBinding
import com.reachfree.timetable.extension.*
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.bottomsheet.SelectSemesterBottomSheet
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.ui.setup.DatePickerFragment
import com.reachfree.timetable.ui.setup.SetupActivity
import com.reachfree.timetable.util.ACTION_TASK_WIDGET_UPDATE
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.widget.TaskListWidget
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
    private var passedTaskId: Long? = null
    private var passedTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        selectedDate.time = Date(requireArguments().getLong(DATE, Date().time))
        passedTaskId = requireArguments().getLong(TASK_ID, -1L)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddTaskBinding {
        return FragmentAddTaskBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupToolbar()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupData() {
        if (passedTaskId != null && passedTaskId != -1L) {
            timetableViewModel.getTaskById(passedTaskId!!)
            timetableViewModel.task.observe(viewLifecycleOwner) { task ->
                if (task != null) {
                    passedTask = task

                    timetableViewModel.getSemesterByTaskId(task.id!!)
                    timetableViewModel.semesterByTaskId.observe(viewLifecycleOwner) { semester ->
                        selectedSemester = semester
                        selectedSemesterId = selectedSemester.id
                        binding.btnSemester.text = semester.title
                        getAllSubjects()
                    }

                    binding.edtTaskTitle.setText(task.title)
                    binding.edtTaskDescription.setText(task.description)

                    if (task.type == TaskType.TASK.ordinal) {
                        binding.btnTaskTypeToggleGroup.check(binding.btnTask.id)
                    } else {
                        binding.btnTaskTypeToggleGroup.check(binding.btnTest.id)
                    }

                    binding.btnDate.text = DateUtils.defaultDateFormat.format(selectedDate.time.time)

                    task.date?.let {
                        val cal = Calendar.getInstance()
                        cal.time = Date(it)
                        val hour = cal.get(Calendar.HOUR_OF_DAY)
                        val minute = cal.get(Calendar.MINUTE)

                        selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                        selectedDate.set(Calendar.MINUTE, minute)
                        selectedDate.set(Calendar.SECOND, 0)
                        selectedDate.set(Calendar.MILLISECOND, 0)

                        binding.btnTime.text = DateUtils.updateHourAndMinute(hour, minute)
                    }

                    binding.deleteSaveBtnLayout.btnDelete.beVisible()
                }
            }
        } else {
            setupDefaultData()
            setupDefaultSubscribeToObserver()
            binding.deleteSaveBtnLayout.btnDelete.beGone()
        }

    }

    private fun setupDefaultSubscribeToObserver() {
        timetableViewModel.thisSemesterLiveData.observe(viewLifecycleOwner) { semester ->
            if (semester != null) {
                selectedSemester = semester
                selectedSemesterId = selectedSemester.id
                binding.btnSemester.text = selectedSemester.title
                getAllSubjects()
            } else {
                timetableViewModel.getAllSemestersLiveData().observe(viewLifecycleOwner) { semesters ->
                    if (!semesters.isNullOrEmpty()) {
                        //TODO: 날짜 비교하여 해당 학기로 세팅
                        selectedSemester = semesters[0]
                        selectedSemesterId = selectedSemester.id
                        binding.btnSemester.text = selectedSemester.title
                        getAllSubjects()
                    } else {
                        showNoSemesterWaringAlert()
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.text = "과제 등록"
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.icon_back_arrow),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupDefaultData() {
        val startHour = binding.btnTime.text.split(":")[0].toInt()
        val startMinute = binding.btnTime.text.split(":")[1].toInt()

        selectedDate.set(Calendar.HOUR_OF_DAY, startHour)
        selectedDate.set(Calendar.MINUTE, startMinute)
        selectedDate.set(Calendar.SECOND, 0)
        selectedDate.set(Calendar.MILLISECOND, 0)

        binding.btnTaskTypeToggleGroup.check(binding.btnTask.id)
        binding.btnDate.text = DateUtils.defaultDateFormat.format(selectedDate.time.time)
    }

    private fun setupViewHandler() {
        binding.btnSemester.setOnSingleClickListener {
            selectSemesterBottomSheet = SelectSemesterBottomSheet(SelectType.SEMESTER, selectedSemesterId)
            selectSemesterBottomSheet.isCancelable = true
            selectSemesterBottomSheet.show(childFragmentManager, SelectSemesterBottomSheet.TAG)

            setupBottomSheetListener()
        }
        binding.btnSubject.setOnSingleClickListener {
            selectedSemesterId?.let {
                selectSemesterBottomSheet = SelectSemesterBottomSheet(SelectType.SUBJECT, it, selectedSubjectId)
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
            val hour = binding.btnTime.text.split(":")[0].toInt()
            val minute = binding.btnTime.text.split(":")[1].toInt()
            openTimePicker(hour, minute) { h, min ->
                binding.btnTime.text = DateUtils.updateHourAndMinute(h, min)
                selectedDate.set(Calendar.HOUR_OF_DAY, h)
                selectedDate.set(Calendar.MINUTE, min)
                selectedDate.set(Calendar.SECOND, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)
            }
        }

        binding.deleteSaveBtnLayout.btnSave.setOnSingleClickListener {
            saveTask()
        }

        binding.deleteSaveBtnLayout.btnDelete.setOnSingleClickListener {
            passedTask?.let {
                timetableViewModel.deleteTask(it)

                TaskListWidget.updateWidgetListView(requireContext())

                runDelayed(500L) {
                    Toast.makeText(requireActivity(), "삭제 완료!",
                        Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }

    private fun saveTask() {
        val taskTitle = binding.edtTaskTitle.text.toString().trim()
        val taskDescription = binding.edtTaskDescription.text.toString().trim()

        if (taskTitle.isEmpty()) {
            return
        }
        if (taskDescription.isEmpty()) {
            return
        }

        var toastMessage = ""
        if (passedTask == null) {
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
                    toastMessage = "저장 완료!"
                }
            } ?: run {
                Toast.makeText(requireActivity(), "에러가 발생했습니다. 다시 시도해주세요.",
                    Toast.LENGTH_LONG).show()
            }
        } else {
            selectedSemesterId?.let {
                selectedSubjectId?.let { id ->
                    val task = Task(
                        id = passedTask!!.id,
                        title = taskTitle,
                        description = taskDescription,
                        date = selectedDate.time.time,
                        type = selectedTaskType,
                        subjectId = id
                    )

                    timetableViewModel.updateTask(task)
                    toastMessage = "수정 완료!"
                }
            } ?: run {
                Toast.makeText(requireActivity(), "에러가 발생했습니다. 다시 시도해주세요.",
                    Toast.LENGTH_LONG).show()
            }
        }

        TaskListWidget.updateWidgetListView(requireContext())

        runDelayed(500L) {
            requireActivity().longToast(toastMessage)
            dismiss()
        }
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

    }

    private fun getAllSubjects() {
        selectedSemesterId?.let {
            timetableViewModel.getAllSubjectBySemester(it).observe(viewLifecycleOwner) { subjects ->
                if (!subjects.isNullOrEmpty()) {
                    if (passedTask != null) {
                        val subjectIdList = subjects.map { subject -> subject.id }
                        if (subjectIdList.contains(passedTask!!.subjectId)) {
                            val index = subjectIdList.indexOf(passedTask!!.subjectId)
                            selectedSubject = subjects[index]
                            selectedSubjectId = selectedSubject.id
                            binding.btnSubject.text = subjects[index].title
                        } else {
                            selectedSubject = subjects[0]
                            selectedSubjectId = selectedSubject.id
                            binding.btnSubject.text = subjects[0].title
                        }
                    } else {
                        selectedSubject = subjects[0]
                        selectedSubjectId = selectedSubject.id
                        binding.btnSubject.text = subjects[0].title
                    }
                } else {
                    showNoSubjectWaringAlert()
                    binding.btnSubject.text = getString(R.string.text_subject_name)
                }
            }
        }
    }

    private fun showNoSemesterWaringAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.text_alert_no_semester_warning_title))
            .setMessage(getString(R.string.text_alert_no_semester_warning_message_by_task))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_alert_button_ok)) { dialog, which ->
                dismiss()
            }
            .create()
            .show()
    }

    private fun showNoSubjectWaringAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.text_alert_no_semester_warning_title))
            .setMessage(getString(R.string.text_alert_no_subject_warning_message_by_task))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_alert_button_ok)) { dialog, which ->
                dismiss()
            }
            .create()
            .show()
    }

    private fun openTimePicker(hour: Int, minute: Int, action: (Int, Int) -> Unit) {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clickFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clickFormat)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText(getString(R.string.text_time_picker_title))
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
        private const val TASK_ID = "task_id"

        fun newInstance(date: Long, taskId: Long? = null) = AddTaskFragment().apply {
            arguments = bundleOf(DATE to date, TASK_ID to taskId)
        }
    }

}