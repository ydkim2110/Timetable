package com.reachfree.timetable.ui.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.timetable.databinding.ItemColorTagBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.ColorTag

class ColorTagAdapter(
    private val colorList: ArrayList<ColorTag>
) : RecyclerView.Adapter<ColorTagAdapter.ColorViewHolder>() {

    inner class ColorViewHolder(
        private val binding: ItemColorTagBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(colorTag: ColorTag) {
            binding.viewColorTag.setBackgroundResource(colorTag.resBg)

            binding.viewColorTag.setOnSingleClickListener {
                onItemClickListener?.let { it(colorTag) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(ItemColorTagBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colorList[position])
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    private var onItemClickListener: ((ColorTag) -> Unit)? = null

    fun setOnItemClickListener(listener: (ColorTag) -> Unit) {
        onItemClickListener = listener
    }

}