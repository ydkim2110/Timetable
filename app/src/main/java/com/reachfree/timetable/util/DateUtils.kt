package com.reachfree.timetable.util

import android.annotation.SuppressLint
import android.content.Context
import com.reachfree.timetable.R
import org.threeten.bp.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
object DateUtils {

    val testDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val defaultDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val semesterShortDateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
    val semesterDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    val widgetTitleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일, EEEE", Locale.getDefault())
    val taskDateFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
    val yearMonthDateFormat = SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
    val yearDateFormat = SimpleDateFormat("yyyy년", Locale.getDefault())
    val monthDateFormat = SimpleDateFormat("MM월", Locale.getDefault())
    val dayDateFormat = SimpleDateFormat("dd일", Locale.getDefault())

    fun updateHourAndMinute(h: Int, min: Int): String {
        return "${if (h < 10) "0$h" else h}:${if (min < 10) "0$min" else min}"
    }

    fun convertDateToLocalDate(date: Date): LocalDate {
        return Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun calculateStartOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
    }

    fun calculateEndOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MAX)
    }

    fun convertDayToString(context: Context, day: Int): String {
        return when (day) {
            1 -> {
                context.resources.getString(R.string.sunday_letter)
            }
            2 -> {
                context.resources.getString(R.string.monday_letter)
            }
            3 -> {
                context.resources.getString(R.string.tuesday_letter)
            }
            4 -> {
                context.resources.getString(R.string.wednesday_letter)
            }
            5 -> {
                context.resources.getString(R.string.thursday_letter)
            }
            6 -> {
                context.resources.getString(R.string.friday_letter)
            }
            else -> {
                context.resources.getString(R.string.saturday_letter)
            }
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