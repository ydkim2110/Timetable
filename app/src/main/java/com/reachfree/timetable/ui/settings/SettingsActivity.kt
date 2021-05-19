package com.reachfree.timetable.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GradeCreditType
import com.reachfree.timetable.data.model.GraduationCreditType
import com.reachfree.timetable.databinding.ActivitySettingsBinding
import com.reachfree.timetable.databinding.DialogSettingEndTimeBinding
import com.reachfree.timetable.databinding.DialogSettingGradeCreditOptionBinding
import com.reachfree.timetable.databinding.DialogSettingStartTimeBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseActivity
import com.reachfree.timetable.util.*
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime
import java.util.*
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class SettingsActivity :
    BaseActivity<ActivitySettingsBinding>({ ActivitySettingsBinding.inflate(it) }) {

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var settingCreditDialog: SettingCreditDialog

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupGraduation()
        setupMandatory()
        setupElective()
        setupStartTime()
        setupEndTime()
        setupIncludeWeekend()
        setupGradeCreditOption()

        sessionManager.getPrefs().registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.getPrefs().unregisterOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = getString(R.string.text_settings)
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
            StartTime.AM_ELEVEN.hour -> {
                startTimeBinding.radio11.isChecked = true
            }
            StartTime.AM_TWELVE.hour -> {
                startTimeBinding.radio12.isChecked = true
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
                    R.id.radio_11 -> {
                        sessionManager.setStartTime(StartTime.AM_ELEVEN.hour)
                    }
                    R.id.radio_12 -> {
                        sessionManager.setStartTime(StartTime.AM_TWELVE.hour)
                    }
                }
            }
            .create()
            .show()
    }

    private fun showEndTimeDialog() {
        val endTimeBinding = DialogSettingEndTimeBinding.inflate(LayoutInflater.from(this))

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

    private fun setupIncludeWeekend() {
        binding.switchIncludeWeekend.isChecked = sessionManager.getIncludeWeekend()
        binding.switchIncludeWeekend.setOnCheckedChangeListener { buttonView, isChecked ->
            sessionManager.setIncludeWeekend(isChecked)
        }
    }

    private fun setupGradeCreditOption() {
        binding.txtGradeCreditOption.text = GradeCreditType.values()
            .find { it.ordinal == sessionManager.getGradeCreditOption() }?.stringRes?.let {
                getString(it)
            } ?: getString(GradeCreditType.CREDIT_4_3.stringRes)
        binding.layoutGradeCreditOption.setOnSingleClickListener {
            showGradeCreditOptionDialog()
        }
    }

    private fun showGradeCreditOptionDialog() {
        val gradeCreditOption = DialogSettingGradeCreditOptionBinding.inflate(LayoutInflater.from(this))

        when (sessionManager.getGradeCreditOption()) {
            GradeCreditType.CREDIT_4_3.ordinal -> {
                gradeCreditOption.radio43.isChecked = true
            }
            GradeCreditType.CREDIT_4_5.ordinal -> {
                gradeCreditOption.radio45.isChecked = true
            }
        }

        AlertDialog.Builder(this)
            .setView(gradeCreditOption.root)
            .setNegativeButton(getString(R.string.text_alert_button_cancel), null)
            .setPositiveButton(getString(R.string.text_alert_button_ok)) { dialog, which ->
                when (gradeCreditOption.radioGroup.checkedRadioButtonId) {
                    R.id.radio_4_3 -> {
                        sessionManager.setGradeCreditOption(GradeCreditType.CREDIT_4_3.ordinal)
                    }
                    R.id.radio_4_5 -> {
                        sessionManager.setGradeCreditOption(GradeCreditType.CREDIT_4_5.ordinal)
                    }
                }
            }
            .create()
            .show()
    }

    private val sharedPrefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
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
                START_TIME -> {
                    val localtime = LocalTime.of(sessionManager.getStartTime(), 0)
                    val cal = Calendar.getInstance()
                    cal.clear()
                    cal.set(0,0,0,localtime.hour,localtime.minute,localtime.second)

                    binding.txtStartTime.text = DateUtils.taskDateFormat.format(cal.time.time)
                    showRestartDialog()
                }
                END_TIME -> {
                    val localtime = LocalTime.of(sessionManager.getEndTime(), 0)
                    val cal = Calendar.getInstance()
                    cal.clear()
                    cal.set(0,0,0,localtime.hour,localtime.minute,localtime.second)

                    binding.txtEndTime.text = DateUtils.taskDateFormat.format(cal.time.time)
                    showRestartDialog()
                }
                CREDIT_OPTION -> {
                    binding.txtGradeCreditOption.text = GradeCreditType.values()
                        .find { it.ordinal == sessionManager.getGradeCreditOption() }?.stringRes?.let {
                            getString(it)
                        } ?: getString(GradeCreditType.CREDIT_4_3.stringRes)
                }
            }
        }

    private fun showRestartDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("재시작")
            .setMessage("변경된 사항을 반영하기 위하여 앱을 재시작합니다.")
            .setPositiveButton(
                "재시작"
            ) { _, _ -> }
            .setOnDismissListener {
                restart()
            }
            .create().show()
    }

    private fun restart() {
        finishAffinity()
        startActivity(packageManager.getLaunchIntentForPackage(packageName))
        exitProcess(0)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

}