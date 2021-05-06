package com.reachfree.timetable.weekview

import android.graphics.Paint
import android.graphics.Rect

object TextHelper {

    fun fitText(
        text: String,
        maxTextSize: Float,
        maxWidth: Int,
        maxHeight: Int
    ): Float {

        var hi = maxTextSize
        var lo = 15f
        val threshold = 0.5f

        val paint = Paint()
        val bounds = Rect()

        while (hi - lo > threshold) {
            val size = (hi + lo) / 2
            paint.textSize = size
            paint.getTextBounds(text, 0, text.length, bounds)

            if (bounds.width() >= maxWidth || bounds.height() >= maxHeight) {
                hi = size
            } else {
                lo = size
            }
        }

        return lo
    }

}