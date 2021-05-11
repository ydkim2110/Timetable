package com.reachfree.timetable.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.reachfree.timetable.R
import com.reachfree.timetable.data.repository.SemesterRepository
import com.reachfree.timetable.data.repository.SubjectRepository
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.timetable.TimetableEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TimetableWidget : AppWidgetProvider() {

    @Inject
    lateinit var subjectRepository: SubjectRepository

    @Inject
    lateinit var semesterRepository: SemesterRepository

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        coroutineScope.launch {
            semesterRepository.getSemesterByFlow(Calendar.getInstance().time.time)
                .collect { semester ->
                    semester.id?.let {
                        Timber.d("DEBUG: semester id: $it")
                        subjectRepository.getAllSubjectsByFlow(it).collect { subjects ->
                            if (!subjects.isNullOrEmpty()) {
                                Timber.d("DEBUG: subject size: ${subjects.size}")
                                val appWidgetManager = AppWidgetManager.getInstance(context)
                                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                                    ComponentName(
                                        context,
                                        TimetableWidget::class.java
                                    )
                                )

                                for (appWidgetId in appWidgetIds) {
                                    val todayEventList = mutableListOf<TimetableEvent.Single>()
                                    todayEventList.clear()

                                    for ((index, value) in subjects.withIndex()) {
                                        for (i in value.days.indices) {
                                            Timber.d("DEBUG: date is: ${value.days[i].day}/${Calendar.getInstance().get(Calendar.DAY_OF_WEEK)}")

                                            if (value.days[i].day == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                                                val event = TimetableEvent.Single(
                                                    id = 1,
                                                    date = DateUtils.calculateDay(value.days[i].day),
                                                    title = value.title,
                                                    shortTitle = value.title,
                                                    classroom = value.classroom,
                                                    building = value.buildingName,
                                                    credit = value.credit,
                                                    startTime = LocalTime.of(
                                                        value.days[i].startHour,
                                                        value.days[i].startMinute
                                                    ),
                                                    endTime = LocalTime.of(
                                                        value.days[i].endHour,
                                                        value.days[i].endMinute
                                                    ),
                                                    backgroundColor = value.backgroundColor,
                                                    textColor = Color.WHITE
                                                )
                                                todayEventList.add(event)
                                            }
                                        }
                                    }

                                    updateAppWidget(context, appWidgetManager, appWidgetId, todayEventList)
                                }
                            }

                        }
                    }
                }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        job.cancel()
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    todayEventList: MutableList<TimetableEvent.Single>? = null
) {
    Timber.d("DEBUG: todayEventList ${todayEventList?.size}")
    var title = ""
    var date = ""
    if (!todayEventList.isNullOrEmpty()) {
        title = todayEventList[0].title
        date = todayEventList[0].startTime.toString()
    } else {
        title = "수업 없음"
        date = "00:00"
    }
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.timetable_widget)
    views.setTextViewText(R.id.txt_widget_title, title)
    views.setTextViewText(R.id.txt_widget_date, date)

    views.setOnClickPendingIntent(R.id.widget_layout, getPendingIntent(context))

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}


private fun getPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, HomeActivity::class.java)
    return PendingIntent.getActivity(context, 0, intent, 0)
}