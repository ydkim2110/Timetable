package com.reachfree.timetable.ui.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.FragmentTaskBinding
import com.reachfree.timetable.ui.base.BaseFragment

class TaskFragment : BaseFragment<FragmentTaskBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskBinding {
        return FragmentTaskBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = TaskFragment()
    }

}