package com.reachfree.timetable.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Subject
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
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TimetableListRemoteViewsService : RemoteViewsService() {

    @Inject
    lateinit var semesterRepository: SemesterRepository
    @Inject
    lateinit var subjectRepository: SubjectRepository
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TimetableListRemoteViewsFactory(this)
    }

    inner class TimetableListRemoteViewsFactory(
        private val context: Context
    ) : RemoteViewsFactory {

        private val todayEventList = mutableListOf<TimetableEvent.Single>()

        override fun onCreate() {

        }

        override fun onDataSetChanged() {
            val semester = semesterRepository.getSemesterForWidgetService(Calendar.getInstance().time.time)
            val subjects = subjectRepository.getAllSubjectsForWidgetService(semester.id!!)
            Timber.d("DEBUG: onDataSetChanged $semester")
            Timber.d("DEBUG: onDataSetChanged $subjects")

            for ((index, value) in subjects.withIndex()) {
                for (i in value.days.indices) {
                    if (value.days[i].day == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                        val event = TimetableEvent.Single(
                            id = 1,
                            date = DateUtils.calculateDay(value.days[i].day),
                            title = value.title,
                            shortTitle = value.title,
                            classroom = value.classroom,
                            building = value.buildingName,
                            credit = value.credit,
                            startTime = LocalTime.of(value.days[i].startHour, value.days[i].startMinute),
                            endTime = LocalTime.of(value.days[i].endHour, value.days[i].endMinute),
                            backgroundColor = value.backgroundColor,
                            textColor = Color.WHITE
                        )
                        todayEventList.add(event)
                    }
                }
            }
        }

        override fun onDestroy() {

        }

        override fun getCount(): Int {
            Timber.d("DEBUG : getCount ${todayEventList.size}")
            return todayEventList.size
        }

        override fun getViewAt(position: Int): RemoteViews? {
            Timber.d("DEBUG : getViewAt called!!")
            if (!todayEventList.isNullOrEmpty()) {
                val subject = todayEventList[position]
                val title = subject.title
                val date = subject.date.toString()

                val remoteViews = RemoteViews(context.packageName, R.layout.timetable_widget)
                remoteViews.setTextViewText(R.id.txt_widget_title, title)
                remoteViews.setTextViewText(R.id.txt_widget_date, date)

                remoteViews.setOnClickPendingIntent(R.id.widget_layout, getPendingIntent(context))

                return remoteViews
            }
            val remoteViews = RemoteViews(context.packageName, R.layout.timetable_widget)
            remoteViews.setTextViewText(R.id.txt_widget_title, "ttitle")
            remoteViews.setTextViewText(R.id.txt_widget_date, "daste")

            remoteViews.setOnClickPendingIntent(R.id.widget_layout, getPendingIntent(context))

            return remoteViews
//            return null
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

    }

}


private fun getPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, HomeActivity::class.java)
    return PendingIntent.getActivity(context, 0, intent, 0)
}