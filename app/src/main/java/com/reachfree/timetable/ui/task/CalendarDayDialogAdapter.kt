package com.reachfree.timetable.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.databinding.ItemCalendarDayBinding

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
            }
        }
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