package com.reachfree.timetable.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.response.SemesterResponse
import com.reachfree.timetable.databinding.ItemSemesterBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.DateUtils

class SemesterAdapter(
    private val semesters: List<SemesterResponse>
) : RecyclerView.Adapter<SemesterAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemSemesterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(semester: SemesterResponse) {
            with(binding) {
                txtTitle.text = semester.title
                val startDate = DateUtils.semesterShortDateFormat.format(semester.startDate)
                val endDate = DateUtils.semesterShortDateFormat.format(semester.endDate)
                txtDate.text = "($startDate ~ $endDate)"
                txtTotalCredit.text = root.context.getString(R.string.text_input_subject_credit, semester.totalCredit)

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(semester) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSemesterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(semesters[position])
    }

    override fun getItemCount(): Int {
        return semesters.size
    }

    private var onItemClickListener: ((SemesterResponse) -> Unit)? = null

    fun setOnItemClickListener(listener: (SemesterResponse) -> Unit) {
        onItemClickListener = listener
    }

}