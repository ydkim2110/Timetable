package com.reachfree.timetable.weekview.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.reachfree.timetable.R
import com.reachfree.timetable.weekview.DayOfWeekUtil
import com.reachfree.timetable.weekview.data.Event
import com.reachfree.timetable.weekview.data.EventConfig
import com.reachfree.timetable.weekview.data.WeekData
import com.reachfree.timetable.weekview.data.WeekViewConfig
import com.reachfree.timetable.weekview.dipToPixelF
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class WeekView(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {

    private val backgroundView: WeekBackgroundView
    private val overlapsWith = ArrayList<EventView>()

    private var isInScreenshotMode = false
    private var layoutCount = 0

    private var clickListener: ((view: EventView) -> Unit)? = null
    private var contextMenuListener: OnCreateContextMenuListener? = null
    private var eventTransitionName: String? = null

    private val accentColor: Int

    private val scaleGestureDetector: ScaleGestureDetector
    private val weekViewConfig: WeekViewConfig

    var eventConfig = EventConfig()

    init {
        val arr = context.obtainStyledAttributes(attributeSet, R.styleable.WeekView)
        accentColor = arr.getColor(R.styleable.WeekView_accent_color, Color.BLUE)
        arr.recycle() // Do this when done.

        val prefs = context.getSharedPreferences("ts_week_view", Context.MODE_PRIVATE)
        weekViewConfig = WeekViewConfig(prefs)

        backgroundView = WeekBackgroundView(context)
        backgroundView.setAccentColor(accentColor)
        backgroundView.scalingFactor = weekViewConfig.scalingFactor

        addView(backgroundView)

        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val factor = weekViewConfig.scalingFactor * detector.scaleFactor
            // Don't let the object get too small or too large.
            val scaleFactor = max(0.75f, min(factor, 3.0f))
            weekViewConfig.scalingFactor = scaleFactor
            backgroundView.scalingFactor = scaleFactor
            Log.d(TAG, "Scale factor is $scaleFactor")
            invalidate()
            requestLayout()
            return true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)
        return scaleGestureDetector.onTouchEvent(event)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setEventTransitionName(transitionName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.eventTransitionName = transitionName
        }
        for (childId in 0 until childCount) {
            val child: View = getChildAt(childId)
            if (child is EventView) {
                child.setTransitionName(transitionName)
            }
        }
    }

    fun setLessonClickListener(clickListener: (view: EventView) -> Unit) {
        this.clickListener = clickListener
        for (childIndex in 0 until childCount) {
            val view: View = getChildAt(childIndex)
            if (view is EventView) {
                view.setOnClickListener {
                    clickListener.invoke(view)
                }
            }
        }
    }

    override fun setOnCreateContextMenuListener(contextMenuListener: OnCreateContextMenuListener?) {
        this.contextMenuListener = contextMenuListener
        for (childIndex in 0 until childCount) {
            val view: View = getChildAt(childIndex)
            if (view is EventView) {
                view.setOnCreateContextMenuListener(contextMenuListener)
            }
        }
    }

    fun addEvents(weekData: WeekData) {
        Log.d(TAG, "Adding ${weekData.getSingleEvents().size} weekData to week view")

        backgroundView.updateTimes(weekData.earliestStart, weekData.latestEnd)

        for (event in weekData.getSingleEvents()) {
            addEvent(event)
        }

        // TODO: support multi day weekData
        Log.d(TAG, " - Done adding weekData to timetable")
    }

    fun addEvent(event: Event.Single) {
        // enable weekend if not enabled yet
        Log.d("DEBUG", "event.date.dayOfWeek ${event.date.dayOfWeek}")
        when (event.date.dayOfWeek) {
            DayOfWeek.SATURDAY -> {
                Log.i(TAG, "Enabling Saturday")
                if (!backgroundView.days.contains(DayOfWeek.SATURDAY)) {
                    backgroundView.days.add(DayOfWeek.SATURDAY)
                }
            }
            DayOfWeek.SUNDAY -> {
                Log.i(TAG, "Enabling Saturday")
                if (!backgroundView.days.contains(DayOfWeek.SATURDAY)) {
                    backgroundView.days.add(DayOfWeek.SATURDAY)
                }
                Log.i(TAG, "Enabling Sunday")
                if (!backgroundView.days.contains(DayOfWeek.SUNDAY)) {
                    backgroundView.days.add(DayOfWeek.SUNDAY)
                }
            }
            else -> {
                // nothing to do, just add the event
            }
        }

        val lv = EventView(context, event, eventConfig, weekViewConfig.scalingFactor)
        Log.d("DEBUG", "startTime ${event.startTime}")
        Log.d("DEBUG", "endTime ${event.endTime}")
        backgroundView.updateTimes(event.startTime, event.endTime)

        // mark active event
        val now = LocalTime.now()
        if (LocalDate.now().dayOfWeek == event.date.dayOfWeek && // this day
            event.startTime < now && event.endTime > now) {
            lv.animation = com.reachfree.timetable.weekview.Animation.createBlinkAnimation()
        }

        lv.setOnClickListener { clickListener?.invoke(lv) }
        lv.setOnCreateContextMenuListener(contextMenuListener)
        lv.transitionName = eventTransitionName

        addView(lv)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        Log.v(TAG, "Measuring ($widthSize x $heightSize)")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.v(TAG, "Laying out timetable for the ${++layoutCount} time.")
        Log.v(TAG, "l: $l, t: $t, r: $r, b: $b")
        super.onLayout(true, l, t, r, b)
        if (isInScreenshotMode) {
            backgroundView.setScreenshotMode(true)
        }

        val saturdayEnabled = backgroundView.days.contains(DayOfWeek.SATURDAY)
        val sundayEnabled = backgroundView.days.contains(DayOfWeek.SUNDAY)

        for (childIndex in 0 until childCount) {
            Log.i(TAG, "child $childIndex of $childCount")
            val eventView: EventView
            val childView = getChildAt(childIndex)
            if (childView is EventView) {
                eventView = childView
            } else {
                continue
            }

            // FIXME   lessonView.setShortNameEnabled(isShortNameEnabled);
            val column: Int = DayOfWeekUtil.mapDayToColumn(eventView.event.date.dayOfWeek, saturdayEnabled, sundayEnabled)
            Log.d("DEBUG: ", "column: $column")
            if (column < 0) {
                // should not be necessary as wrong days get filtered before.
                Log.v(TAG, "Removing view for event $eventView")
                childView.setVisibility(View.GONE)
                removeView(childView)
                continue
            }
            var left: Int = backgroundView.getColumnStart(column, true)
            val right: Int = backgroundView.getColumnEnd(column, true)

            overlapsWith.clear()
            for (j in 0 until childIndex) {
                val v2 = getChildAt(j)
                // get next LessonView
                if (v2 is EventView) {
                    // check for overlap
                    if (v2.event.date != eventView.event.date) {
                        continue // days differ, overlap not possible
                    } else if (overlaps(eventView, v2)) {
                        overlapsWith.add(v2)
                    }
                }
            }

            if (overlapsWith.size > 0) {
                val width = (right - left) / (overlapsWith.size + 1)
                for ((index, view) in overlapsWith.withIndex()) {
                    val left2 = left + index * width
                    view.layout(left2, view.top, left2 + width, view.bottom)
                }
                left = right - width
            }

            eventView.scalingFactor = weekViewConfig.scalingFactor
            val startTime = backgroundView.startTime
            val lessonStart = eventView.event.startTime
            val offset = Duration.between(startTime, lessonStart)

            val yOffset = offset.toMinutes() * weekViewConfig.scalingFactor
            val top = context.dipToPixelF(yOffset) + backgroundView.topOffsetPx

            val bottom = top + eventView.measuredHeight
            eventView.layout(left, top.roundToInt(), right, bottom.roundToInt())
        }
    }

    fun setScreenshotModeEnabled(enabled: Boolean) {
        isInScreenshotMode = enabled
    }

    private fun overlaps(left: EventView, right: EventView): Boolean {
        val rightStartsAfterLeftStarts = right.event.startTime >= left.event.startTime
        val rightStartsBeforeLeftEnds = right.event.startTime < left.event.endTime
        val lessonStartsWithing = rightStartsAfterLeftStarts && rightStartsBeforeLeftEnds

        val leftStartsBeforeRightEnds = left.event.startTime < right.event.endTime
        val rightEndsBeforeOrWithLeftEnds = right.event.endTime <= left.event.endTime
        val lessonEndsWithing = leftStartsBeforeRightEnds && rightEndsBeforeOrWithLeftEnds

        val leftStartsAfterRightStarts = left.event.startTime > right.event.startTime
        val rightEndsAfterLeftEnds = right.event.endTime > left.event.endTime
        val lessonWithin = leftStartsAfterRightStarts && rightEndsAfterLeftEnds

        return lessonStartsWithing || lessonEndsWithing || lessonWithin
    }

    companion object {
        private const val TAG = "WeekView"
    }
}
