package com.reachfree.timetable.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getBroadcast
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.reachfree.timetable.R
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.ACTION_TASK
import com.reachfree.timetable.util.ACTION_TASK_WIDGET_UPDATE
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.TASK_LIST_CLICK_BROADCAST
import timber.log.Timber
import java.util.*

class TaskListWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("DEBUG : onReceive ${intent.action}")
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                updateWidgetListView(context)
            }
            TASK_LIST_CLICK_BROADCAST -> {
                val homeIntent = Intent(context, HomeActivity::class.java).apply {
                    action = ACTION_TASK
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(homeIntent)
            }
            ACTION_TASK_WIDGET_UPDATE -> {
                updateWidgetListView(context)
            }
            Intent.ACTION_DATE_CHANGED -> {
                updateWidgetListView(context)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_task_widget)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        val intent = Intent(context, TaskListWidget::class.java)
        intent.action = ACTION_TASK_WIDGET_UPDATE
        val pendingIntent = getBroadcast(context, 0, intent, FLAG_UPDATE_CURRENT)

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 1)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC, cal.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    override fun onDisabled(context: Context) {
        val intent = Intent(context, TaskListWidget::class.java)
        intent.action = ACTION_TASK_WIDGET_UPDATE
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getBroadcast(context, 0, intent, 0))
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, TaskListRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.task_collection_widget)
            val today = DateUtils.widgetTitleDateFormat.format(Calendar.getInstance().time.time)

            views.setTextViewText(R.id.txt_task_widget_today_title, today)
            views.setOnClickPendingIntent(R.id.layout_header, getPendingIntent(context))

            views.setImageViewResource(R.id.img_refresh, R.drawable.ic_refresh)

            val refreshIntent = Intent(context, TaskListWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            views.setOnClickPendingIntent(R.id.img_refresh,
                getBroadcast(context, 0, refreshIntent, FLAG_UPDATE_CURRENT)
            )

            views.setRemoteAdapter(R.id.list_view_task_widget, intent)
            views.setEmptyView(R.id.list_view_task_widget, R.id.txt_task_widget_empty)

            val pendingIntent = getBroadcast(context, 0, Intent(context, TaskListWidget::class.java), 0)
            views.setPendingIntentTemplate(R.id.list_view_task_widget, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateWidgetListView(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, TaskListWidget::class.java))
            for (appWidgetId in ids) {
                appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.list_view_task_widget)
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, TaskListWidget::class.java).apply {
                action = TASK_LIST_CLICK_BROADCAST
            }
            return getBroadcast(context, 0, intent, FLAG_UPDATE_CURRENT)
        }
    }
}
