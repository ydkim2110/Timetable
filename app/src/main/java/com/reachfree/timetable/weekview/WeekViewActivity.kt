package com.reachfree.timetable.weekview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.reachfree.timetable.databinding.ActivityWeekViewBinding
import com.reachfree.timetable.weekview.data.Event
import com.reachfree.timetable.weekview.data.EventConfig
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class WeekViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeekViewBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeekViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val config = EventConfig(
//                showSubtitle = false,
//                showTimeEnd = false
        )
        binding.weekView.eventConfig = config

        val nowEvent = Event.Single(
                id = 1,
                date = LocalDate.now().minusDays(12),
                title = "거시경제학",
                shortTitle = "미시경제학",
                location = "연희관303호",
                startTime = LocalTime.of(9, 30, 0),
                endTime = LocalTime.of(11, 10, 0),
                backgroundColor = Color.RED,
                textColor = Color.WHITE
        )

        val tomorrowEvent = Event.Single(
            id = 1,
            date = LocalDate.now(),
            title = "수",
            shortTitle = "수",
            location = "대우관201호",
            startTime = LocalTime.of(9, 30, 0),
            endTime = LocalTime.of(11, 10, 0),
            backgroundColor = Color.BLUE,
            textColor = Color.WHITE
        )

        binding.weekView.addEvent(nowEvent)
        binding.weekView.addEvent(tomorrowEvent)

        binding.weekView.setOnTouchListener { v, event ->
            when (event.pointerCount) {
                1 -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
                2 -> {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            false
        }
    }
}