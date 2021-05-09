package com.reachfree.timetable.util

import android.annotation.SuppressLint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
object DateUtils {

    val testDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val defaultDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val semesterDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    val yearMonthDateFormat = SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
    val yearDateFormat = SimpleDateFormat("yyyy년", Locale.getDefault())
    val monthDateFormat = SimpleDateFormat("MM월", Locale.getDefault())

    fun calculateStartOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
    }

    fun calculateEndOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MAX)
    }


    fun calculateDay(day: Int): LocalDate {
        val cal = Calendar.getInstance()
        when (day) {
            0 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            1 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
            }
            2 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
            }
            3 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
            }
            4 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            }
            5 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
            else -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            }
        }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val calDay = cal.get(Calendar.DAY_OF_MONTH)
        return LocalDate.of(year, month, calDay)
    }
}