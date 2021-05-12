package com.reachfree.timetable.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.SubjectType
import com.reachfree.timetable.data.response.SemesterResponse
import com.reachfree.timetable.databinding.FragmentProfileBinding
import com.reachfree.timetable.extension.animateProgressBar
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.AppUtils
import com.reachfree.timetable.util.SessionManager
import com.reachfree.timetable.util.SpacingItemDecoration
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    private val timetableViewModel: TimetableViewModel by viewModels()

    interface ProfileHandlerListener {
        fun onSemesterItemClicked(semesterResponse: SemesterResponse)
    }

    private lateinit var profileHandlerListener: ProfileHandlerListener

    private lateinit var semesterAdapter: SemesterAdapter

    private var graduationTotalCredit: Int = 0
    private var mandatoryTotalCredit: Int = 0
    private var electiveTotalCredit: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is HomeActivity) {
            profileHandlerListener = activity as HomeActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        graduationTotalCredit = sessionManager.getGraduationTotalCredit()
        mandatoryTotalCredit = sessionManager.getMandatoryTotalCredit()
        electiveTotalCredit = sessionManager.getElectiveTotalCredit()
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
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpacingItemDecoration(2, 32))
        }
    }

    private fun subscribeToObserver() {
        timetableViewModel.getAllSubjects().observe(viewLifecycleOwner) { response ->
            if (!response.isNullOrEmpty()) {
                val mandatoryCredit = response.filter { it.type == SubjectType.MANDATORY.ordinal }
                    .sumOf { it.credit }
                val electiveCredit = response.filter { it.type == SubjectType.ELECTIVE.ordinal }
                    .sumOf { it.credit }

                val mandatory = AppUtils.calculatePercentage(mandatoryCredit, mandatoryTotalCredit)
                val elective = AppUtils.calculatePercentage(electiveCredit, electiveTotalCredit)

                binding.txtMandatory.text = getString(R.string.text_input_profile_mandatory, mandatoryCredit, mandatoryTotalCredit)
                binding.txtElective.text = getString(R.string.text_input_profile_elective, electiveCredit, electiveTotalCredit)

                binding.progressMandatory.animateProgressBar(mandatory)
                binding.progressElective.animateProgressBar(elective)
            } else {
                binding.txtMandatory.text = getString(R.string.text_input_profile_mandatory, DEFAULT_VALUE, mandatoryTotalCredit)
                binding.txtElective.text = getString(R.string.text_input_profile_elective, DEFAULT_VALUE, electiveTotalCredit)

                binding.progressMandatory.animateProgressBar(DEFAULT_VALUE)
                binding.progressElective.animateProgressBar(DEFAULT_VALUE)
            }
        }

        timetableViewModel.getAllSemestersWithTotalCount().observe(viewLifecycleOwner) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                semesterAdapter = SemesterAdapter(semesters)
                binding.recyclerSemester.adapter = semesterAdapter

                semesterAdapter.setOnItemClickListener { semesterResponse ->
                    profileHandlerListener.onSemesterItemClicked(semesterResponse)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_VALUE = 0

        fun newInstance() = ProfileFragment()
    }

}