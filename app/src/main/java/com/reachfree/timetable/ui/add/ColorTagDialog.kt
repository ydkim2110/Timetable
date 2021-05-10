package com.reachfree.timetable.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.reachfree.timetable.databinding.ColorTagDialogBinding
import com.reachfree.timetable.util.ColorTag

class ColorTagDialog : DialogFragment() {

    private var _binding: ColorTagDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var colorTagAdapter: ColorTagAdapter
    private lateinit var colorList: ArrayList<ColorTag>

    private lateinit var onColorTagSelected: OnColorTagSelected

    interface OnColorTagSelected {
        fun onSelectedItem(colorTag: ColorTag)
    }

    fun setColorTagSelected(onColorTagSelected: OnColorTagSelected) {
        this.onColorTagSelected = onColorTagSelected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        colorList = ArrayList()
        colorList.clear()
        for (type in ColorTag.values()) {
            colorList.add(type)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ColorTagDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewHandler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.recyclerColorTag.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 5)
            colorTagAdapter = ColorTagAdapter(colorList)
            adapter = colorTagAdapter
        }
    }

    private fun initViewHandler() {
        colorTagAdapter.setOnItemClickListener { data ->
            onColorTagSelected.onSelectedItem(data)
            dismiss()
        }
    }

    companion object {
        const val TAG = "ColorTagDialog"
    }
}