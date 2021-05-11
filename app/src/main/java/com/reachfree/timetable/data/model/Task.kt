package com.reachfree.timetable.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("subject_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "date") var date: Long? = 0L,
    @ColumnInfo(name = "type") var type: Int = TaskType.TASK.ordinal,
    @ColumnInfo(name = "subject_id") var subjectId: Long
)

enum class TaskType {
    TASK,
    TEST
}
