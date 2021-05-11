package com.reachfree.timetable.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.databinding.FragmentAddSemesterBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.ui.setup.DatePickerFragment
import com.reachfree.timetable.ui.setup.SetupActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.weekview.runDelayed
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddSemesterFragment : BaseDialogFragment<FragmentAddSemesterBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private var selectedStartDate = Calendar.getInstance()
    private var selectedEndDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddSemesterBinding {
        return FragmentAddSemesterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        setupViewHandler()
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.text = "학기 등록"
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupView() {
        binding.btnSemesterStartDate.text = DateUtils.defaultDateFormat.format(selectedStartDate.time)
        binding.btnSemesterEndDate.text = DateUtils.defaultDateFormat.format(selectedEndDate.time)
    }

    private fun setupViewHandler() {
        binding.btnSemesterStartDate.setOnSingleClickListener {
            showDatePicker(selectedStartDate.time.time, SetupActivity.START_TIME)
        }
        binding.btnSemesterEndDate.setOnSingleClickListener {
            showDatePicker(selectedEndDate.time.time, SetupActivity.END_TIME)
        }
        binding.btnSave.setOnSingleClickListener {
            saveSemester()
        }
    }

    private fun saveSemester() {
        val semesterTitle = binding.edtSemesterTitle.text.toString()
        val semesterDescription = binding.edtSemesterDescription.text.toString()

        //TODO: 시작짜 종료날짜 체크
        val semester = Semester(
            id = null,
            title = semesterTitle,
            description = semesterDescription,
            startDate = selectedStartDate.time.time,
            endDate = selectedEndDate.time.time,
        )

        timetableViewModel.insertSemester(semester)
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
                binding.btnSemesterStartDate.text = DateUtils.defaultDateFormat.format(selectedStartDate.time)
            }
            SetupActivity.END_TIME -> {
                selectedEndDate.set(Calendar.YEAR, year)
                selectedEndDate.set(Calendar.MONTH, month)
                selectedEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.btnSemesterEndDate.text = DateUtils.defaultDateFormat.format(selectedEndDate.time)
            }
        }
    }

    companion object {
        fun newInstance() = AddSemesterFragment()
    }

}