package com.reachfree.timetable.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.ItemSubjectBinding
import com.reachfree.timetable.extension.setOnSingleClickListener

class SubjectAdapter(
    private val semesters: List<Subject>
) : RecyclerView.Adapter<SubjectAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemSubjectBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subject: Subject) {
            with(binding) {
                txtTitle.text = subject.title

                var daysString = ""
                for (i in subject.days.indices) {
                    daysString += subject.days[i].day
                    if (i != subject.days.size -1) {
                        daysString += ", "
                    }
                }

                txtDays.text = daysString

                txtCredit.text = "${subject.credit}학점"

                root.setOnSingleClickListener {
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