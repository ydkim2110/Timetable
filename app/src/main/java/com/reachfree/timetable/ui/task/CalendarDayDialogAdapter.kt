package com.reachfree.timetable.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.data.model.TaskType
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.databinding.ItemCalendarDayBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.DateUtils

class CalendarDayDialogAdapter(
    private val dataList: List<CalendarTaskResponse>
) : RecyclerView.Adapter<CalendarDayDialogAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CalendarTaskResponse) {
            with(binding) {
                txtTitle.text = data.title
                txtDescription.text = data.description
                txtDate.text = DateUtils.taskDateFormat.format(data.date)

                if (data.type == TaskType.TASK.ordinal) {
                    txtType.text = "과제"
                    txtType.setBackgroundResource(data.backgroundColor)
                } else {
                    txtType.text = "시험"
                    txtType.setBackgroundResource(data.backgroundColor)
                }

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(data) }
                }
            }
        }
    }

    private var onItemClickListener: ((CalendarTaskResponse) -> Unit)? = null

    fun setOnItemClickListener(listener: (CalendarTaskResponse) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarDayDialogAdapter.MyViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarDayDialogAdapter.MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}