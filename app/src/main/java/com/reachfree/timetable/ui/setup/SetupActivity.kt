package com.reachfree.timetable.ui.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.databinding.ActivitySetupBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseActivity
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.SessionManager
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.extension.runDelayed
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupActivity : BaseActivity<ActivitySetupBinding>({ ActivitySetupBinding.inflate(it) }) {

    @Inject
    lateinit var sessionManager: SessionManager

    private val timetableViewModel: TimetableViewModel by viewModels()

    private var selectedStartDate = Calendar.getInstance()
    private var selectedEndDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupViewHandler()
    }

    private fun setupView() {
        binding.btnSemesterStartDate.text = DateUtils.defaultDateFormat.format(selectedStartDate.time)
        binding.btnSemesterEndDate.text = DateUtils.defaultDateFormat.format(selectedEndDate.time)
    }

    private fun setupViewHandler() {
        binding.btnSemesterStartDate.setOnSingleClickListener {
            showDatePicker(selectedStartDate.time.time, START_TIME)
        }
        binding.btnSemesterEndDate.setOnSingleClickListener {
            showDatePicker(selectedEndDate.time.time, END_TIME)
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

        Toast.makeText(this, resources.getString(R.string.toast_semester_register_complete),
            Toast.LENGTH_SHORT).show()

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
        private const val TIME_DELAY = 1500L

        fun start(context: Context) {
            context.startActivity(Intent(context, SetupActivity::class.java))
        }
    }

}