package com.reachfree.timetable.ui.timetable

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.DialogFragment
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.TimetableDetailDialogBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.ColorTag
import com.reachfree.timetable.util.timetable.TimetableEventView

class TimetableDetailDialog(
    private val timetableEventView: TimetableEventView
) : DialogFragment() {

    private var _binding: TimetableDetailDialogBinding? = null
    private val binding get() = _binding!!

    interface TimetableDetailDialogListener {
        fun onEditButtonClicked(timetableEventView: TimetableEventView)
    }

    private lateinit var timetableDetailDialogListener: TimetableDetailDialogListener

    fun setOnSelectTypeListener(timetableDetailDialogListener: TimetableDetailDialogListener) {
        this.timetableDetailDialogListener = timetableDetailDialogListener
    }

    override fun onStart() {
        super.onStart()
        val outMetrics = DisplayMetrics()
        val display: Display?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getRealMetrics(outMetrics)
        }
        val size = Point()
        display?.getRealSize(size)

        dialog?.window?.setLayout((size.x * 0.7).toInt(), (size.y * 0.5).toInt())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  TimetableDetailDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewHandler()
    }

    private fun setupView() {
        binding.txtSubjectTitle.text = timetableEventView.event.title
        binding.txtClassroom.text = timetableEventView.event.classroom
        binding.txtBuilding.text = timetableEventView.event.building
        binding.txtCredit.text = getString(R.string.text_input_subject_credit, timetableEventView.event.credit)
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
            timetableDetailDialogListener.onEditButtonClicked(timetableEventView)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "TimetableDetailDialog"
    }
}