package com.reachfree.timetable.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.reachfree.timetable.R
import com.reachfree.timetable.data.repository.SemesterRepository
import com.reachfree.timetable.data.repository.SubjectRepository
import com.reachfree.timetable.data.repository.TaskRepository
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.extension.toMillis
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.TASK_LIST_CLICK_BROADCAST
import com.reachfree.timetable.util.TIMETABLE_LIST_CLICK_BROADCAST
import com.reachfree.timetable.util.timetable.TimetableEvent
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TaskListRemoteViewsService : RemoteViewsService() {

    @Inject
    lateinit var semesterRepository: SemesterRepository
    @Inject
    lateinit var subjectRepository: SubjectRepository
    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TaskListRemoteViewsFactory(this)
    }

    inner class TaskListRemoteViewsFactory(
        private val context: Context
    ) : RemoteViewsFactory {

        private val todayEventList = mutableListOf<CalendarTaskResponse>()

        override fun onCreate() {
        }

        override fun onDataSetChanged() {
            todayEventList.clear()
            val semester = semesterRepository.getSemesterForWidgetService(Calendar.getInstance().time.time)
            val subjects = subjectRepository.getAllSubjectsForWidgetService(semester.id!!)

            for ((index, value) in subjects.withIndex()) {
                val today = DateUtils.calculateStartOfDay(LocalDate.now()).toMillis()!!
                val tasks = taskRepository.getAllTaskBySubjectForWidgetService(value.id!!, today)

                for (j in tasks.indices) {
                    val task = CalendarTaskResponse(
                        id = tasks[j].id,
                        semesterTitle = tasks[j].semesterTitle,
                        title = tasks[j].title,
                        description = tasks[j].description,
                        date = tasks[j].date,
                        type = tasks[j].type,
                        subjectId = tasks[j].subjectId,
                        backgroundColor = tasks[j].backgroundColor
                    )
                    todayEventList.add(task)
                    todayEventList.sortBy { it.date }
                }
            }
        }

        override fun onDestroy() {

        }

        override fun getCount(): Int {
            return todayEventList.size
        }

        override fun getViewAt(position: Int): RemoteViews? {
            if (!todayEventList.isNullOrEmpty()) {
                val task = todayEventList[position]
                val title = "${task.semesterTitle} - ${task.title}"
                val date = DateUtils.dayDateFormat.format(task.date)
                val time = DateUtils.taskDateFormat.format(task.date)

                val remoteViews = RemoteViews(context.packageName, R.layout.task_widget)
                remoteViews.setTextViewText(R.id.txt_task_widget_title, title)
                remoteViews.setTextViewText(R.id.txt_task_widget_date, date)
                remoteViews.setTextViewText(R.id.txt_task_widget_time, time)
                remoteViews.setInt(R.id.task_background_color, "setBackgroundResource", task.backgroundColor)

                val appsIntent = Intent(context, TaskListWidget::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0,
                    appsIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                remoteViews.setOnClickPendingIntent(R.id.layout_header, pendingIntent)

                val fillInIntent = Intent()
                    .putExtra("TITLE", "title")
                    .setAction(TASK_LIST_CLICK_BROADCAST)

                remoteViews.setOnClickFillInIntent(R.id.task_widget_layout, fillInIntent)

                return remoteViews
            }
            return null
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