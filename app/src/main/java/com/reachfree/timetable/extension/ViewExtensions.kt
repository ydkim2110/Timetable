package com.reachfree.timetable.extension

import android.view.View
import com.reachfree.timetable.util.OnSingleClickListener

fun View.setOnSingleClickListener(action: (View) -> Unit) {
    val onClick = OnSingleClickListener {
        action(it)
    }
    setOnClickListener(onClick)
}
