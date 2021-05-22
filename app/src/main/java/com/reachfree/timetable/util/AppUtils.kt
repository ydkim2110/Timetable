package com.reachfree.timetable.util

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.reachfree.timetable.R

object AppUtils {

    fun calculatePercentage(numerator: Int, denominator: Int): Int {
        return try {
            ((numerator.toDouble() / denominator.toDouble()) * 100).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun convertGradientTextView(textView: TextView) {
        val purple = ContextCompat.getColor(textView.context, R.color.start)
        val teal = ContextCompat.getColor(textView.context, R.color.end)

        val title = textView.text.toString()
        val spannable = title.toSpannable()
        spannable[0..title.length] = LinearGradientSpan(title, title, purple, teal)
        textView.text = spannable
    }
}