package com.reachfree.timetable.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.databinding.FragmentProfileBinding
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var profileHandlerListener: ProfileHandlerListener

    private lateinit var semesterAdapter: SemesterAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is HomeActivity) {
            profileHandlerListener = activity as HomeActivity
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObserver()
    }

    private fun setupRecyclerView() {
        binding.recyclerSemester.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObserver() {
        timetableViewModel.getAllSemestersWithTotalCount().observe(viewLifecycleOwner) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                semesterAdapter = SemesterAdapter(semesters)
                binding.recyclerSemester.adapter = semesterAdapter

                semesterAdapter.setOnItemClickListener { semesterResponse ->
                    profileHandlerListener.onDetailSubjectClicked(semesterResponse)
                }
            }
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }

}