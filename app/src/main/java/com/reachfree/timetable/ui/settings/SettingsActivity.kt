package com.reachfree.timetable.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GraduationCreditType
import com.reachfree.timetable.databinding.ActivitySettingsBinding
import com.reachfree.timetable.databinding.DatePickerBinding
import com.reachfree.timetable.databinding.DialogSettingEndTimeBinding
import com.reachfree.timetable.databinding.DialogSettingStartTimeBinding
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseActivity
import com.reachfree.timetable.util.*
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoField
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity :
    BaseActivity<ActivitySettingsBinding>({ ActivitySettingsBinding.inflate(it) }) {

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var settingCreditDialog: SettingCreditDialog

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("DEBUG: startTime ${sessionManager.getStartTime()}")
        Timber.d("DEBUG: endTime ${sessionManager.getEndTime()}")

        setupToolbar()
        setupGraduation()
        setupMandatory()
        setupElective()
        setupStartTime()
        setupEndTime()

        sessionManager.getPrefs().registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.getPrefs().unregisterOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = "설정"
        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupGraduation() {
        val credit = sessionManager.getGraduationTotalCredit()
        binding.txtGraduation.text = getString(R.string.text_input_subject_credit, credit)
        binding.layoutGraduation.setOnSingleClickListener {
            settingCreditDialog = SettingCreditDialog(GraduationCreditType.GRADUATION, credit)
            settingCreditDialog.show(supportFragmentManager, SettingCreditDialog.TAG)
            settingCreditDialog.setOnSettingCreditDialogListener(object :
                SettingCreditDialog.SettingCreditDialogListener {
                override fun onPositiveButtonClicked(type: GraduationCreditType, value: Int) {
                    sessionManager.setGraduationTotalCredit(value)
                }
            })
        }
    }

    private fun setupMandatory() {
        val credit = sessionManager.getMandatoryTotalCredit()
        binding.txtMandatory.text = getString(R.string.text_input_subject_credit, credit)
        binding.layoutMandatory.setOnSingleClickListener {
            settingCreditDialog = SettingCreditDialog(GraduationCreditType.MANDATORY, credit)
            settingCreditDialog.show(supportFragmentManager, SettingCreditDialog.TAG)
            settingCreditDialog.setOnSettingCreditDialogListener(object :
                SettingCreditDialog.SettingCreditDialogListener {
                override fun onPositiveButtonClicked(type: GraduationCreditType, value: Int) {
                    sessionManager.setMandatoryTotalCredit(value)
                }
            })
        }
    }

    private fun setupElective() {
        val credit = sessionManager.getElectiveTotalCredit()
        binding.txtElective.text = getString(R.string.text_input_subject_credit, credit)
        binding.layoutElective.setOnSingleClickListener {
            settingCreditDialog = SettingCreditDialog(GraduationCreditType.ELECTIVE, credit)
            settingCreditDialog.show(supportFragmentManager, SettingCreditDialog.TAG)
            settingCreditDialog.setOnSettingCreditDialogListener(object :
                SettingCreditDialog.SettingCreditDialogListener {
                override fun onPositiveButtonClicked(type: GraduationCreditType, value: Int) {
                    sessionManager.setElectiveTotalCredit(value)
                }
            })
        }
    }

    private fun setupStartTime() {
        val localtime = LocalTime.of(sessionManager.getStartTime(), 0)
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(0,0,0,localtime.hour,localtime.minute,localtime.second)

        binding.txtStartTime.text = DateUtils.taskDateFormat.format(cal.time.time)
        binding.layoutStartTime.setOnSingleClickListener {
            showStartTimeDialog()
        }
    }

    private fun setupEndTime() {
        val localtime = LocalTime.of(sessionManager.getEndTime(), 0)
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(0,0,0,localtime.hour,localtime.minute,localtime.second)

        binding.txtEndTime.text = DateUtils.taskDateFormat.format(cal.time.time)
        binding.layoutEndTime.setOnSingleClickListener {
            showEndTimeDialog()
        }
    }

    private fun showStartTimeDialog() {
        val startTimeBinding = DialogSettingStartTimeBinding.inflate(LayoutInflater.from(this))

        when (sessionManager.getStartTime()) {
            StartTime.AM_SIX.hour -> {
                startTimeBinding.radio6.isChecked = true
            }
            StartTime.AM_SEVEN.hour -> {
                startTimeBinding.radio7.isChecked = true
            }
            StartTime.AM_EIGHT.hour -> {
                startTimeBinding.radio8.isChecked = true
            }
            StartTime.AM_NINE.hour -> {
                startTimeBinding.radio9.isChecked = true
            }
            StartTime.AM_TEN.hour -> {
                startTimeBinding.radio10.isChecked = true
            }
        }

        AlertDialog.Builder(this)
            .setView(startTimeBinding.root)
            .setNegativeButton(getString(R.string.text_alert_button_cancel), null)
            .setPositiveButton(getString(R.string.text_alert_button_ok)) { dialog, which ->
                when (startTimeBinding.radioGroup.checkedRadioButtonId) {
                    R.id.radio_6 -> {
                        sessionManager.setStartTime(StartTime.AM_SIX.hour)
                    }
                    R.id.radio_7 -> {
                        sessionManager.setStartTime(StartTime.AM_SEVEN.hour)
                    }
                    R.id.radio_8 -> {
                        sessionManager.setStartTime(StartTime.AM_EIGHT.hour)
                    }
                    R.id.radio_9 -> {
                        sessionManager.setStartTime(StartTime.AM_NINE.hour)
                    }
                    R.id.radio_10 -> {
                        sessionManager.setStartTime(StartTime.AM_TEN.hour)
                    }
                }
            }
            .create()
            .show()
    }

    private fun showEndTimeDialog() {
        val endTimeBinding = DialogSettingEndTimeBinding.inflate(LayoutInflater.from(this))

        Timber.d("DEBUG: getEndTime is ${sessionManager.getEndTime()}")

        when (sessionManager.getEndTime()) {
            EndTime.PM_FIVE.hour -> {
                endTimeBinding.radio5.isChecked = true
            }
            EndTime.PM_SIX.hour -> {
                endTimeBinding.radio6.isChecked = true
            }
            EndTime.PM_SEVEN.hour -> {
                endTimeBinding.radio7.isChecked = true
            }
            EndTime.PM_EIGHT.hour -> {
                endTimeBinding.radio8.isChecked = true
            }
            EndTime.PM_NINE.hour -> {
                endTimeBinding.radio9.isChecked = true
            }
            EndTime.PM_TEN.hour -> {
                endTimeBinding.radio10.isChecked = true
            }
            EndTime.PM_ELEVEN.hour -> {
                endTimeBinding.radio11.isChecked = true
            }
        }

        AlertDialog.Builder(this)
            .setView(endTimeBinding.root)
            .setNegativeButton(getString(R.string.text_alert_button_cancel), null)
            .setPositiveButton(getString(R.string.text_alert_button_ok)) { dialog, which ->
                when (endTimeBinding.radioGroup.checkedRadioButtonId) {
                    R.id.radio_5 -> {
                        sessionManager.setEndTime(EndTime.PM_FIVE.hour)
                    }
                    R.id.radio_6 -> {
                        sessionManager.setEndTime(EndTime.PM_SIX.hour)
                    }
                    R.id.radio_7 -> {
                        sessionManager.setEndTime(EndTime.PM_SEVEN.hour)
                    }
                    R.id.radio_8 -> {
                        sessionManager.setEndTime(EndTime.PM_EIGHT.hour)
                    }
                    R.id.radio_9 -> {
                        sessionManager.setEndTime(EndTime.PM_NINE.hour)
                    }
                    R.id.radio_10 -> {
                        sessionManager.setEndTime(EndTime.PM_TEN.hour)
                    }
                    R.id.radio_11 -> {
                        sessionManager.setEndTime(EndTime.PM_ELEVEN.hour)
                    }
                }
            }
            .create()
            .show()
    }



    private val sharedPrefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
            Timber.d("DEBUG: Changed key is $key")
            when (key) {
                GRADUATION_CREDIT -> {
                    binding.txtGraduation.text = getString(R.string.text_input_subject_credit,
                        sessionManager.getGraduationTotalCredit())
                }
                MANDATORY_CREDIT -> {
                    binding.txtMandatory.text = getString(R.string.text_input_subject_credit,
                        sessionManager.getMandatoryTotalCredit())
                }
                ELECTIVE_CREDIT -> {
                    binding.txtElective.text = getString(R.string.text_input_subject_credit,
                        sessionManager.getElectiveTotalCredit())
                }
                START_TIME ->{
                    val localtime = LocalTime.of(sessionManager.getStartTime(), 0)
                    val cal = Calendar.getInstance()
                    cal.clear()
                    cal.set(0,0,0,localtime.hour,localtime.minute,localtime.second)

                    binding.txtStartTime.text = DateUtils.taskDateFormat.format(cal.time.time)
                }
                END_TIME ->{
                    val localtime = LocalTime.of(sessionManager.getEndTime(), 0)
                    val cal = Calendar.getInstance()
                    cal.clear()
                    cal.set(0,0,0,localtime.hour,localtime.minute,localtime.second)

                    binding.txtEndTime.text = DateUtils.taskDateFormat.format(cal.time.time)
                }
            }
        }

    companion object {
        private const val END_TIME_ADJUST_VALUE = 1
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

}