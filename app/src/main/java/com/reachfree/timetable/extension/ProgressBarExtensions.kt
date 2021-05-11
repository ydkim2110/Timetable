package com.reachfree.timetable.extension

import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar

fun ProgressBar.animateProgressBar(
    percentage: Int
) {
    ObjectAnimator.ofInt(this, "progress", 0, percentage).apply {
        duration = 1000
        interpolator = DecelerateInterpolator()
        start()
    }
}