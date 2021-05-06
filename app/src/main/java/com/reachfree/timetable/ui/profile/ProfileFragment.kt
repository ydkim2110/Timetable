package com.reachfree.timetable.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.reachfree.timetable.databinding.FragmentProfileBinding
import com.reachfree.timetable.ui.base.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }

}