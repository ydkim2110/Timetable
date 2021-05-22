package com.reachfree.timetable.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.ItemSubjectBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.DateUtils

class SemesterDetailSubjectAdapter(
    private val semesters: List<Subject>
) : RecyclerView.Adapter<SemesterDetailSubjectAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemSubjectBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceType")
        fun bind(subject: Subject) {
            with(binding) {
                txtTitle.text = subject.title

                var daysString = "일자: "
                for (i in subject.days.indices) {
                    daysString += DateUtils.convertDayToString(root.context, subject.days[i].day)
                    val startTime = DateUtils.updateHourAndMinute(subject.days[i].startHour, subject.days[i].startMinute)
                    val endTime = DateUtils.updateHourAndMinute(subject.days[i].endHour, subject.days[i].endMinute)
                    daysString += "($startTime~$endTime)"
                    if (i != subject.days.size -1) {
                        daysString += ", "
                    }
                }

                txtDays.text = daysString
                txtCredit.text = root.context.resources.getString(R.string.text_input_subject_credit, subject.credit)
                txtBuildingClassroom.text = root.context.resources.getString(R.string.text_input_subject_building_classroom,
                    subject.buildingName, subject.classroom)

                layoutSubject.setOnSingleClickListener {
                    onItemClickListener?.let { it(subject) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(semesters[position])
    }

    override fun getItemCount(): Int {
        return semesters.size
    }

    private var onItemClickListener: ((Subject) -> Unit)? = null

    fun setOnItemClickListener(listener: (Subject) -> Unit) {
        onItemClickListener = listener
    }

}