package com.reachfree.timetable.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.response.SemesterTotalCreditResponse
import com.reachfree.timetable.databinding.ActivityHomeBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.add.AddSemesterFragment
import com.reachfree.timetable.ui.add.AddSubjectFragment
import com.reachfree.timetable.ui.add.AddTaskFragment
import com.reachfree.timetable.ui.base.BaseActivity
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.ui.bottomsheet.SelectTypeBottomSheet
import com.reachfree.timetable.ui.profile.ProfileFragment
import com.reachfree.timetable.ui.profile.ProfileHandlerListener
import com.reachfree.timetable.ui.profile.SubjectListFragment
import com.reachfree.timetable.ui.task.TaskFragment
import com.reachfree.timetable.ui.timetable.TimetableFragment
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.weekview.runDelayed
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>({ ActivityHomeBinding.inflate(it)}),
    BottomNavigationView.OnNavigationItemSelectedListener, ProfileHandlerListener {

    private lateinit var selectTypeBottomSheet: SelectTypeBottomSheet

    private val timetableFragment = TimetableFragment.newInstance()
    private val taskFragment = TaskFragment.newInstance()
    private val profileFragment = ProfileFragment.newInstance()
    private val fm = supportFragmentManager
    private var active: Fragment = timetableFragment

    private var doubleBackToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupBottomNavigationView()
        setupViewHandler()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = DateUtils.defaultDateFormat.format(Date())
        }
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

    private fun setupViewHandler() {
        binding.fab.setOnSingleClickListener {
            when (active) {
                is TimetableFragment -> {
                    showSelectTypeBottomSheet("timetable")
                }
                is TaskFragment -> {
                    showSelectTypeBottomSheet("task")
                }
            }

        }
    }

    private fun showSelectTypeBottomSheet(fragmentName: String) {
        selectTypeBottomSheet = SelectTypeBottomSheet(fragmentName)
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
                    SelectType.TEST -> {

                    }
                    SelectType.TASK -> {
                        AddTaskFragment.newInstance()
                            .apply { show(supportFragmentManager, null) }
                    }
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bottom_week -> {
                fm.beginTransaction().hide(active).show(timetableFragment).commit()
                active = timetableFragment
                return true
            }
            R.id.bottom_task -> {
                fm.beginTransaction().hide(active).show(taskFragment).commit()
                active = taskFragment
                return true
            }
            R.id.bottom_profile -> {
                fm.beginTransaction().hide(active).show(profileFragment).commit()
                active = profileFragment
                return true
            }
        }
        return false
    }

    override fun onDetailSubjectClicked(semester: SemesterTotalCreditResponse) {
        semester.id?.let {
            SubjectListFragment.newInstance(it).apply { show(supportFragmentManager, SubjectListFragment.TAG) }
        }
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