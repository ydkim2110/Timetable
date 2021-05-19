package com.reachfree.timetable.widget

import android.app.PendingIntent
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
            ACTION_TASK_WIDGET_UPDATE -> {
                Timber.d("DEBUG : ACTION_TASK_WIDGET_UPDATE")
            }
            TASK_LIST_CLICK_BROADCAST -> {
                val homeIntent = Intent(context, HomeActivity::class.java).apply {
                    action = ACTION_TASK
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(homeIntent)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Timber.d("DEBUG: onUpdate called!!")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_task_widget)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Timber.d("DEBUG: updateAppWidget called!! $appWidgetId")
            val intent = Intent(context, TaskListRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.task_collection_widget)
            val today = DateUtils.widgetTitleDateFormat.format(Calendar.getInstance().time.time)

            views.setTextViewText(R.id.txt_task_widget_today_title, today)
            views.setOnClickPendingIntent(R.id.layout_header, getPendingIntent(context))

            views.setRemoteAdapter(R.id.list_view_task_widget, intent)
            views.setEmptyView(R.id.list_view_task_widget, R.id.txt_task_widget_empty)

            val onTaskClickPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, TaskListWidget::class.java),
                0
            )
            views.setPendingIntentTemplate(R.id.list_view_task_widget, onTaskClickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateWidgetListView(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, TaskListWidget::class.java))
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.list_view_task_widget)
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, TaskListWidget::class.java).apply {
                action = TASK_LIST_CLICK_BROADCAST
            }
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}
