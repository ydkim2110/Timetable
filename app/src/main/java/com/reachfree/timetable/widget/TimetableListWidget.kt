package com.reachfree.timetable.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.reachfree.timetable.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TimetableListWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view_timetable_widget)

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
        ) {0
            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
//            views.setTextViewText(R.id.app, widgetText)

            // Instruct the widget manager to update the widget

            val intent = Intent(context, TimetableListRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.timetable_collection_widget)

            views.setRemoteAdapter(R.id.list_view_timetable_widget, intent)

            views.setEmptyView(R.id.list_view_timetable_widget, R.id.txt_timetable_widget_empty)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

