package com.reachfree.timetable.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.reachfree.timetable.R
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.LIST_CLICK_BROADCAST
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class TimetableListWidget : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("DEBUG: onReceive called!! ${intent.action}")
        if (intent.action == LIST_CLICK_BROADCAST) {
            val homeIntent = Intent(context, HomeActivity::class.java)
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(homeIntent)
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
            Timber.d("DEBUG: updateAppWidget called!!")
            val intent = Intent(context, TimetableListRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.timetable_collection_widget)
            val today = DateUtils.widgetTitleDateFormat.format(Calendar.getInstance().time.time)
            views.setTextViewText(R.id.txt_widget_today_title, today)
            views.setRemoteAdapter(R.id.list_view_timetable_widget, intent)
            views.setEmptyView(R.id.list_view_timetable_widget, R.id.txt_timetable_widget_empty)

            val appsIntent = Intent(context, TimetableListWidget::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0,
                appsIntent, FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.list_view_timetable_widget, pendingIntent)

            val onTaskClickPendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context, TimetableListWidget::class.java), 0)
            views.setPendingIntentTemplate(R.id.list_view_timetable_widget, onTaskClickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}