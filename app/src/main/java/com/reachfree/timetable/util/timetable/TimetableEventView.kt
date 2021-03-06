package com.reachfree.timetable.util.timetable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.reachfree.timetable.util.TextHelper
import com.reachfree.timetable.extension.dipToPixelF
import com.reachfree.timetable.extension.dipToPixelI
import com.reachfree.timetable.util.DateUtils
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

class TimetableEventView(
    context: Context,
    val event: TimetableEvent.Single,
    val config: TimetableEventConfig,
    var scalingFactor: Float = 1f
) : View(context) {

    private val CORNER_RADIUS_PX = context.dipToPixelF(2f)

    private val titlePaint: Paint by lazy { Paint().apply { isAntiAlias = true } }
    private val buildingPaint: Paint by lazy { Paint().apply { isAntiAlias = true } }
    private val classroomPaint: Paint by lazy { Paint().apply { isAntiAlias = true } }

    private val subjectName: String by lazy { if (config.useShortNames) event.shortTitle else event.title }
    private val building: String by lazy { event.building ?: "" }
    private val classroom: String by lazy { event.classroom ?: "" }

    private val titleBounds: Rect = Rect()
    private val buildingBounds: Rect = Rect()
    private val classRoomBounds: Rect = Rect()

    private val weightSum: Int
    private val weightStartTime: Int
    private val weightUpperText: Int
    private val weightTitle = 3
    private val weightSubTitle: Int
    private val weightLowerText: Int
    private val weightEndTime: Int

    init {
        val padding = this.context.dipToPixelI(2f)
        setPadding(padding, padding, padding, padding)

        setBackgroundResource(event.backgroundColor)


//        background = PaintDrawable().apply {
//            setCornerRadius(CORNER_RADIUS_PX)
//        }

        /** Calculate weights above & below. */
        weightStartTime = if (config.showTimeStart) 1 else 0
        weightUpperText = if (config.showUpperText) 1 else 0
        weightSubTitle = if (config.showSubtitle) 1 else 0
        weightLowerText = if (config.showLowerText) 1 else 0
        weightEndTime = if (config.showTimeEnd) 1 else 0

        weightSum =
            weightStartTime + weightUpperText + weightSubTitle + weightLowerText + weightEndTime + weightTitle

        titlePaint.color = event.textColor
        buildingPaint.color = event.textColor
        classroomPaint.color = event.textColor
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // ??????
        val maxTextSize = TextHelper.fitText(
            subjectName,
            titlePaint.textSize * 3,
            width - (paddingLeft + paddingRight),
            height / 4
        )
        titlePaint.textSize = maxTextSize
        titlePaint.getTextBounds(subjectName, 0, subjectName.length, titleBounds)
        var weight = weightStartTime + weightUpperText
        if (weight == 0) {
            weight++
        }
        val subjectY = getY(weight, weightTitle, titleBounds)
        canvas.drawText(
            subjectName,
            (width / 2 - titleBounds.centerX()).toFloat(),
            subjectY.toFloat(),
            titlePaint
        )

        titlePaint.textSize = TextHelper.fitText(
            "123456", maxTextSize, width / 2,
            getY(position = 1, bounds = titleBounds) - getY(position = 0, bounds = titleBounds)
        )

        val buildingMaxTextSize = TextHelper.fitText(
            building,
            titlePaint.textSize * 1.2f,
            width - (paddingLeft + paddingRight + 24),
            height / 6
        )
        buildingPaint.textSize = buildingMaxTextSize
        buildingPaint.getTextBounds(building, 0, building.length, buildingBounds)
        canvas.drawText(
            building,
            (width / 2 - buildingBounds.centerX()).toFloat(),
            subjectY - titleBounds.height().toFloat()-8f,
            buildingPaint
        )

        val classroomMaxTextSize = TextHelper.fitText(
            classroom,
            titlePaint.textSize * 1.2f,
            width - (paddingLeft + paddingRight + 24),
            height / 6
        )
        classroomPaint.textSize = classroomMaxTextSize
        classroomPaint.getTextBounds(classroom, 0, classroom.length, classRoomBounds)
        canvas.drawText(
            classroom,
            (width / 2 - classRoomBounds.centerX()).toFloat(),
            subjectY + classRoomBounds.height().toFloat()+8f,
            classroomPaint
        )


        // ????????????
        if (config.showTimeStart) {
            val localtime = event.startTime
            val cal = Calendar.getInstance()
            cal.clear()
            cal.set(0, 0, 0, localtime.hour, localtime.minute, localtime.second)

            val startText = DateUtils.shortHourMinuteDateFormat.format(cal.time.time)
            titlePaint.getTextBounds(startText, 0, startText.length, titleBounds)
            canvas.drawText(
                startText,
                (titleBounds.left + paddingLeft).toFloat(),
                (titleBounds.height() + paddingTop).toFloat(),
                titlePaint
            )
        }

        // ????????????
        if (config.showTimeEnd) {
            val localtime = event.endTime
            val cal = Calendar.getInstance()
            cal.clear()
            cal.set(0, 0, 0, localtime.hour, localtime.minute, localtime.second)

            val endText = DateUtils.shortHourMinuteDateFormat.format(cal.time.time)
            titlePaint.getTextBounds(endText, 0, endText.length, titleBounds)
            canvas.drawText(
                endText,
                (width - (titleBounds.right + paddingRight)).toFloat(),
                (height - paddingBottom).toFloat(),
                titlePaint
            )
        }
    }

    private fun getY(position: Int, weight: Int = 1, bounds: Rect): Int {
        val content = height - (paddingTop + paddingBottom)
        val y = (content * (position + 0.7f * weight) / weightSum) + paddingTop
        return y.roundToInt() - bounds.centerY()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredHeightDp = event.duration.toMinutes() * scalingFactor
        val desiredHeightPx = context.dipToPixelI(desiredHeightDp)
        val resolvedHeight = resolveSize(desiredHeightPx, heightMeasureSpec)

        setMeasuredDimension(width, resolvedHeight)
    }
}