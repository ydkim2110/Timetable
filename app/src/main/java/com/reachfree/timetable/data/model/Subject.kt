package com.reachfree.timetable.data.model

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.reachfree.timetable.R
import com.reachfree.timetable.data.response.GradeListResponse
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
    @ColumnInfo(name = "credit") var credit: Int = 3,
    @ColumnInfo(name = "grade") var grade: String = "",
    @ColumnInfo(name = "type") var type: Int = SubjectType.MANDATORY.ordinal,
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

data class GradeListGroup(
    var key: String = "",
    var subjectList: ArrayList<GradeListResponse> = ArrayList()
)

enum class GradeCreditType(
    @StringRes val stringRes: Int
)
{
    CREDIT_4_3(R.string.credit_4_3),
    CREDIT_4_5(R.string.credit_4_5)
}

enum class GradeType(
    val title: String,
    val point43: Float? = null,
    val point45: Float? = null
) {
    A_PLUS("A+", 4.3F, 4.5F),
    A_ZERO("A", 4.0F, 4.0F),
    A_MINUS("A-", 3.7F, null),
    B_PLUS("B+", 3.3F, 3.5F),
    B_ZERO("B", 3.0F, 3.0F),
    B_MINUS("B-", 2.7F, null),
    C_PLUS("C+", 2.3F, 2.5F),
    C_ZERO("C", 2.0F, 2.0F),
    C_MINUS("C-", 1.7F, null),
    D_PLUS("D+", 1.3F, 1.5F),
    D_ZERO("D", 1.0F, 1.0F),
    D_MINUS("D-", 0.7F, null),
    PASS("P", 0F, 0F),
    FAIL("F", 0F, 0F),
    DELETE("취소");

    companion object {
        fun calculateAverageGradeBy43(subjects: List<Subject>): Float {
            val denominator = subjects.filter { it.grade != PASS.title }.filter { it.grade.isNotBlank() }.sumBy { it.credit }

            if (denominator != 0) {
                var totalCredit = 0F
                subjects.forEach { subject ->
                    if (subject.grade.isNotBlank() && subject.grade != PASS.title) {
                        val grade = values().find { it.title == subject.grade }
                        totalCredit += subject.credit * grade?.point43!!
                    }
                }
                return totalCredit/denominator
            }
            return 0f
        }
        fun calculateAverageGradeBy43GradeListResponse(subjects: List<GradeListResponse>): Float {
            val denominator = subjects.filter { it.grade != PASS.title }.filter { it.grade.isNotBlank() }.sumBy { it.credit }

            if (denominator != 0) {
                var totalCredit = 0F
                subjects.forEach { subject ->
                    if (subject.grade.isNotBlank() && subject.grade != PASS.title) {
                        val grade = values().find { it.title == subject.grade }
                        totalCredit += subject.credit * grade?.point43!!
                    }
                }
                return totalCredit/denominator
            }
            return 0f
        }
        fun calculateAverageGradeBy45(subjects: List<Subject>): Float {
            val denominator = subjects.filter { it.grade != PASS.title }.filter { it.grade.isNotBlank() }.sumBy { it.credit }
            if (denominator != 0) {
                var totalCredit = 0F
                subjects.forEach { subject ->
                    if (subject.grade.isNotBlank() && subject.grade != PASS.title) {
                        val grade = values().find { it.title == subject.grade }
                        totalCredit += subject.credit * grade?.point45!!
                    }
                }
                return totalCredit/denominator
            }
            return 0f
        }
        fun calculateAverageGradeBy45GradeListResponse(subjects: List<GradeListResponse>): Float {
            val denominator = subjects.filter { it.grade != PASS.title }.filter { it.grade.isNotBlank() }.sumBy { it.credit }
            if (denominator != 0) {
                var totalCredit = 0F
                subjects.forEach { subject ->
                    if (subject.grade.isNotBlank() && subject.grade != PASS.title) {
                        val grade = values().find { it.title == subject.grade }
                        totalCredit += subject.credit * grade?.point45!!
                    }
                }
                return totalCredit/denominator
            }
            return 0f
        }
    }
}

data class GradeTypeModel(
    var gradeType: GradeType,
    var isSelected: Boolean = false
)

enum class GraduationCreditType {
    GRADUATION,
    MANDATORY,
    ELECTIVE
}

enum class SubjectType(
    @StringRes val stringRes: Int
) {
    MANDATORY(R.string.text_mandatory),
    ELECTIVE(R.string.text_elective),
    OTHER(R.string.text_other)
}