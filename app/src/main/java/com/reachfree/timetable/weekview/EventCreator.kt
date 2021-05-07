package com.reachfree.timetable.weekview

import android.graphics.Color
import com.reachfree.timetable.util.timetable.TimetableEvent
import com.reachfree.timetable.util.timetable.TimetableData
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.Random

object EventCreator {

    private val random = Random()
    private val titles = listOf("Title", "Event", "Android", "Sport", "Yoga", "Shopping", "Meeting")
    private val subTitles = listOf("City Center", "@Home", "urgent", "New York", null)
    private val weekDays = DayOfWeek.values()
            // Filter the weekend
            .filter { it != DayOfWeek.SATURDAY }
            .filter { it != DayOfWeek.SUNDAY }

    private const val minEventLength = 30
    private const val maxEventLength = 90

    val weekData: TimetableData by lazy {
        TimetableData().apply {
            var startTime: LocalTime
            weekDays
                    .map { dayOfWeek ->
                        startTime = LocalTime.of(8 + random.nextInt(1), random.nextInt(60))
                        while (startTime.isBefore(LocalTime.of(15, 0))) {
                            val endTime = startTime.plusMinutes(minEventLength + random.nextInt(maxEventLength - minEventLength).toLong())
                            this.add(createSampleEntry(dayOfWeek, startTime, endTime))
                            startTime = endTime.plusMinutes(5 + random.nextInt(95).toLong())
                        }
                    }
        }
    }

    fun createRandomEvent(): TimetableEvent.Single {
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        val endTime = startTime.plusMinutes((30 + random.nextInt(60)).toLong())
        val day = weekDays.shuffled().first()
        return createSampleEntry(day, startTime, endTime)
    }

    private fun createSampleEntry(day: DayOfWeek, startTime: LocalTime, endTime: LocalTime): TimetableEvent.Single {
        val name = titles[random.nextInt(titles.size)]
        val subTitle = subTitles[random.nextInt(subTitles.size)]
        return TimetableEvent.Single(
                id = random.nextLong(),
                date = LocalDate.now().with(day),
                title = name,
                shortTitle = name,
                subTitle = subTitle,
                startTime = startTime,
                endTime = endTime,
                textColor = Color.WHITE,
                backgroundColor = randomColor()
        )
    }

    private fun randomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}