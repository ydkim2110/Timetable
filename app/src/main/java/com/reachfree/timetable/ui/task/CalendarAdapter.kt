package com.reachfree.timetable.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.databinding.ItemCalendarBinding
import java.util.*

class CalendarAdapter(
    private val dateList: List<Date>,
    private val calendar: Calendar,
    private val itemHeight: Int
) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemCalendarBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {
            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = date

            val dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH)
            val displayYear = dateCalendar.get(Calendar.YEAR)
            val displayMonth = dateCalendar.get(Calendar.MONTH) + 1
            val displayDay = dateCalendar.get(Calendar.DAY_OF_MONTH)

            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val todayYear = Calendar.getInstance().get(Calendar.YEAR)
            val todayMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
            val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            binding.txtCalendarDay.text = dayNo.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val params:GridLayoutManager.LayoutParams = binding.root.layoutParams as GridLayoutManager.LayoutParams
        params.height = itemHeight / 5
        binding.root.layoutParams = params
        return  MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dateList[position])
    }

}