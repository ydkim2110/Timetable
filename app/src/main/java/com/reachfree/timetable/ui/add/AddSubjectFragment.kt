package com.reachfree.timetable.ui.add

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.SubjectType
import com.reachfree.timetable.databinding.FragmentAddSubjectBinding
import com.reachfree.timetable.databinding.LayoutStartEndTimeBinding
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.beVisible
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.bottomsheet.SelectSemesterBottomSheet
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.util.ColorTag
import com.reachfree.timetable.util.DateUtils.updateHourAndMinute
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.weekview.runDelayed
import com.reachfree.timetable.widget.TimetableListWidget
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class AddSubjectFragment : BaseDialogFragment<FragmentAddSubjectBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var selectedSemester: Semester

    private lateinit var selectSemesterBottomSheet: SelectSemesterBottomSheet

    private val colorTagDialog: ColorTagDialog by lazy { ColorTagDialog() }
    private var color: ColorTag = ColorTag.COLOR_1

    private var selectedSubjectType = SubjectType.MANDATORY.ordinal

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
                if (subject != null) {
                    passedSubject = subject
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

                    binding.deleteSaveBtnLayout.btnDelete.beVisible()
                }
            }
        } else {
            setupDefaultChip()
            setupDefaultColor()
            setupDefaultToggleGroup()

            binding.deleteSaveBtnLayout.btnDelete.beGone()
        }
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple_500
            )
        )
        binding.appBar.txtToolbarTitle.text = "과목 등록"
        binding.appBar.txtToolbarTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
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
            selectSemesterBottomSheet = SelectSemesterBottomSheet(SelectType.SEMESTER)
            selectSemesterBottomSheet.isCancelable = true
            selectSemesterBottomSheet.show(childFragmentManager, SelectSemesterBottomSheet.TAG)

            selectSemesterBottomSheet.setOnSelectSemesterListener(object :
                SelectSemesterBottomSheet.SelectSemesterListener {
                override fun onSemesterSelected(semester: Semester) {
                    selectedSemester = semester
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
                runDelayed(500L) {
                    Toast.makeText(
                        requireActivity(), "삭제되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
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

        timetableViewModel.getAllSemestersLiveData().observe(this) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                //TODO: 날짜 비교하여 해당 학기로 세팅
                selectedSemester = semesters[0]
                binding.btnSemester.text = selectedSemester.title
            } else {
                showNoSemesterWaringAlert()
            }
        }
    }

    private fun showNoSemesterWaringAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.text_alert_no_semester_warning_title))
            .setMessage(getString(R.string.text_alert_no_semester_warning_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_alert_button_ok)) { dialog, which ->
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

        if (subjectClassroom.isEmpty()) {
            return
        }

        if (subjectBuildingName.isEmpty()) {
            return
        }

        val selectedDays = mutableListOf<Subject.Days>()

        //TODO: 시작시간 종료시간 차이 체크
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
                Toast.makeText(
                    requireActivity(), "시작 또는 종료시간을 확인해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val days = Subject.Days(
                day = convertDayNameToInt(day.text.toString()),
                startHour = startTime.text.split(":")[0].toInt(),
                startMinute = startTime.text.split(":")[1].toInt(),
                endHour = endTime.text.split(":")[0].toInt(),
                endMinute = endTime.text.split(":")[1].toInt()
            )
            selectedDays.add(days)
        }

        var toastMessage = ""
        if (passedSubject == null) {
            val subject = Subject(
                null,
                title = subjectTitle,
                days = selectedDays,
                classroom = subjectClassroom,
                buildingName = subjectBuildingName,
                credit = subjectCredit,
                type = selectedSubjectType,
                backgroundColor = color.resId,
                semesterId = selectedSemester.id!!
            )

            timetableViewModel.insertSubject(subject)
            toastMessage = "저장 완료!"
        } else {
            val subject = Subject(
                id = passedSubject!!.id,
                title = subjectTitle,
                days = selectedDays,
                classroom = subjectClassroom,
                buildingName = subjectBuildingName,
                credit = subjectCredit,
                type = selectedSubjectType,
                backgroundColor = color.resId,
                semesterId = selectedSemester.id!!
            )

            timetableViewModel.updateSubject(subject)
            toastMessage = "수정 완료!"
        }

        val man = AppWidgetManager.getInstance(requireActivity())
        val ids = man.getAppWidgetIds(ComponentName(requireActivity(), TimetableListWidget::class.java))
        val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        requireActivity().sendBroadcast(updateIntent)

        runDelayed(500L) {
            Toast.makeText(
                requireActivity(), toastMessage,
                Toast.LENGTH_SHORT
            ).show()
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
            }
        }
        layoutBinding.btnEndTime.setOnSingleClickListener {
            val hour = layoutBinding.btnEndTime.text.split(":")[0].toInt()
            val minute = layoutBinding.btnEndTime.text.split(":")[1].toInt()
            openTimePicker(hour, minute) { h, min ->
                layoutBinding.btnEndTime.text = updateHourAndMinute(h, min)
            }
        }
        binding.layoutTime.addView(layoutBinding.root)
    }

    private fun convertDayNameToInt(name: String): Int {
        when (name) {
            requireContext().resources.getString(R.string.sunday_short) -> {
                return 1
            }
            requireContext().resources.getString(R.string.monday_short) -> {
                return 2
            }
            requireContext().resources.getString(R.string.tuesday_short) -> {
                return 3
            }
            requireContext().resources.getString(R.string.wednesday_short) -> {
                return 4
            }
            requireContext().resources.getString(R.string.thursday_short) -> {
                return 5
            }
            requireContext().resources.getString(R.string.friday_short) -> {
                return 6
            }
            requireContext().resources.getString(R.string.saturday_short) -> {
                return 6
            }
            else -> {
                return 7
            }
        }
    }

    private fun openTimePicker(hour: Int, minute: Int, action: (Int, Int) -> Unit) {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clickFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clickFormat)
            .setHour(hour)
            .setMinute(minute)
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
        private const val TIME_PICKER_TAG = "TIME_PICKER_TAG"
        private const val SUBJECT_ID = "subject_id"
        fun newInstance(subjectId: Long? = null) = AddSubjectFragment().apply {
            arguments = bundleOf(SUBJECT_ID to subjectId)
        }
    }


}