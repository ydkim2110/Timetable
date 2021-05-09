package com.reachfree.timetable.extension

import android.view.View
import com.reachfree.timetable.util.OnSingleClickListener

fun View.setOnSingleClickListener(action: (View) -> Unit) {
    val onClick = OnSingleClickListener {
        action(it)
    }
    setOnClickListener(onClick)
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}