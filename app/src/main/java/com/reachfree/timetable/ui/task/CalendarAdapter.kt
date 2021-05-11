package com.reachfree.timetable.ui.task

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.R
import com.reachfree.timetable.data.response.CalendarResponse
import com.reachfree.timetable.databinding.ItemCalendarBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import timber.log.Timber
import java.util.*

class CalendarAdapter(
    private val calendarResponse: CalendarResponse,
    private val calendar: Calendar,
    private val itemHeight: Int
) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(date: Date)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class MyViewHolder(
        private val binding: ItemCalendarBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("RtlHardcoded")
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

            with(binding) {
                if (adapterPosition % 7 == 0) {
                    txtCalendarDay.setTextColor(ContextCompat.getColor(root.context, android.R.color.holo_red_dark))
                } else if (adapterPosition % 7 == 6) {
                    txtCalendarDay.setTextColor(ContextCompat.getColor(root.context, android.R.color.holo_blue_dark))
                }

                if (!(displayMonth == currentMonth && displayYear == currentYear)) {
                    txtCalendarDay.setTextColor(ContextCompat.getColor(root.context, android.R.color.darker_gray))
                }

                if (displayYear == todayYear && displayMonth == todayMonth && displayDay == todayDay) {
                    txtCalendarDay.setBackgroundResource(R.drawable.bg_calendar_day)
                    txtCalendarDay.setTextColor(ContextCompat.getColor(root.context, android.R.color.white))
                }

                val eventCalendar = Calendar.getInstance()

                if (!calendarResponse.taskList.isNullOrEmpty()) {
                    val view = View.inflate(root.context, R.layout.layout_task_list, null)
                    val container = view.findViewById<LinearLayout>(R.id.task_text_container)
                    container.gravity = Gravity.CENTER

                    for (i in calendarResponse.taskList!!.indices) {
                        eventCalendar.time = Date(calendarResponse.taskList!![i].date!!)
                        if (dayNo == eventCalendar.get(Calendar.DAY_OF_MONTH)
                            && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                            && displayYear == eventCalendar.get(Calendar.YEAR)) {
                            val item = TextView(root.context)

                            item.run {
                                id = calendarResponse.taskList!![i].id!!.toInt()
                                text = calendarResponse.taskList!![i].title
                                textSize = 10f
                                gravity = Gravity.LEFT
                                maxLines = 1
                                ellipsize = TextUtils.TruncateAt.END
                                setBackgroundResource(calendarResponse.taskList!![i].backgroundColor)
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(0, 0, 0, 8)
                                }

                                layoutParams = params
                            }

                            container.addView(item)
                        }
                    }

                    layoutItemContainer.addView(container)
                }

                txtCalendarDay.text = dayNo.toString()

                root.setOnSingleClickListener {
                    onItemClickListener?.onItemClick(date)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val params:GridLayoutManager.LayoutParams = binding.root.layoutParams as GridLayoutManager.LayoutParams
        params.height = itemHeight / 6
        binding.root.layoutParams = params
        return  MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return calendarResponse.dateList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(calendarResponse.dateList[position])
    }

}