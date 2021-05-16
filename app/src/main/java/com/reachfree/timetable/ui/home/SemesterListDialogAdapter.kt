package com.reachfree.timetable.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.databinding.ItemSemesterListBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.DateUtils

class SemesterListDialogAdapter(
    private val semesters: List<Semester>
) : RecyclerView.Adapter<SemesterListDialogAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemSemesterListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(semester: Semester) {
            with(binding) {
                txtTitle.text = semester.title
                val startDate = DateUtils.semesterShortDateFormat.format(semester.startDate)
                val endDate = DateUtils.semesterShortDateFormat.format(semester.endDate)
                txtDate.text = "($startDate ~ $endDate)"

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(semester) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSemesterListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(semesters[position])
    }

    override fun getItemCount(): Int {
        return semesters.size
    }

    private var onItemClickListener: ((Semester) -> Unit)? = null

    fun setOnItemClickListener(listener: (Semester) -> Unit) {
        onItemClickListener = listener
    }

}