package com.reachfree.timetable.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reachfree.timetable.R
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.ui.setup.SetupActivity
import com.reachfree.timetable.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.getResisterSemester()) {
            HomeActivity.start(this)
        } else {
            SetupActivity.start(this)
        }
        finish()
    }
}