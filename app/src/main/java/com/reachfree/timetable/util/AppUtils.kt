package com.reachfree.timetable.util

object AppUtils {

    fun calculatePercentage(numerator: Int, denominator: Int): Int {
        return try {
            ((numerator.toDouble() / denominator.toDouble()) * 100).toInt()
        } catch (e: Exception) {
            0
        }
    }

}