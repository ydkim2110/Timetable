package com.reachfree.timetable.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.data.response.SemesterResponse
import com.reachfree.timetable.databinding.ActivityHomeBinding
import com.reachfree.timetable.ui.add.AddSemesterFragment
import com.reachfree.timetable.ui.add.AddSubjectFragment
import com.reachfree.timetable.ui.add.AddTaskFragment
import com.reachfree.timetable.ui.base.BaseActivity
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.ui.bottomsheet.SelectTypeBottomSheet
import com.reachfree.timetable.ui.profile.ProfileFragment
import com.reachfree.timetable.ui.profile.SemesterDetailFragment
import com.reachfree.timetable.ui.settings.SettingsActivity
import com.reachfree.timetable.ui.task.TaskFragment
import com.reachfree.timetable.ui.timetable.TimetableFragment
import com.reachfree.timetable.util.timetable.TimetableEventView
import com.reachfree.timetable.extension.runDelayed
import com.reachfree.timetable.util.ACTION_TASK
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>({ ActivityHomeBinding.inflate(it)}),
    BottomNavigationView.OnNavigationItemSelectedListener,
    TimetableFragment.TimetableFragmentListener,
    TaskFragment.TaskFragmentListener, ProfileFragment.ProfileFragmentListener {

    private lateinit var selectTypeBottomSheet: SelectTypeBottomSheet
    private lateinit var semesterListDialog: SemesterListDialog

    private val timetableFragment = TimetableFragment.newInstance()
    private val taskFragment = TaskFragment.newInstance()
    private val profileFragment = ProfileFragment.newInstance()
    private val fm = supportFragmentManager
    private var active: Fragment = timetableFragment

    private var doubleBackToExit = false

    private lateinit var menu: Menu

    private lateinit var semesterChangedListener: SemesterChangedListener

    fun setOnSemesterChangedListener(semesterChangedListener: SemesterChangedListener) {
        this.semesterChangedListener = semesterChangedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupBottomNavigationView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupBottomNavigationView() {
        fm.beginTransaction()
            .add(binding.container.id, profileFragment, TAG_PROFILE)
            .hide(profileFragment)
            .commit()

        fm.beginTransaction()
            .add(binding.container.id, taskFragment, TAG_TASK)
            .hide(taskFragment)
            .commit()

        fm.beginTransaction()
            .add(binding.container.id, timetableFragment, TAG_TIMETABLE)
            .commit()

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun showSemesterListDialog() {
        semesterListDialog = SemesterListDialog()
        semesterListDialog.show(supportFragmentManager, null)
        semesterListDialog.setOnSemesterListDialogListener(object : SemesterListDialog.SemesterListDialogListener {
            override fun onSemesterItemClicked(semester: Semester) {
                semesterChangedListener.onSemesterChanged(semester)
            }

            override fun onSemesterAddButtonClicked() {
                AddSemesterFragment.newInstance()
                    .apply { show(supportFragmentManager, null) }
            }
        })
    }

    private fun showSelectTypeBottomSheet() {
        selectTypeBottomSheet = SelectTypeBottomSheet()
        selectTypeBottomSheet.isCancelable = true
        selectTypeBottomSheet.show(supportFragmentManager, SelectTypeBottomSheet.TAG)
        setupSelectTypeListener()
    }

    private fun setupSelectTypeListener() {
        selectTypeBottomSheet.setOnSelectTypeListener(object :
            SelectTypeBottomSheet.SelectTypeListener {
            override fun onSelected(type: SelectType) {
                when (type) {
                    SelectType.SEMESTER -> {
                        AddSemesterFragment.newInstance()
                            .apply { show(supportFragmentManager, null) }
                    }
                    SelectType.SUBJECT -> {
                        AddSubjectFragment.newInstance()
                            .apply { show(supportFragmentManager, null) }
                    }
                    SelectType.TASK -> {
                        AddTaskFragment.newInstance(Date().time)
                            .apply { show(supportFragmentManager, null) }
                    }
                }
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.toolbar_settings).isVisible = false
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_timetable -> {
                showSemesterListDialog()
            }
            R.id.toolbar_add -> {
                showSelectTypeBottomSheet()
            }
            R.id.toolbar_settings -> {
                SettingsActivity.start(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bottom_week -> {
                fm.beginTransaction().hide(active).show(timetableFragment).commit()
                active = timetableFragment
                showTimetableFragmentMenu()
                return true
            }
            R.id.bottom_task -> {
                fm.beginTransaction().hide(active).show(taskFragment).commit()
                active = taskFragment
                showTaskFragmentMenu()
                return true
            }
            R.id.bottom_profile -> {
                fm.beginTransaction().hide(active).show(profileFragment).commit()
                active = profileFragment
                showProfileFragmentMenu()
                return true
            }
        }
        return false
    }

    private fun showTimetableFragmentMenu() {
        menu.findItem(R.id.toolbar_timetable).isVisible = true
        menu.findItem(R.id.toolbar_add).isVisible = true
        menu.findItem(R.id.toolbar_settings).isVisible = false
    }

    private fun showTaskFragmentMenu() {
        menu.findItem(R.id.toolbar_timetable).isVisible = false
        menu.findItem(R.id.toolbar_add).isVisible = true
        menu.findItem(R.id.toolbar_settings).isVisible = false
    }

    private fun showProfileFragmentMenu() {
        menu.findItem(R.id.toolbar_timetable).isVisible = false
        menu.findItem(R.id.toolbar_add).isVisible = false
        menu.findItem(R.id.toolbar_settings).isVisible = true
    }

    override fun onSemesterItemClicked(semester: SemesterResponse) {
        val detail = SemesterDetailFragment.newInstance(semester)
            .apply { show(supportFragmentManager, SemesterDetailFragment.TAG) }

        detail.setOnSemesterDetailFragmentListener(object: SemesterDetailFragment.SemesterDetailFragmentListener {
            override fun onEditButtonClicked(semesterId: Long) {
                AddSemesterFragment.newInstance(semesterId)
                    .apply { show(supportFragmentManager, null) }
            }

            override fun onSubjectItemClicked(subject: Subject) {
                AddSubjectFragment.newInstance(subject.id)
                    .apply { show(supportFragmentManager, null) }
            }

            override fun onAddButtonClicked() {
                AddSubjectFragment.newInstance()
                    .apply { show(supportFragmentManager, null) }
            }

            override fun onSemesterDeleteButtonClicked() {
                semesterChangedListener.onSemesterDeleted()
            }
        })
    }

    override fun onAddSemesterButtonClicked() {
        AddSemesterFragment.newInstance()
            .apply { show(supportFragmentManager, null) }
    }

    override fun onAddButtonClicked(date: Date) {
        AddTaskFragment.newInstance(date.time)
            .apply { show(supportFragmentManager, null) }
    }

    override fun onDetailTaskClicked(data: CalendarTaskResponse) {
        AddTaskFragment.newInstance(data.date!!, data.id)
            .apply { show(supportFragmentManager, null) }
    }

    override fun onEditButtonClicked(timetableEventView: TimetableEventView) {
        AddSubjectFragment.newInstance(timetableEventView.event.id)
            .apply { show(supportFragmentManager, null) }
    }

    override fun onSemesterTitle(title: String) {
        binding.toolbar.title = title
    }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, getString(R.string.toast_exit_message),
                Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(TIME_DELAY_EXIT) {
                doubleBackToExit = false
            }
        }
    }

    companion object {
        private const val TAG_TIMETABLE = "1"
        private const val TAG_TASK = "2"
        private const val TAG_PROFILE = "3"
        private const val TIME_DELAY_EXIT = 1500L

        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

}