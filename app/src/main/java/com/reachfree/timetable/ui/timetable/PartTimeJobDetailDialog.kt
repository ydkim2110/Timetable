package com.reachfree.timetable.ui.timetable

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.reachfree.timetable.databinding.PartTimeJobDetailDialogBinding
import com.reachfree.timetable.databinding.TimetableDetailDialogBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.ColorTag
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.util.timetable.TimetableEventView

class PartTimeJobDetailDialog(
    private val timetableEventView: TimetableEventView
) : DialogFragment() {

    private var _binding: PartTimeJobDetailDialogBinding? = null
    private val binding get() = _binding!!

    interface PartTimeJobDetailDialogListener {
        fun onEditButtonClicked(timetableEventView: TimetableEventView)
    }

    private lateinit var partTimeJobDetailDialogListener: PartTimeJobDetailDialogListener

    fun setOnSelectTypeListener(partTimeJobDetailDialogListener: PartTimeJobDetailDialogListener) {
        this.partTimeJobDetailDialogListener = partTimeJobDetailDialogListener
    }

    override fun onStart() {
        super.onStart()
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        dialog?.window?.setLayout((size.x * 0.7).toInt(), (size.y * 0.5).toInt())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  PartTimeJobDetailDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewHandler()
    }

    private fun setupView() {
        binding.txtPartTimeJobTitle.text = timetableEventView.event.title
        binding.txtStartDate.text = DateUtils.defaultDateFormat.format(timetableEventView.event.startDate)
        binding.txtEndDate.text = DateUtils.defaultDateFormat.format(timetableEventView.event.endDate)
        binding.txtStartTime.text = timetableEventView.event.startTime.toString()
        binding.txtEndTime.text = timetableEventView.event.endTime.toString()

        for (i in ColorTag.values().indices) {
            if (ColorTag.values()[i].resId == timetableEventView.event.backgroundColor) {
                binding.imgBackgroundColor.setBackgroundResource(ColorTag.values()[i].resBg)
                break
            }
        }
    }

    private fun setupViewHandler() {
        binding.btnEdit.setOnSingleClickListener {
            partTimeJobDetailDialogListener.onEditButtonClicked(timetableEventView)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PartTimeJobDetailDialog"
    }
}