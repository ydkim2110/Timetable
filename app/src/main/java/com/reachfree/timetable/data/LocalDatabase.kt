package com.reachfree.timetable.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reachfree.timetable.data.dao.SemesterDao
import com.reachfree.timetable.data.dao.SubjectDao
import com.reachfree.timetable.data.dao.TaskDao
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.Task

@Database(
    entities = [Semester::class, Subject::class, Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun semesterDao(): SemesterDao
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao

}