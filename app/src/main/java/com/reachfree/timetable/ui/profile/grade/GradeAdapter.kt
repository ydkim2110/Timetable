package com.reachfree.timetable.ui.profile.grade

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.SubjectType
import com.reachfree.timetable.databinding.ItemGradeBinding
import com.reachfree.timetable.extension.setOnSingleClickListener

class GradeAdapter(
    private val subjects: List<Subject>
) : RecyclerView.Adapter<GradeAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemGradeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subject: Subject) {
            with(binding) {
                txtSubjectTitle.text = subject.title
                txtSubjectType.text = root.context.getString(SubjectType.values()[subject.type].stringRes)
                txtSubjectCredit.text = root.context.getString(R.string.text_input_subject_credit, subject.credit)

                if (subject.grade.isBlank()) {
                    btnGrade.text = root.context.getString(R.string.text_not_registered)
                } else {
                    btnGrade.text = subject.grade
                }

                btnGrade.setOnSingleClickListener {
                    onItemClickListener?.let { it(subject) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemGradeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    private var onItemClickListener: ((Subject) -> Unit)? = null

    fun setOnItemClickListener(listener: (Subject) -> Unit) {
        onItemClickListener = listener
    }

}