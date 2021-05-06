package com.reachfree.timetable.weekview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.os.Build
import android.os.Debug
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.reachfree.timetable.weekview.TextHelper
import com.reachfree.timetable.weekview.data.Event
import com.reachfree.timetable.weekview.data.EventConfig
import com.reachfree.timetable.weekview.dipToPixelF
import com.reachfree.timetable.weekview.dipToPixelI
import com.reachfree.timetable.weekview.toLocalString
import kotlin.math.roundToInt

class EventView(
        context: Context,
        val event: Event.Single,
        val config: EventConfig,
        var scalingFactor: Float = 1f
) : View(context) {

    private val CORNER_RADIUS_PX = context.dipToPixelF(2f)

    private val textPaint: Paint by lazy { Paint().apply { isAntiAlias = true } }
    private val textPaint2: Paint by lazy { Paint().apply { isAntiAlias = true } }

    private val subjectName: String by lazy { if (config.useShortNames) event.shortTitle else event.title }
    private val location: String by lazy { event.location ?: "" }

    private val textBounds: Rect = Rect()

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

        background = PaintDrawable().apply {
            paint.color = event.backgroundColor
            setCornerRadius(CORNER_RADIUS_PX)
        }

        /** Calculate weights above & below. */
        weightStartTime = if (config.showTimeStart) 1 else 0
        weightUpperText = if (config.showUpperText) 1 else 0
        weightSubTitle = if (config.showSubtitle) 1 else 0
        weightLowerText = if (config.showLowerText) 1 else 0
        weightEndTime = if (config.showTimeEnd) 1 else 0

        weightSum = weightStartTime + weightUpperText + weightSubTitle + weightLowerText + weightEndTime + weightTitle

        textPaint.color = event.textColor
        textPaint2.color = event.textColor
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (Debug.isDebuggerConnected()) {
            for (i in 0..weightSum) {
                val content = height - (paddingTop + paddingBottom)
                val y = content * i / weightSum + paddingTop
                canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), textPaint)
            }
        }

        // 과목
        val maxTextSize = TextHelper.fitText(
            subjectName,
            textPaint.textSize * 3,
            width - (paddingLeft + paddingRight),
            height / 4
        )
        textPaint.textSize = maxTextSize
        textPaint.getTextBounds(subjectName, 0, subjectName.length, textBounds)
        var weight = weightStartTime + weightUpperText
        if (weight == 0) {
            weight++
        }
        val subjectY = getY(weight, weightTitle, textBounds)
        canvas.drawText(
            subjectName,
            (width / 2 - textBounds.centerX()).toFloat(),
            subjectY.toFloat(),
            textPaint
        )

        textPaint.textSize = TextHelper.fitText("123456", maxTextSize, width / 2,
                getY(position = 1, bounds = textBounds) - getY(position = 0, bounds = textBounds))

        // 위치
        val maxTextSize2 = TextHelper.fitText(
                location,
                textPaint2.textSize * 3,
                width - (paddingLeft + paddingRight),
                height / 4
        )
        textPaint2.textSize = maxTextSize2
        textPaint2.getTextBounds(location, 0, location.length, textBounds)
        var weight2 = weightStartTime + weightUpperText
        if (weight2 == 0) {
            weight2++
        }
        val subjectY2 = getY(weight2, weightTitle, textBounds)
        canvas.drawText(
            location,
            (width / 2 - textBounds.centerX()).toFloat(),
            subjectY + 30f,
            textPaint2
        )

        // 시작시간
        if (config.showTimeStart) {
            val startText = event.startTime.toLocalString()
            textPaint.getTextBounds(startText, 0, startText.length, textBounds)
            canvas.drawText(
                startText,
                (textBounds.left + paddingLeft).toFloat(),
                (textBounds.height() + paddingTop).toFloat(),
                    textPaint
            )
        }

        // 종료시간
        if (config.showTimeEnd) {
            val endText = event.endTime.toLocalString()
            textPaint.getTextBounds(endText, 0, endText.length, textBounds)
            canvas.drawText(
                endText,
                (width - (textBounds.right + paddingRight)).toFloat(),
                (height - paddingBottom).toFloat(),
                textPaint
            )
        }




    }

    private fun getY(position: Int, weight: Int = 1, bounds: Rect): Int {
        val content = height - (paddingTop + paddingBottom)
        val y = (content * (position + 0.5f * weight) / weightSum) + paddingTop
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