package com.reachfree.timetable.ui.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.FragmentAddSubjectBinding
import com.reachfree.timetable.databinding.LayoutStartEndTimeBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.bottomsheet.SelectSemesterBottomSheet
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddSubjectFragment : BaseDialogFragment<FragmentAddSubjectBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var selectedSemester: Semester

    private val selectSemesterBottomSheet: SelectSemesterBottomSheet by lazy {
        SelectSemesterBottomSheet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddSubjectBinding {
        return FragmentAddSubjectBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupChip()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.text = "과목 등록"
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    @SuppressLint("ResourceType")
    private fun setupChip() {
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

    private fun setupViewHandler() {
        binding.btnSemester.setOnSingleClickListener {
            selectSemesterBottomSheet.isCancelable = true
            selectSemesterBottomSheet.show(childFragmentManager, SelectSemesterBottomSheet.TAG)
        }
        binding.deleteSaveBtnLayout.btnSave.setOnSingleClickListener {
            saveSubject()
        }
        binding.deleteSaveBtnLayout.btnDelete.setOnSingleClickListener {

        }
    }

    private fun subscribeToObserver() {
        selectSemesterBottomSheet.setOnSelectSemesterListener(object : SelectSemesterBottomSheet.SelectSemesterListener {
            override fun onSelected(semester: Semester) {
                selectedSemester = semester
            }
        })

        timetableViewModel.getAllSemesters().observe(this) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                selectedSemester = semesters[0]
                binding.btnSemester.text = selectedSemester.title
            }
        }
    }

    private fun saveSubject() {
        val subjectTitle = binding.edtSubjectTitle.text.toString().trim()
        val subjectClassroom = binding.edtSubjectClassroom.text.toString().trim()
        val subjectBuildingName = binding.edtSubjectBuildingName.text.toString().trim()

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
            val startTime = child.findViewById<TextView>(R.id.txt_start_time)
            val endTime = child.findViewById<TextView>(R.id.txt_end_time)
            val days = Subject.Days(
                day = convertDayNameToInt(day.text.toString()),
                startHour = startTime.text.split(":")[0].toInt(),
                startMinute = startTime.text.split(":")[1].toInt(),
                endHour = endTime.text.split(":")[0].toInt(),
                endMinute = endTime.text.split(":")[1].toInt()
            )
            selectedDays.add(days)
        }

        val subject = Subject(
            null,
            title = subjectTitle,
            days = selectedDays,
            classroom = subjectClassroom,
            buildingName = subjectBuildingName,
            semesterId = selectedSemester.id!!
        )

        timetableViewModel.insertSubject(subject)

        dismiss()
    }

    private fun createLayout(chipName: String) {
        val layoutBinding = LayoutStartEndTimeBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        layoutBinding.txtSelectedDay.text = chipName
        layoutBinding.txtStartTime.setOnSingleClickListener {
            openTimePicker { h, min ->
                layoutBinding.txtStartTime.text = updateHourAndMinute(h, min)
            }
        }
        layoutBinding.txtEndTime.setOnSingleClickListener {
            openTimePicker { h, min ->
                layoutBinding.txtEndTime.text = updateHourAndMinute(h, min)
            }
        }
        binding.layoutTime.addView(layoutBinding.root)
    }

    private fun convertDayNameToInt(name: String): Int {
        when (name) {
            requireContext().resources.getString(R.string.monday_short) -> {
                return 0
            }
            requireContext().resources.getString(R.string.tuesday_short) -> {
                return 1
            }
            requireContext().resources.getString(R.string.wednesday_short) -> {
                return 2
            }
            requireContext().resources.getString(R.string.thursday_short) -> {
                return 3
            }
            requireContext().resources.getString(R.string.thursday_short) -> {
                return 4
            }
            requireContext().resources.getString(R.string.friday_short) -> {
                return 5
            }
            else -> {
                return 6
            }
        }
    }

    private fun updateHourAndMinute(h: Int, min: Int): String {
        return "${if (h < 10) "0$h" else h}:${if (min < 10) "0$min" else min}"
    }

    private fun openTimePicker(action: (Int, Int) -> Unit) {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clickFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clickFormat)
            .setHour(12)
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
        private const val TIME_PICKER_TAG = "TIME_PICKER_TAG"
        fun newInstance() = AddSubjectFragment()
    }


}