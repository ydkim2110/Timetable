package com.reachfree.timetable.util.timetable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.reachfree.timetable.weekview.DayOfWeekUtil
import com.reachfree.timetable.weekview.dipToPixelF
import com.reachfree.timetable.weekview.dipToPixelI
import com.reachfree.timetable.weekview.toLocalString
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

internal class TimetableBackgroundView constructor(context: Context) : View(context) {

    private val accentPaint: Paint by lazy {
        Paint().apply { strokeWidth = DIVIDER_WIDTH_PX.toFloat() * 2 }
    }

    private val paintDivider: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = DIVIDER_WIDTH_PX.toFloat()
            color = DIVIDER_COLOR
        }
    }
    private val paintLabels: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            textSize = context.dipToPixelF(12f)
            textAlign = Paint.Align.CENTER
        }
    }

    private var isInScreenshotMode = false

    val topOffsetPx: Int = context.dipToPixelI(32f)
    private val leftOffset: Int = context.dipToPixelI(48f)

    private var drawCount = 0

    val days: MutableList<DayOfWeek> = DayOfWeekUtil.createList()
        .toMutableList()
        .apply {
            remove(DayOfWeek.SATURDAY)
            remove(DayOfWeek.SUNDAY)
        }

    var startTime: LocalTime = LocalTime.of(8, 0)
        private set
    private var endTime: LocalTime = LocalTime.of(19, 0)

    var scalingFactor = 1f
        /**
         * Updated the scaling factor and redraws the view.
         */
        set(value) {
            field = value
            requestLayout()
            // invalidate()
        }

    fun setAccentColor(color: Int) {
        accentPaint.color = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        canvas.drawHorizontalDividers()
        canvas.drawColumnsWithHeaders()

        if (!isInScreenshotMode && !isInEditMode) {
            drawNowIndicator(canvas)
        }
    }

    private fun drawNowIndicator(canvas: Canvas) {
        if (startTime.isBefore(LocalTime.now()) && endTime.isAfter(LocalTime.now())) {
            val nowOffset = Duration.between(startTime, LocalTime.now())

            val minutes = nowOffset.toMinutes()
            val y = topOffsetPx + context.dipToPixelF(minutes * scalingFactor)
            accentPaint.alpha = 200
            canvas.drawLine(0f, y, canvas.width.toFloat(), y, accentPaint)
        }
    }

    private fun Canvas.drawHorizontalDividers() {
        var localTime = startTime
        var last = LocalTime.MIN
        while (localTime.isBefore(endTime) && !last.isAfter(localTime)) {
            val offset = Duration.between(startTime, localTime)
            val y = topOffsetPx + context.dipToPixelF(offset.toMinutes() * scalingFactor)
            drawLine(0f, y, width.toFloat(), y, paintDivider)

            // final String timeString = localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
            val timeString = localTime.toLocalString()
            drawMultiLineText(this, timeString, context.dipToPixelF(25f), y + context.dipToPixelF(20f), paintLabels)

            last = localTime
            localTime = localTime.plusHours(1)
        }
        val offset = Duration.between(startTime, localTime)
        drawLine(0f, bottom.toFloat(), width.toFloat(), bottom.toFloat(), paintDivider)
    }

    private fun Canvas.drawColumnsWithHeaders() {
        val todayDay: DayOfWeek = LocalDate.now().dayOfWeek
        for ((column, dayId) in days.withIndex()) {
            drawLeftColumnDivider(column)
            drawWeekDayName(dayId, column)
            if (dayId == todayDay) {
                drawDayHighlight(column)
            }
        }
    }

    private fun Canvas.drawLeftColumnDivider(column: Int) {
        val left: Int = getColumnStart(column, false)
        drawLine(left.toFloat(), 0f, left.toFloat(), bottom.toFloat(), paintDivider)
    }

    private fun Canvas.drawDayHighlight(column: Int) {
        val left2: Int = getColumnStart(column, true)
        val right: Int = getColumnEnd(column, true)
        val rect = Rect(left2, 0, right, bottom)
        accentPaint.alpha = 32
        drawRect(rect, accentPaint)
    }

    private fun Canvas.drawWeekDayName(day: DayOfWeek, column: Int) {
        val shortName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val xLabel = (getColumnStart(column, false) + getColumnEnd(column, false)) / 2
        drawText(shortName, xLabel.toFloat(), topOffsetPx / 2 + paintLabels.descent(), paintLabels)
    }

    private fun drawMultiLineText(canvas: Canvas, text: String, initialX: Float, initialY: Float, paint: Paint) {
        var currentY = initialY
        text.split(" ")
            .dropLastWhile(String::isEmpty)
            .forEach {
                canvas.drawText(it, initialX, currentY, paint)
                currentY += (-paint.ascent() + paint.descent()).toInt()
            }
    }

    /**
     * Returns the offset (px!) from left for a given column.
     * First column is the first weekday.
     *
     * @param column starting to count at 0
     * @return offset in px
     */
    internal fun getColumnStart(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Int = width - leftOffset
        var offset: Int = leftOffset + contentWidth * column / days.size
        if (considerDivider) {
            offset += (DIVIDER_WIDTH_PX / 2)
        }
        return offset
    }

    internal fun getColumnEnd(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Int = width - leftOffset
        var offset: Int = leftOffset + contentWidth * (column + 1) / days.size
        if (considerDivider) {
            offset -= (DIVIDER_WIDTH_PX / 2)
        }
        return offset
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMeasure(widthMeasureSpec: Int, hms: Int) {
        val height = topOffsetPx + context.dipToPixelF(getDurationMinutes() * scalingFactor) + paddingBottom
        val heightMeasureSpec2 = MeasureSpec.makeMeasureSpec(height.roundToInt(), MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2)
    }

    fun setScreenshotMode(screenshotMode: Boolean) {
        isInScreenshotMode = screenshotMode
    }

    fun updateTimes(startTime: LocalTime, endTime: LocalTime) {
        if (startTime.isAfter(endTime)) {
            throw IllegalArgumentException("Start time $startTime must be before end time $endTime")
        }
        var timesHaveChanged = false

        Timber.d("DEBUG: startTime $startTime")

//        2021-05-08 12:53:03.779 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 00:20
//        2021-05-08 12:53:03.781 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 00:30
//        2021-05-08 12:53:03.782 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 00:40
//        2021-05-08 12:53:03.783 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 00:20
//        2021-05-08 12:53:03.784 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 00:30
//        2021-05-08 12:53:03.784 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 09:30
//        2021-05-08 12:53:03.785 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 09:30
//        2021-05-08 12:53:03.785 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 10:10
//        2021-05-08 12:53:03.786 26316-26316/com.reachfree.timetable D/TimetableBackgroundView: DEBUG: startTime 14:30

        if (startTime.isBefore(this.startTime)) {
            this.startTime = startTime.truncatedTo(ChronoUnit.HOURS)
            timesHaveChanged = true
        }

        if (endTime.isAfter(this.endTime)) {
            if (endTime.isBefore(LocalTime.of(23, 0))) {
                this.endTime = endTime.truncatedTo(ChronoUnit.HOURS).plusHours(1)
            } else {
                this.endTime = LocalTime.MAX
            }
            timesHaveChanged = true
        }

        if (this.startTime.isAfter(this.endTime)) throw IllegalArgumentException()

        if (timesHaveChanged) {
            requestLayout()
        }
    }

    private fun getDurationMinutes(): Long {
        return Duration.between(startTime, endTime).toMinutes()
    }

    companion object {
        /** Thickness of the grid.
         * Should be a multiple of 2 because of rounding. */
        private const val DIVIDER_WIDTH_PX: Int = 2
        private const val DIVIDER_COLOR = Color.LTGRAY
    }
}
