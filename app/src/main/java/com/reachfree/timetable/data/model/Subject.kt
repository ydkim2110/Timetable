package com.reachfree.timetable.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.reachfree.timetable.util.ColorTag

@Entity(
    tableName = "subjects",
    foreignKeys = [ForeignKey(
        entity = Semester::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("semester_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Subject(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "days") var days: List<Days>,
    @ColumnInfo(name = "classroom") var classroom: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "building_name") var buildingName: String = "",
    @ColumnInfo(name = "professor_name") var professorName: String = "",
    @ColumnInfo(name = "book_name") var book_name: String = "",
    @ColumnInfo(name = "credit") var credit: Int = 0,
    @ColumnInfo(name = "type") var type: Int = 0,
    @ColumnInfo(name = "background_color") var backgroundColor: Int = ColorTag.COLOR_1.resId,
    @ColumnInfo(name = "semester_id") var semesterId: Long
) {

    data class Days(
        var day: Int,
        var startHour: Int,
        var startMinute: Int,
        var endHour: Int,
        var endMinute: Int
    )

}