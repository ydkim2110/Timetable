package com.reachfree.timetable.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reachfree.timetable.R
import com.reachfree.timetable.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeActivity.start(this)
        finish()
    }
}