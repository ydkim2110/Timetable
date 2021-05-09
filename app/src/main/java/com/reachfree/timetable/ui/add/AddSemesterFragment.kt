package com.reachfree.timetable.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.FragmentAddSemesterBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.base.BaseFragment

class AddSemesterFragment : BaseDialogFragment<FragmentAddSemesterBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddSemesterBinding {
        return FragmentAddSemesterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.text = "학기 등록"
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    companion object {
        fun newInstance() = AddSemesterFragment()
    }

}