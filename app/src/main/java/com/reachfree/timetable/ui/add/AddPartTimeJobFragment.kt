package com.reachfree.timetable.ui.add

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.PartTimeJob
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.FragmentAddPartTimeJobBinding
import com.reachfree.timetable.databinding.LayoutStartEndTimeBinding
import com.reachfree.timetable.extension.*
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.setup.DatePickerFragment
import com.reachfree.timetable.ui.setup.SetupActivity
import com.reachfree.timetable.util.ColorTag
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.DateUtils.updateHourAndMinute
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime
import java.util.*

@AndroidEntryPoint
class AddPartTimeJobFragment : BaseDialogFragment<FragmentAddPartTimeJobBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private var selectedStartDate = Calendar.getInstance()
    private var selectedEndDate = Calendar.getInstance()

    private var passedPartTimeJobId: Long? = null

    private val colorTagDialog: ColorTagDialog by lazy { ColorTagDialog() }
    private var color: ColorTag = ColorTag.COLOR_1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        passedPartTimeJobId = requireArguments().getLong(PART_TIME_JOB_ID, DEFAULT_ID)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddPartTimeJobBinding {
        return FragmentAddPartTimeJobBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupToolbar()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupData() {
        if (passedPartTimeJobId != null && passedPartTimeJobId != DEFAULT_ID) {
            timetableViewModel.getPartTimeJobById(passedPartTimeJobId!!)
            timetableViewModel.partTimeJobById.observe(viewLifecycleOwner) { partTimeJob ->
                if (partTimeJob != null) {
                    setupPassedPartTimeJobInformation(partTimeJob)
                    setupPassedPartTimeJobChip(partTimeJob)

                    binding.deleteSaveBtnLayout.btnDelete.beVisible()
                }
            }
        } else {
            setupDefaultView()
            setupDefaultColor()
            setupDefaultChip()

            binding.deleteSaveBtnLayout.btnDelete.beGone()
        }
    }

    private fun setupPassedPartTimeJobInformation(partTimeJob: PartTimeJob) {
        binding.edtPartTimeJobTitle.setText(partTimeJob.title)
        binding.btnPartTimeJobStartDate.text = DateUtils.defaultDateFormat.format(partTimeJob.startDate)
        binding.btnPartTimeJobEndDate.text = DateUtils.defaultDateFormat.format(partTimeJob.endDate)

        selectedStartDate.time = Date(partTimeJob.startDate)
        selectedEndDate.time = Date(partTimeJob.endDate)

        for (i in ColorTag.values().indices) {
            if (ColorTag.values()[i].resId == partTimeJob.backgroundColor) {
                color = ColorTag.values()[i]
                binding.backgroundColor.setBackgroundResource(ColorTag.values()[i].resBg)
                break
            }
        }
    }

    private fun setupPassedPartTimeJobChip(partTimeJob: PartTimeJob) {
        val list = mutableListOf<String>()
        for (i in partTimeJob.days.indices) {
            when (partTimeJob.days[i].day) {
                1 -> {
                    binding.chipGroup.check(binding.chipSun.id)
                    createLayout(binding.chipSun.text.toString(), partTimeJob.days[i])
                    list.add(binding.chipSun.text.toString())
                }
                2 -> {
                    binding.chipGroup.check(binding.chipMon.id)
                    createLayout(binding.chipMon.text.toString(), partTimeJob.days[i])
                    list.add(binding.chipMon.text.toString())
                }
                3 -> {
                    binding.chipGroup.check(binding.chipTue.id)
                    createLayout(binding.chipTue.text.toString(), partTimeJob.days[i])
                    list.add(binding.chipTue.text.toString())
                }
                4 -> {
                    binding.chipGroup.check(binding.chipWed.id)
                    createLayout(binding.chipWed.text.toString(), partTimeJob.days[i])
                    list.add(binding.chipWed.text.toString())
                }
                5 -> {
                    binding.chipGroup.check(binding.chipThu.id)
                    createLayout(binding.chipThu.text.toString(), partTimeJob.days[i])
                    list.add(binding.chipThu.text.toString())
                }
                6 -> {
                    binding.chipGroup.check(binding.chipFri.id)
                    createLayout(binding.chipFri.text.toString(), partTimeJob.days[i])
                    list.add(binding.chipFri.text.toString())
                }
                else -> {
                    binding.chipGroup.check(binding.chipSat.id)
                    createLayout(binding.chipSat.text.toString(), partTimeJob.days[i])
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

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.appBar.txtToolbarTitle.text = "알바 등록"
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

    private fun setupDefaultView() {
        binding.btnPartTimeJobStartDate.text = DateUtils.defaultDateFormat.format(selectedStartDate.time.time)
        selectedEndDate.add(Calendar.MONTH, 3)
        binding.btnPartTimeJobEndDate.text = DateUtils.defaultDateFormat.format(selectedEndDate.time.time)
    }

    private fun setupDefaultColor() {
        binding.backgroundColor.setBackgroundResource(color.resBg)
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

    private fun setupViewHandler() {
        binding.btnPartTimeJobStartDate.setOnSingleClickListener {
            showDatePicker(selectedStartDate.time.time, SetupActivity.START_TIME)
        }
        binding.btnPartTimeJobEndDate.setOnSingleClickListener {
            showDatePicker(selectedEndDate.time.time, SetupActivity.END_TIME)
        }
        binding.deleteSaveBtnLayout.btnSave.setOnSingleClickListener {
            savePartTimeJob()
        }
        binding.deleteSaveBtnLayout.btnDelete.setOnSingleClickListener {

        }

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

    private fun showDatePicker(date: Long, typeName: String) {
        DatePickerFragment.newInstance(date, typeName).apply {
            dateSelected = { year, month, dayOfMonth, type -> dateSet(year, month, dayOfMonth, type) }
        }.show(childFragmentManager, DatePickerFragment.TAG)
    }

    private fun dateSet(year: Int, month: Int, dayOfMonth: Int, type: String) {
        when (type) {
            SetupActivity.START_TIME -> {
                selectedStartDate.set(Calendar.YEAR, year)
                selectedStartDate.set(Calendar.MONTH, month)
                selectedStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.btnPartTimeJobStartDate.text = DateUtils.defaultDateFormat.format(selectedStartDate.time)
            }
            SetupActivity.END_TIME -> {
                selectedEndDate.set(Calendar.YEAR, year)
                selectedEndDate.set(Calendar.MONTH, month)
                selectedEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.btnPartTimeJobEndDate.text = DateUtils.defaultDateFormat.format(selectedEndDate.time)
            }
        }
    }

    private fun subscribeToObserver() {
    }

    private fun savePartTimeJob() {
        val partTimeJobTitle = binding.edtPartTimeJobTitle.text.toString().trim()

        if (partTimeJobTitle.isEmpty()) {
            requireActivity().longToast("알바명을 입력해주세요.")
            return
        }

        if (selectedStartDate.time.time > selectedEndDate.time.time) {
            requireActivity().longToast(getString(R.string.toast_start_end_date_warning_message))
            return
        }

        val startDate = DateUtils.calculateStartOfDay(
            DateUtils.convertDateToLocalDate(Date(selectedStartDate.time.time))).toMillis()!!
        val endDate = DateUtils.calculateEndOfDay(
            DateUtils.convertDateToLocalDate(Date(selectedEndDate.time.time))).toMillis()!!

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
        if (passedPartTimeJobId != null && passedPartTimeJobId != -DEFAULT_ID) {
            val partTimeJob = PartTimeJob(
                id = passedPartTimeJobId,
                title = partTimeJobTitle,
                startDate = startDate,
                endDate = endDate,
                days = selectedDays,
                backgroundColor = color.resId
            )

            timetableViewModel.updatePartTimeJob(partTimeJob)
            toastMessage = requireActivity().getString(R.string.toast_edit_complete_message)
        } else {
            val partTimeJob = PartTimeJob(
                id = null,
                title = partTimeJobTitle,
                startDate = startDate,
                endDate = endDate,
                days = selectedDays,
                backgroundColor = color.resId
            )

            timetableViewModel.insertPartTimeJob(partTimeJob)
            toastMessage = requireActivity().getString(R.string.toast_save_complete_message)
        }


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
        const val TAG = "AddPartTimeJobFragment"
        private const val DEFAULT_ID = -1L
        private const val TIME_PICKER_TAG = "TIME_PICKER_TAG"
        private const val SUBJECT_ID = "subject_id"
        private const val TIME_DELAY = 500L
        private const val PART_TIME_JOB_ID = "part_time_job_id"

        fun newInstance(partTimeJobId: Long? = null) = AddPartTimeJobFragment().apply {
            arguments = bundleOf(PART_TIME_JOB_ID to partTimeJobId)
        }
    }


}