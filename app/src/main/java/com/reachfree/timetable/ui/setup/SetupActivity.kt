package com.reachfree.timetable.ui.setup

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GradeCreditType
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.databinding.ActivitySetupBinding
import com.reachfree.timetable.extension.longToast
import com.reachfree.timetable.extension.runDelayed
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseActivity
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.SessionManager
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupActivity : BaseActivity<ActivitySetupBinding>({ ActivitySetupBinding.inflate(it) }) {

    @Inject
    lateinit var sessionManager: SessionManager

    private val timetableViewModel: TimetableViewModel by viewModels()

    private var selectedStartDate = Calendar.getInstance()
    private var selectedEndDate = Calendar.getInstance()

    private var selectedTaskType = GradeCreditType.CREDIT_4_3.ordinal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupViewHandler()
    }

    private fun setupView() {
        binding.btnSemesterStartDate.text = DateUtils.defaultDateFormat.format(selectedStartDate.time.time)
        selectedEndDate.add(Calendar.MONTH, 3)
        binding.btnSemesterEndDate.text = DateUtils.defaultDateFormat.format(selectedEndDate.time.time)

        binding.btnGradeCreditToggleGroup.check(binding.btnCredit43.id)
    }

    private fun setupViewHandler() {
        binding.btnGradeCreditToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonId == -1) group.check(checkedId)

            if (isChecked) {
                when (binding.btnGradeCreditToggleGroup.checkedButtonId) {
                    binding.btnCredit43.id -> selectedTaskType = GradeCreditType.CREDIT_4_3.ordinal
                    binding.btnCredit45.id -> selectedTaskType = GradeCreditType.CREDIT_4_5.ordinal
                }
            }
        }

        binding.btnSemesterStartDate.setOnSingleClickListener {
            showDatePicker(selectedStartDate.time.time, START_TIME)
        }
        binding.btnSemesterEndDate.setOnSingleClickListener {
            showDatePicker(selectedEndDate.time.time, END_TIME)
        }
        binding.btnSave.setOnSingleClickListener {
            saveGradeCredit()
            saveSemester()
        }
    }

    private fun saveGradeCredit() {
        sessionManager.setGradeCreditOption(selectedTaskType)
    }

    private fun saveSemester() {
        val semesterTitle = binding.edtSemesterTitle.text.toString()
        val semesterDescription = binding.edtSemesterDescription.text.toString()

        if (semesterTitle.isEmpty()) {
            longToast(getString(R.string.toast_semester_name_message))
            return
        }

        if (semesterDescription.isEmpty()) {
            longToast(getString(R.string.toast_semester_memo_message))
            return
        }

        if (selectedStartDate.get(Calendar.YEAR) == selectedEndDate.get(Calendar.YEAR) &&
            selectedStartDate.get(Calendar.MONTH) == selectedEndDate.get(Calendar.MONTH) &&
            selectedStartDate.get(Calendar.DAY_OF_MONTH) == selectedEndDate.get(Calendar.DAY_OF_MONTH)) {
            longToast(getString(R.string.toast_start_end_date_cannot_be_the_save))
            return
        }

        if (selectedStartDate.time.time > selectedEndDate.time.time) {
            longToast(getString(R.string.toast_start_end_date_warning_message))
            return
        }

        val semester = Semester(
            id = null,
            title = semesterTitle,
            description = semesterDescription,
            startDate = selectedStartDate.time.time,
            endDate = selectedEndDate.time.time,
        )

        timetableViewModel.insertSemester(semester)

        longToast(getString(R.string.toast_semester_register_complete))

        runDelayed(TIME_DELAY) {
            sessionManager.setRegisterSemester(true)
            HomeActivity.start(this)
            finish()
        }

    }

    private fun showDatePicker(date: Long, typeName: String) {
        DatePickerFragment.newInstance(date, typeName).apply {
            dateSelected = { year, month, dayOfMonth, type -> dateSet(year, month, dayOfMonth, type) }
        }.show(supportFragmentManager, DatePickerFragment.TAG)
    }

    private fun dateSet(year: Int, month: Int, dayOfMonth: Int, type: String) {
        when (type) {
            START_TIME -> {
                selectedStartDate.set(Calendar.YEAR, year)
                selectedStartDate.set(Calendar.MONTH, month)
                selectedStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.btnSemesterStartDate.text = DateUtils.defaultDateFormat.format(selectedStartDate.time)
            }
            END_TIME -> {
                selectedEndDate.set(Calendar.YEAR, year)
                selectedEndDate.set(Calendar.MONTH, month)
                selectedEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.btnSemesterEndDate.text = DateUtils.defaultDateFormat.format(selectedEndDate.time)
            }
        }
    }

    companion object {
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        private const val TIME_DELAY = 500L

        fun start(context: Context) {
            context.startActivity(Intent(context, SetupActivity::class.java))
        }
    }

}