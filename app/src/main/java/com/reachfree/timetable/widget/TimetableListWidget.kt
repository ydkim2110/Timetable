package com.reachfree.timetable.widget

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_DAY
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getBroadcast
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_DATE_CHANGED
import android.widget.RemoteViews
import com.reachfree.timetable.R
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.ACTION_AUTO_UPDATE_WIDGET
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.TIMETABLE_LIST_CLICK_BROADCAST
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class TimetableListWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("DEBUG: onReceive called!! ${intent.action}")
        when (intent.action) {
            TIMETABLE_LIST_CLICK_BROADCAST -> {
                val homeIntent = Intent(context, HomeActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(homeIntent)
            }
            ACTION_AUTO_UPDATE_WIDGET -> {
                Timber.d("DEBUG: update daily called!!")
                val man = AppWidgetManager.getInstance(context)
                val ids = man.getAppWidgetIds(ComponentName(context.packageName, TimetableListWidget::class.java.name))
                onUpdate(context, man, ids)
            }
            ACTION_DATE_CHANGED -> {
                Timber.d("DEBUG: date changed called!!")
                val man = AppWidgetManager.getInstance(context)
                val ids = man.getAppWidgetIds(ComponentName(context.packageName, TimetableListWidget::class.java.name))
                onUpdate(context, man, ids)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Timber.d("DEBUG: onUpdate called!!")
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_timetable_widget)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Timber.d("DEBUG: onEnabled called!!")
        val intent = Intent(ACTION_AUTO_UPDATE_WIDGET)
        val pendingIntent = getBroadcast(context, 0, intent, FLAG_UPDATE_CURRENT)

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 1)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC, cal.timeInMillis, INTERVAL_DAY, pendingIntent)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        val intent = Intent(ACTION_AUTO_UPDATE_WIDGET)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getBroadcast(context, 0, intent, 0))
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Timber.d("DEBUG: updateAppWidget called!! $appWidgetId")
            val intent = Intent(context, TimetableListRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.timetable_collection_widget)
            val today = DateUtils.widgetTitleDateFormat.format(Calendar.getInstance().time.time)

            views.setTextViewText(R.id.txt_widget_today_title, today)
            views.setOnClickPendingIntent(R.id.txt_widget_today_title, getPendingIntent(context))

            views.setRemoteAdapter(R.id.list_view_timetable_widget, intent)
            views.setEmptyView(R.id.list_view_timetable_widget, R.id.txt_timetable_widget_empty)

            val pendingIntent = getBroadcast(context, 0, Intent(context, TimetableListWidget::class.java), 0)
            views.setPendingIntentTemplate(R.id.list_view_timetable_widget, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }


        fun updateWidgetListView(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, TimetableListWidget::class.java))
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.list_view_timetable_widget)
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, TimetableListWidget::class.java).apply {
                action = TIMETABLE_LIST_CLICK_BROADCAST
            }
            return getBroadcast(context, 0, intent, FLAG_UPDATE_CURRENT)
        }
    }
}