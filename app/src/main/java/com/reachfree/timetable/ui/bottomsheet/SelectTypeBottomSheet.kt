package com.reachfree.timetable.ui.bottomsheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.SelectTypeBottomSheetBinding
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.beVisible
import com.reachfree.timetable.extension.setOnSingleClickListener

class SelectTypeBottomSheet(
    private val fragmentName: String
) : BottomSheetDialogFragment() {

    private var _binding: SelectTypeBottomSheetBinding? = null
    private val binding get() = _binding!!

    interface SelectTypeListener {
        fun onSelected(type: SelectType)
    }

    private lateinit var selectTypeListener: SelectTypeListener

    fun setOnSelectTypeListener(selectTypeListener: SelectTypeListener) {
        this.selectTypeListener = selectTypeListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SelectTypeBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectTypeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewHandler()
    }

    private fun setupView() {
        when (fragmentName) {
            "timetable" -> {
                binding.layoutSemester.beVisible()
                binding.layoutSubject.beVisible()
                binding.layoutTest.beGone()
                binding.layoutTask.beGone()
            }
            "task" -> {
                binding.layoutSemester.beGone()
                binding.layoutSubject.beGone()
                binding.layoutTest.beVisible()
                binding.layoutTask.beVisible()
            }
            else -> {

            }
        }
    }

    private fun setupViewHandler() {
        binding.layoutSemester.setOnSingleClickListener {
            selectTypeListener.onSelected(SelectType.SEMESTER)
            dismiss()
        }
        binding.layoutSubject.setOnSingleClickListener {
            selectTypeListener.onSelected(SelectType.SUBJECT)
            dismiss()
        }
        binding.layoutTest.setOnSingleClickListener {
            selectTypeListener.onSelected(SelectType.TEST)
            dismiss()
        }
        binding.layoutTask.setOnSingleClickListener {
            selectTypeListener.onSelected(SelectType.TASK)
            dismiss()
        }
        binding.imgCloseIcon.setOnSingleClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SelectTypeBottomSheet"
    }
}