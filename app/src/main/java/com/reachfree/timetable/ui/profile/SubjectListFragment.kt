package com.reachfree.timetable.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.databinding.FragmentSubjectListBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SubjectListFragment : BaseDialogFragment<FragmentSubjectListBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()
    private lateinit var subjectAdapter: SubjectAdapter

    private var semesterId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        semesterId = requireArguments().getLong(SEMESTER_ID, -1)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSubjectListBinding {
        return FragmentSubjectListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.text = "등록 과목"
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupRecyclerView() {
        binding.recyclerSubject.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObserver() {
        if (this.semesterId != -1L) {
            timetableViewModel.getAllSubjectBySemester(semesterId).observe(viewLifecycleOwner) { subjects ->
                Timber.d("DEBUG: subject is $subjects")
                subjectAdapter = SubjectAdapter(subjects)
                binding.recyclerSubject.adapter = subjectAdapter

            }
        }
    }

    companion object {
        const val TAG = "SubjectListFragment"
        private const val SEMESTER_ID = "semester_id"
        fun newInstance(semesterId: Long) = SubjectListFragment().apply {
            arguments = bundleOf(SEMESTER_ID to semesterId)
        }
    }

}