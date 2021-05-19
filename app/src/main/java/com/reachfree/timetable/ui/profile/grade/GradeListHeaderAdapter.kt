package com.reachfree.timetable.ui.profile.grade

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.data.model.GradeCreditType
import com.reachfree.timetable.data.model.GradeListGroup
import com.reachfree.timetable.data.model.GradeType
import com.reachfree.timetable.databinding.ItemGradeListHeaderBinding
import com.reachfree.timetable.util.SessionManager

class GradeListHeaderAdapter(
    private val sessionManager: SessionManager
) : ListAdapter<GradeListGroup, GradeListHeaderAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemGradeListHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(gradeListGroup: GradeListGroup) {
            with(binding) {
                txtTitle.text = gradeListGroup.key.split("#")[1]

                val averageCredit = if (sessionManager.getGradeCreditOption() == GradeCreditType.CREDIT_4_3.ordinal) {
                    GradeType.calculateAverageGradeBy43GradeListResponse(gradeListGroup.subjectList)
                } else {
                    GradeType.calculateAverageGradeBy45GradeListResponse(gradeListGroup.subjectList)
                }
                binding.txtTotalAverageCredit.text = String.format("%.2f", averageCredit)

                recyclerSubGradeList.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(root.context)
                    val gradeListAdapter = GradeListAdapter()
                    adapter = gradeListAdapter
                    gradeListAdapter.submitList(gradeListGroup.subjectList)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemGradeListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<GradeListGroup>() {
        override fun areItemsTheSame(
            oldItem: GradeListGroup,
            newItem: GradeListGroup
        ): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(
            oldItem: GradeListGroup,
            newItem: GradeListGroup
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

}