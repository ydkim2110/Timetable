package com.reachfree.timetable.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reachfree.timetable.util.ColorTag

@Entity(
    tableName = "part_time_jobs"
)
data class PartTimeJob(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "start_date") var startDate: Long = 0L,
    @ColumnInfo(name = "end_date") var endDate: Long = 0L,
    @ColumnInfo(name = "days") var days: List<Subject.Days>,
    @ColumnInfo(name = "background_color") var backgroundColor: Int = ColorTag.COLOR_9.resId,
)