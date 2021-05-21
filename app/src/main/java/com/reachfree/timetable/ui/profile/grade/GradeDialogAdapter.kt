package com.reachfree.timetable.ui.profile.grade

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GradeType
import com.reachfree.timetable.databinding.ItemGradeTitleBinding

class GradeDialogAdapter(
    private val gradeList: List<GradeType>,
    position: Int
) : RecyclerView.Adapter<GradeDialogAdapter.MyViewHolder>() {

    private var selectedPosition = position

    inner class MyViewHolder(
        private val binding: ItemGradeTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(gradeType: GradeType) {
            with(binding) {
                txtGradeTitle.text = gradeType.title

                if (selectedPosition == -1) {
                    layoutGrade.setBackgroundResource(R.drawable.bg_btn_outline_gray)
                } else {
                    if (selectedPosition == adapterPosition) {
                        layoutGrade.setBackgroundResource(R.drawable.bg_btn_outline_orange)
                    } else {
                        layoutGrade.setBackgroundResource(R.drawable.bg_btn_outline_gray)
                    }
                }

                layoutGrade.setOnClickListener {
                    layoutGrade.setBackgroundResource(R.drawable.bg_btn_outline_orange)

                    if (selectedPosition != adapterPosition) {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = adapterPosition
                    }
                }
            }
        }
    }

    fun getSelected(): GradeType? {
        if (selectedPosition != -1) {
            return gradeList[selectedPosition]
        }
        return null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemGradeTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(gradeList[position])
    }

    override fun getItemCount(): Int {
        return gradeList.size
    }

}