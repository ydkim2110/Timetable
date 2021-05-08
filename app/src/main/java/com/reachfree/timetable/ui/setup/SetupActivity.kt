package com.reachfree.timetable.ui.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.ActivitySetupBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseActivity
import timber.log.Timber

class SetupActivity : BaseActivity<ActivitySetupBinding>({ ActivitySetupBinding.inflate(it) }) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewHandler()
    }

    private fun setupViewHandler() {
        binding.btnSemesterStartDate.setOnSingleClickListener {
            showDatePicker(START_TIME)
        }
        binding.btnSemesterEndDate.setOnSingleClickListener {
            showDatePicker(END_TIME)
        }
        binding.btnSave.setOnSingleClickListener {

        }
    }

    private fun showDatePicker(typeName: String) {
        DatePickerFragment.newInstance(typeName).apply {
            dateSelected = { year, month, dayOfMonth, type -> dateSet(year, month, dayOfMonth, type) }
        }.show(supportFragmentManager, DatePickerFragment.TAG)
    }

    private fun dateSet(year: Int, month: Int, dayOfMonth: Int, type: String) {
        Timber.d("DEBUG: selected date $year, $month, $dayOfMonth, $type")
    }

    companion object {
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"

        fun start(context: Context) {
            context.startActivity(Intent(context, SetupActivity::class.java))
        }
    }

}