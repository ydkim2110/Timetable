package com.reachfree.timetable

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Timetable : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        Timber.plant(Timber.DebugTree())
    }

}