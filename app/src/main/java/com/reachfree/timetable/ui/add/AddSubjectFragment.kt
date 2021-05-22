package com.reachfree.timetable.ui.add

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.SubjectType
import com.reachfree.timetable.databinding.FragmentAddSubjectBinding
import com.reachfree.timetable.databinding.LayoutStartEndTimeBinding
import com.reachfree.timetable.extension.*
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.bottomsheet.PickerViewBottomSheet
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.util.ColorTag
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.DateUtils.updateHourAndMinute
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.widget.TimetableListWidget
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class AddSubjectFragment : BaseDialogFragment<FragmentAddSubjectBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var selectSemesterBottomSheet: PickerViewBottomSheet

    private val colorTagDialog: ColorTagDialog by lazy { ColorTagDialog() }
    private var color: ColorTag = ColorTag.COLOR_1

    private var selectedSubjectType = SubjectType.MANDATORY.ordinal

    private lateinit var selectedSemester: Semester
    private var selectedSemesterId: Long? = null
    private var passedSubjectId: Long? = null
    private var passedSubject: Subject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        passedSubjectId = requireArguments().getLong(SUBJECT_ID, -1L)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddSubjectBinding {
        return FragmentAddSubjectBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupToolbar()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupData() {
        if (passedSubjectId != null && passedSubjectId != -1L) {
            timetableViewModel.getSubjectById(passedSubjectId!!)
            timetableViewModel.subject.observe(viewLifecycleOwner) { subject ->
                if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    if (subject != null) {
                        passedSubject = subject
                        selectedSemesterId = subject.semesterId

                        setupPassedSubjectSemester(subject)
                        setupPassedSubjectInformation(subject)
                        setupPassedSubjectChip(subject)
                        binding.deleteSaveBtnLayout.btnDelete.beVisible()
                    }
                }

            }
        } else {
            setupDefaultChip()
            setupDefaultColor()
            setupDefaultToggleGroup()
            setupDefaultSubscribeToObserver()

            binding.deleteSaveBtnLayout.btnDelete.beGone()
        }
    }

    private fun setupPassedSubjectInformation(subject: Subject) {
        binding.edtSubjectTitle.setText(subject.title)
        binding.edtSubjectClassroom.setText(subject.classroom)
        binding.edtSubjectBuildingName.setText(subject.buildingName)
        binding.numberPickerCredit.progress = subject.credit

        when (subject.type) {
            SubjectType.MANDATORY.ordinal -> {
                binding.btnSubjectTypeToggleGroup.check(binding.btnMandatory.id)
            }
            SubjectType.ELECTIVE.ordinal -> {
                binding.btnSubjectTypeToggleGroup.check(binding.btnElective.id)
            }
            SubjectType.OTHER.ordinal -> {
                binding.btnSubjectTypeToggleGroup.check(binding.btnOther.id)
            }
        }

        for (i in ColorTag.values().indices) {
            if (ColorTag.values()[i].resId == subject.backgroundColor) {
                color = ColorTag.values()[i]
                binding.backgroundColor.setBackgroundResource(ColorTag.values()[i].resBg)
                break
            }
        }
    }

    private fun setupPassedSubjectSemester(subject: Subject) {
        timetableViewModel.getSemesterById(subject.semesterId)
        timetableViewModel.semesterById.observe(viewLifecycleOwner) { semester ->
            if (semester != null) {
                selectedSemester = semester
                selectedSemesterId = selectedSemester.id
                binding.btnSemester.text = semester.title
            }
        }
    }

    private fun setupPassedSubjectChip(subject: Subject) {
        val list = mutableListOf<String>()
        for (i in subject.days.indices) {
            when (subject.days[i].day) {
                1 -> {
                    binding.chipGroup.check(binding.chipSun.id)
                    createLayout(binding.chipSun.text.toString(), subject.days[i])
                    list.add(binding.chipSun.text.toString())
                }
                2 -> {
                    binding.chipGroup.check(binding.chipMon.id)
                    createLayout(binding.chipMon.text.toString(), subject.days[i])
                    list.add(binding.chipMon.text.toString())
                }
                3 -> {
                    binding.chipGroup.check(binding.chipTue.id)
                    createLayout(binding.chipTue.text.toString(), subject.days[i])
                    list.add(binding.chipTue.text.toString())
                }
                4 -> {
                    binding.chipGroup.check(binding.chipWed.id)
                    createLayout(binding.chipWed.text.toString(), subject.days[i])
                    list.add(binding.chipWed.text.toString())
                }
                5 -> {
                    binding.chipGroup.check(binding.chipThu.id)
                    createLayout(binding.chipThu.text.toString(), subject.days[i])
                    list.add(binding.chipThu.text.toString())
                }
                6 -> {
                    binding.chipGroup.check(binding.chipFri.id)
                    createLayout(binding.chipFri.text.toString(), subject.days[i])
                    list.add(binding.chipFri.text.toString())
                }
                else -> {
                    binding.chipGroup.check(binding.chipSat.id)
                    createLayout(binding.chipSat.text.toString(), subject.days[i])
                    list.add(binding.chipSat.text.toString())
                }
            }
        }

        for (index in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(index) as Chip
            chip.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    createLayout(chip.text.toString())
                    list.add(view.text.toString())
                } else {
                    binding.layoutTime.removeViewAt(list.indexOf(view.text.toString()))
                    list.remove(view.text.toString())
                }
            }
        }
    }

    private fun setupDefaultSubscribeToObserver() {
        timetableViewModel.getAllSemestersLiveData().observe(this) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                //TODO: 날짜 비교하여 해당 학기로 세팅
                selectedSemester = semesters[0]
                selectedSemesterId = selectedSemester.id
                binding.btnSemester.text = selectedSemester.title
            } else {
                showNoSemesterWaringAlert()
            }
        }
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.appBar.txtToolbarTitle.text = getString(R.string.toolbar_title_add_subject)
        binding.appBar.txtToolbarTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
        binding.appBar.btnBack.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.icon_back_arrow),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    @SuppressLint("ResourceType")
    private fun setupDefaultChip() {
        binding.chipGroup.check(binding.chipMon.id)
        binding.chipGroup.check(binding.chipWed.id)

        val list = mutableListOf<String>()
        list.add(binding.chipMon.text.toString())
        list.add(binding.chipWed.text.toString())

        for (i in 0 until list.size) {
            createLayout(list[i])
        }

        for (index in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(index) as Chip
            chip.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    createLayout(chip.text.toString())
                    list.add(view.text.toString())
                } else {
                    binding.layoutTime.removeViewAt(list.indexOf(view.text.toString()))
                    list.remove(view.text.toString())
                }
            }
        }
    }

    private fun setupDefaultColor() {
        binding.backgroundColor.setBackgroundResource(color.resBg)
    }

    private fun setupDefaultToggleGroup() {
        binding.btnSubjectTypeToggleGroup.check(binding.btnMandatory.id)
    }

    private fun setupViewHandler() {
        binding.btnSubjectTypeToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonId == -1) group.check(checkedId)

            if (isChecked) {
                when (binding.btnSubjectTypeToggleGroup.checkedButtonId) {
                    binding.btnMandatory.id -> selectedSubjectType = SubjectType.MANDATORY.ordinal
                    binding.btnElective.id -> selectedSubjectType = SubjectType.ELECTIVE.ordinal
                    binding.btnOther.id -> selectedSubjectType = SubjectType.OTHER.ordinal
                }
            }
        }

        binding.btnSemester.setOnSingleClickListener {
            selectSemesterBottomSheet = if (passedSubject != null) {
                PickerViewBottomSheet(SelectType.SEMESTER, selectedSemesterId)
            } else {
                PickerViewBottomSheet(SelectType.SEMESTER)
            }
            selectSemesterBottomSheet.isCancelable = true
            selectSemesterBottomSheet.show(childFragmentManager, PickerViewBottomSheet.TAG)

            selectSemesterBottomSheet.setOnSelectSemesterListener(object :
                PickerViewBottomSheet.SelectSemesterListener {
                override fun onSemesterSelected(semester: Semester) {
                    selectedSemester = semester
                    selectedSemesterId = selectedSemester.id
                    binding.btnSemester.text = selectedSemester.title
                }

                override fun onSubjectSelected(subject: Subject) {

                }
            })
        }
        binding.deleteSaveBtnLayout.btnSave.setOnSingleClickListener {
            saveSubject()
        }
        binding.deleteSaveBtnLayout.btnDelete.setOnSingleClickListener {
            passedSubject?.let {
                timetableViewModel.deleteSubject(it)

                TimetableListWidget.updateWidgetListView(requireContext())

                runDelayed(TIME_DELAY) {
                    requireActivity().longToast(getString(R.string.toast_delete_completed_message))
                    dismiss()
                }
            }
        }
    }

    private fun subscribeToObserver() {
        binding.backgroundColor.setOnSingleClickListener {
            colorTagDialog.show(requireActivity().supportFragmentManager, ColorTagDialog.TAG)
        }

        colorTagDialog.setColorTagSelected(object : ColorTagDialog.OnColorTagSelected {
            override fun onSelectedItem(colorTag: ColorTag) {
                color = colorTag
                binding.backgroundColor.setBackgroundResource(color.resBg)
            }
        })
    }

    private fun showNoSemesterWaringAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.text_alert_no_semester_warning_title))
            .setMessage(getString(R.string.text_alert_no_semester_warning_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_alert_button_ok)) { _, _ ->
                dismiss()
            }
            .create()
            .show()
    }

    private fun saveSubject() {
        val subjectTitle = binding.edtSubjectTitle.text.toString().trim()
        val subjectClassroom = binding.edtSubjectClassroom.text.toString().trim()
        val subjectBuildingName = binding.edtSubjectBuildingName.text.toString().trim()
        val subjectCredit = binding.numberPickerCredit.progress

        if (subjectTitle.isEmpty()) {
            requireActivity().longToast(getString(R.string.toast_subject_name_message))
            return
        }

        if (subjectClassroom.isEmpty()) {
            requireActivity().longToast(getString(R.string.toast_subject_classroom_message))
            return
        }

        if (subjectBuildingName.isEmpty()) {
            requireActivity().longToast(getString(R.string.toast_subject_building_message))
            return
        }

        if (binding.layoutTime.childCount <= 0) {
            requireActivity().longToast(getString(R.string.toast_subject_day_message))
            return
        }

        val selectedDays = mutableListOf<Subject.Days>()

        for (i in 0 until binding.layoutTime.childCount) {
            val child = binding.layoutTime.getChildAt(i)
            val day = child.findViewById<TextView>(R.id.txt_selected_day)
            val startTime = child.findViewById<TextView>(R.id.btn_start_time)
            val endTime = child.findViewById<TextView>(R.id.btn_end_time)

            val startHour = startTime.text.split(":")[0].toInt()
            val startMinute = startTime.text.split(":")[1].toInt()
            val endHour = endTime.text.split(":")[0].toInt()
            val endMinute = endTime.text.split(":")[1].toInt()

            val start = LocalTime.of(startHour, startMinute)
            val end = LocalTime.of(endHour, endMinute)

            if (start.isAfter(end)) {
                requireActivity().longToast(getString(R.string.toast_start_end_time_warning_message))
                return
            }

            val days = Subject.Days(
                day = DateUtils.convertDayNameToInt(requireContext(), day.text.toString()),
                startHour = startTime.text.split(":")[0].toInt(),
                startMinute = startTime.text.split(":")[1].toInt(),
                endHour = endTime.text.split(":")[0].toInt(),
                endMinute = endTime.text.split(":")[1].toInt()
            )
            selectedDays.add(days)
        }

        var toastMessage = ""
        if (passedSubject == null) {
            selectedSemesterId?.let { semesterId ->
                val subject = Subject(
                    null,
                    title = subjectTitle,
                    days = selectedDays,
                    classroom = subjectClassroom,
                    buildingName = subjectBuildingName,
                    credit = subjectCredit,
                    type = selectedSubjectType,
                    backgroundColor = color.resId,
                    semesterId = semesterId
                )

                timetableViewModel.insertSubject(subject)
                toastMessage = getString(R.string.toast_save_complete_message)
            } ?: run {
                requireActivity().longToast(getString(R.string.toast_error_message))
            }
        } else {
            selectedSemesterId?.let { semesterId ->
                val subject = Subject(
                    id = passedSubject!!.id,
                    title = subjectTitle,
                    days = selectedDays,
                    classroom = subjectClassroom,
                    buildingName = subjectBuildingName,
                    credit = subjectCredit,
                    type = selectedSubjectType,
                    backgroundColor = color.resId,
                    semesterId = semesterId
                )

                timetableViewModel.updateSubject(subject)
                toastMessage = getString(R.string.toast_edit_complete_message)
            } ?: run {
                requireActivity().longToast(getString(R.string.toast_error_message))
            }
        }

        TimetableListWidget.updateWidgetListView(requireContext())

        runDelayed(TIME_DELAY) {
            requireActivity().longToast(toastMessage)
            dismiss()
        }
    }

    private fun createLayout(chipName: String, days: Subject.Days? = null) {
        val layoutBinding =
            LayoutStartEndTimeBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        layoutBinding.txtSelectedDay.text = chipName

        days?.let {
            layoutBinding.btnStartTime.text = updateHourAndMinute(days.startHour, days.startMinute)
            layoutBinding.btnEndTime.text = updateHourAndMinute(days.endHour, days.endMinute)
        }

        layoutBinding.btnStartTime.setOnSingleClickListener {
            val hour = layoutBinding.btnStartTime.text.split(":")[0].toInt()
            val minute = layoutBinding.btnStartTime.text.split(":")[1].toInt()
            openTimePicker(hour, minute) { h, min ->
                layoutBinding.btnStartTime.text = updateHourAndMinute(h, min)

                val endHour = layoutBinding.btnEndTime.text.split(":")[0].toInt()
                val endMinute = layoutBinding.btnEndTime.text.split(":")[1].toInt()
                val start = LocalTime.of(h, min)
                val end = LocalTime.of(endHour, endMinute)

                if (start.isAfter(end)) {
                    layoutBinding.btnEndTime.text = updateHourAndMinute(h+1, min)
                }
            }
        }
        layoutBinding.btnEndTime.setOnSingleClickListener {
            val hour = layoutBinding.btnEndTime.text.split(":")[0].toInt()
            val minute = layoutBinding.btnEndTime.text.split(":")[1].toInt()
            openTimePicker(hour, minute) { h, min ->
                layoutBinding.btnEndTime.text = updateHourAndMinute(h, min)

                val startHour = layoutBinding.btnStartTime.text.split(":")[0].toInt()
                val startMinute = layoutBinding.btnStartTime.text.split(":")[1].toInt()
                val start = LocalTime.of(startHour, startMinute)
                val end = LocalTime.of(h, min)

                if (end.isBefore(start)) {
                    layoutBinding.btnStartTime.text = updateHourAndMinute(h-1, min)
                }
            }
        }
        binding.layoutTime.addView(layoutBinding.root)
    }

    private fun openTimePicker(hour: Int, minute: Int, action: (Int, Int) -> Unit) {
        val isSystem24Hour = is24HourFormat(requireContext())
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
        private const val TIME_PICKER_TAG = "TIME_PICKER_TAG"
        private const val SUBJECT_ID = "subject_id"
        private const val TIME_DELAY = 500L

        fun newInstance(subjectId: Long? = null) = AddSubjectFragment().apply {
            arguments = bundleOf(SUBJECT_ID to subjectId)
        }
    }


}