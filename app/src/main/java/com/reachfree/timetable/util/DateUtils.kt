package com.reachfree.timetable.util

import android.annotation.SuppressLint
import org.threeten.bp.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
object DateUtils {

    val testDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val defaultDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val semesterDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    val taskDateFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
    val yearMonthDateFormat = SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
    val yearDateFormat = SimpleDateFormat("yyyy년", Locale.getDefault())
    val monthDateFormat = SimpleDateFormat("MM월", Locale.getDefault())
    val dayDateFormat = SimpleDateFormat("dd일", Locale.getDefault())

    fun convertDateToLocalDate(date: Date): LocalDate {
        return Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun calculateStartOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
    }

    fun calculateEndOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MAX)
    }

    fun getTodayNumber(): Int {
        val cal = Calendar.getInstance()
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> 7
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            6 -> 5
            else -> 6
        }
    }

    fun calculateDay(day: Int): LocalDate {
        val cal = Calendar.getInstance()
        when (day) {
            1 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            }
            2 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            3 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
            }
            4 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
            }
            5 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
            }
            6 -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            }
            else -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
        }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val calDay = cal.get(Calendar.DAY_OF_MONTH)
        return LocalDate.of(year, month, calDay)
    }

    fun compareStartAndEndTime(startTime: String, endTime: String): Boolean {
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        val start = LocalTime.of(startHour, startMinute)
        val end = LocalTime.of(endHour, endMinute)

        return start.isAfter(end)
    }
}