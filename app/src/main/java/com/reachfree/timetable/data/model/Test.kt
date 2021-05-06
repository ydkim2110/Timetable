package com.reachfree.timetable.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tests",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("subject"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Test(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "date") var date: Long? = 0L,
    @ColumnInfo(name = "subject") var subject: String = ""
)
