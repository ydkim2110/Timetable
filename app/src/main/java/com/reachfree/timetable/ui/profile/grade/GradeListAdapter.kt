package com.reachfree.timetable.ui.profile.grade

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.SubjectType
import com.reachfree.timetable.data.response.GradeListResponse
import com.reachfree.timetable.databinding.ItemGradeListBinding

class GradeListAdapter : ListAdapter<GradeListResponse, GradeListAdapter.MyViewHolder>(DiffUtils()) {
    inner class MyViewHolder(
        private val binding: ItemGradeListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gradeListResponse: GradeListResponse) {
            with(binding) {
                txtSubjectTitle.text = gradeListResponse.title
                txtSubjectType.text = root.context.getString(SubjectType.values()[gradeListResponse.type].stringRes)
                txtSubjectCredit.text = root.context.getString(R.string.text_input_subject_credit, gradeListResponse.credit)

                if (gradeListResponse.grade.isBlank()) {
                    btnGrade.text = root.context.getString(R.string.text_not_registered)
                } else {
                    btnGrade.text = gradeListResponse.grade
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemGradeListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<GradeListResponse>() {
        override fun areItemsTheSame(
            oldItem: GradeListResponse,
            newItem: GradeListResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GradeListResponse,
            newItem: GradeListResponse
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

}