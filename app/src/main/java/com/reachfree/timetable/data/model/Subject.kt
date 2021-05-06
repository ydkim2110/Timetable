package com.reachfree.timetable.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
    foreignKeys = [ForeignKey(
        entity = Semester::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("semester"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Subject(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "days") var days: List<Days>,
    @ColumnInfo(name = "classroom") var classroom: String = "",
    @ColumnInfo(name = "building_name") var buildingName: String = "",
    @ColumnInfo(name = "professor_name") var professorName: String = "",
    @ColumnInfo(name = "book_name") var book_name: String = "",
    @ColumnInfo(name = "credit") var credit: String = "",
    @ColumnInfo(name = "semester") var semester: String
) {

    data class Days(
        var day: Int,
        var startDate: Long,
        var endDate: Long
    )

}