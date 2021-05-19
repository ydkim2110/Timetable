package com.reachfree.timetable.extension

import android.content.Context
import android.widget.Toast
import com.reachfree.timetable.R

fun Context.getCornerRadius() = resources.getDimension(R.dimen.rounded_corner_radius_small)

fun Context.shortToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()