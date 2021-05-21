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
import com.reachfree.timetable.extension.runDelayed
import com.reachfree.timetable.ui.add.AddPartTimeJobFragment
import com.reachfree.timetable.ui.add.AddSemesterFragment
import com.reachfree.timetable.ui.add.AddSubjectFragment
import com.reachfree.timetable.ui.add.AddTaskFragment
import com.reachfree.timetable.ui.base.BaseActivity
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.ui.bottomsheet.SelectTypeBottomSheet
import com.reachfree.timetable.ui.profile.grade.GradeFragment
import com.reachfree.timetable.ui.profile.grade.GradeListFragment
import com.reachfree.timetable.ui.profile.ProfileFragment
import com.reachfree.timetable.ui.profile.SemesterDetailFragment
import com.reachfree.timetable.ui.settings.SettingsActivity
import com.reachfree.timetable.ui.task.TaskFragment
import com.reachfree.timetable.ui.timetable.TimetableFragment
import com.reachfree.timetable.util.timetable.TimetableEventView
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
        semesterListDialog = SemesterListDialog().apply {
            show(supportFragmentManager, null)
            this.setOnSemesterListDialogListener(object : SemesterListDialog.SemesterListDialogListener {
                override fun onSemesterItemClicked(semester: Semester) {
                    semesterChangedListener.onSemesterSelected(semester)
                }

                override fun onSemesterAddButtonClicked() {
                    setupAddSemesterFragment()
                }
            })
        }
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
                        setupAddSemesterFragment()
                    }
                    SelectType.SUBJECT -> {
                        setupAddSubjectFragment()
                    }
                    SelectType.TASK -> {
                        setupAddTaskFragment(Calendar.getInstance().time.time)
                    }
                    SelectType.PART_TIME_JOB -> {
                        setupAddPartTimeJobFragment()
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

    override fun onSemesterItemClicked(semesterResponse: SemesterResponse) {
        val detail = SemesterDetailFragment.newInstance(semesterResponse)
            .apply { show(supportFragmentManager, SemesterDetailFragment.TAG) }

        detail.setOnSemesterDetailFragmentListener(object: SemesterDetailFragment.SemesterDetailFragmentListener {
            override fun onGradeSemesterButtonClicked(semesterId: Long) {
                setupGradeFragment(semesterId)
            }

            override fun onEditSemesterButtonClicked(semesterId: Long) {
                setupAddSemesterFragment(semesterId)
            }

            override fun onSubjectListItemClicked(subject: Subject) {
                setupAddSubjectFragment(subject.id)
            }

            override fun onAddSubjectButtonClicked() {
                setupAddSubjectFragment()
            }

            override fun onDeleteSemesterButtonClicked() {
                semesterChangedListener.onSemesterDeleted()
            }
        })
    }

    private fun setupGradeFragment(semesterId: Long) {
        GradeFragment.newInstance(semesterId).apply {
            show(supportFragmentManager, GradeFragment.TAG)
        }
    }

    private fun setupAddSemesterFragment(semesterId: Long? = null) {
        semesterId?.let {
            AddSemesterFragment.newInstance(semesterId).apply {
                show(supportFragmentManager, null)
                this.setOnAddSemesterFragmentListener(object : AddSemesterFragment.AddSemesterFragmentListener {
                    override fun onSemesterChanged() {
                        semesterChangedListener.onSemesterChanged()
                    }
                })
            }
        } ?: run {
            AddSemesterFragment.newInstance().apply {
                show(supportFragmentManager, null)
                this.setOnAddSemesterFragmentListener(object : AddSemesterFragment.AddSemesterFragmentListener {
                    override fun onSemesterChanged() {

                    }
                })
            }
        }
    }

    private fun setupAddSubjectFragment(subjectId: Long? = null) {
        subjectId?.let {
            AddSubjectFragment.newInstance(subjectId)
                .apply { show(supportFragmentManager, null) }
        } ?: run {
            AddSubjectFragment.newInstance()
                .apply { show(supportFragmentManager, null) }
        }
    }

    private fun setupAddTaskFragment(date: Long, taskId: Long? = null) {
        taskId?.let {
            AddTaskFragment.newInstance(date, it)
                .apply { show(supportFragmentManager, null) }
        } ?: run {
            AddTaskFragment.newInstance(date)
                .apply { show(supportFragmentManager, null) }
        }
    }

    private fun setupAddPartTimeJobFragment(partTimeJobId: Long? = null) {
        AddPartTimeJobFragment.newInstance(partTimeJobId).apply {
            show(supportFragmentManager, AddPartTimeJobFragment.TAG)
        }
    }

    override fun onAddSemesterButtonClicked() {
        setupAddSemesterFragment()
    }

    override fun onGoToGradeListFragmentClicked() {
        GradeListFragment.newInstance().apply {
            show(supportFragmentManager, GradeListFragment.TAG)
        }
    }

    override fun onAddButtonClicked(date: Date) {
        setupAddTaskFragment(date.time)
    }

    override fun onDetailTaskClicked(data: CalendarTaskResponse) {
        setupAddTaskFragment(data.date!!, data.id)
    }

    override fun onEditButtonClicked(timetableEventView: TimetableEventView) {
        if (timetableEventView.event.category == SUBJECT) {
            setupAddSubjectFragment(timetableEventView.event.id)
        } else {
            setupAddPartTimeJobFragment(timetableEventView.event.id)
        }
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
        private const val SUBJECT = 0
        private const val PART_TIME_JOB = 1
        private const val TAG_TIMETABLE = "1"
        private const val TAG_TASK = "2"
        private const val TAG_PROFILE = "3"
        private const val TIME_DELAY_EXIT = 1500L

        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

}