package com.reachfree.timetable.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reachfree.timetable.data.dao.SemesterDao
import com.reachfree.timetable.data.dao.SubjectDao
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.model.Test

@Database(
    entities = [Semester::class, Subject::class, Task::class, Test::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun semesterDao(): SemesterDao
    abstract fun subjectDao(): SubjectDao

}