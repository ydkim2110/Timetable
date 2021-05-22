package com.reachfree.timetable.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.PartTimeJob
import com.reachfree.timetable.data.response.SemesterResponse
import com.reachfree.timetable.databinding.ItemPartTimeJobBinding
import com.reachfree.timetable.databinding.ItemSemesterBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.DateUtils

class PartTimeJobAdapter(
    private val semesters: List<PartTimeJob>
) : RecyclerView.Adapter<PartTimeJobAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemPartTimeJobBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(partTimeJob: PartTimeJob) {
            with(binding) {
                txtTitle.text = partTimeJob.title
                val startDate = DateUtils.semesterShortDateFormat.format(partTimeJob.startDate)
                val endDate = DateUtils.semesterShortDateFormat.format(partTimeJob.endDate)
                txtDate.text = "($startDate ~ $endDate)"

                var daysString = "요일: "
                for (i in partTimeJob.days.indices) {
                    daysString += DateUtils.convertDayToString(root.context, partTimeJob.days[i].day)
                    if (i != partTimeJob.days.size -1) {
                        daysString += ", "
                    }
                }

                txtDays.text = daysString

                layoutPartTimeJob.setOnSingleClickListener {
                    onItemClickListener?.let { it(partTimeJob) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemPartTimeJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(semesters[position])
    }

    override fun getItemCount(): Int {
        return semesters.size
    }

    private var onItemClickListener: ((PartTimeJob) -> Unit)? = null

    fun setOnItemClickListener(listener: (PartTimeJob) -> Unit) {
        onItemClickListener = listener
    }

}