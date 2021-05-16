package com.reachfree.timetable.ui.profile

import android.content.Context
import android.content.SharedPreferences
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
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.beVisible
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseFragment
import com.reachfree.timetable.ui.home.HomeActivity
import com.reachfree.timetable.util.*
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager
    private val timetableViewModel: TimetableViewModel by viewModels()

    interface ProfileFragmentListener {
        fun onSemesterItemClicked(semesterResponse: SemesterResponse)
        fun onAddSemesterButtonClicked()
    }

    private lateinit var profileFragmentListener: ProfileFragmentListener

    private lateinit var semesterAdapter: SemesterAdapter

    private var graduationTotalCredit: Int = 0
    private var mandatoryTotalCredit: Int = 0
    private var electiveTotalCredit: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is HomeActivity) {
            profileFragmentListener = activity as HomeActivity
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
        setupViewHandler()
        subscribeToObserver()

        sessionManager.getPrefs().registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    private fun setupRecyclerView() {
        binding.recyclerSemester.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpacingItemDecoration(SPAN_COUNT, SPAN_SPACING))
        }
    }

    private fun setupViewHandler() {
        binding.btnAddSemester.setOnSingleClickListener {
            profileFragmentListener.onAddSemesterButtonClicked()
        }
    }

    private fun subscribeToObserver() {
        timetableViewModel.getAllSubjects().observe(viewLifecycleOwner) { response ->
            if (!response.isNullOrEmpty()) {
                val mandatoryCredit = response.filter { it.type == SubjectType.MANDATORY.ordinal }
                    .sumOf { it.credit }
                val electiveCredit = response.filter { it.type == SubjectType.ELECTIVE.ordinal }
                    .sumOf { it.credit }
                val graduationCredit = mandatoryCredit + electiveCredit

                val graduation = AppUtils.calculatePercentage(graduationCredit, graduationTotalCredit)
                val mandatory = AppUtils.calculatePercentage(mandatoryCredit, mandatoryTotalCredit)
                val elective = AppUtils.calculatePercentage(electiveCredit, electiveTotalCredit)

                binding.txtGraduation.text = getString(R.string.text_input_profile_graduation, graduationCredit, graduationTotalCredit)
                binding.txtMandatory.text = getString(R.string.text_input_profile_mandatory, mandatoryCredit, mandatoryTotalCredit)
                binding.txtElective.text = getString(R.string.text_input_profile_elective, electiveCredit, electiveTotalCredit)

                binding.progressGraduation.animateProgressBar(graduation)
                binding.progressMandatory.animateProgressBar(mandatory)
                binding.progressElective.animateProgressBar(elective)
            } else {
                binding.txtGraduation.text = getString(R.string.text_input_profile_graduation, DEFAULT_VALUE, graduationTotalCredit)
                binding.txtMandatory.text = getString(R.string.text_input_profile_mandatory, DEFAULT_VALUE, mandatoryTotalCredit)
                binding.txtElective.text = getString(R.string.text_input_profile_elective, DEFAULT_VALUE, electiveTotalCredit)

                binding.progressGraduation.animateProgressBar(DEFAULT_VALUE)
                binding.progressMandatory.animateProgressBar(DEFAULT_VALUE)
                binding.progressElective.animateProgressBar(DEFAULT_VALUE)
            }
        }

        timetableViewModel.getAllSemestersWithTotalCount().observe(viewLifecycleOwner) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                binding.recyclerSemester.beVisible()
                binding.layoutEmpty.beGone()
            } else {
                binding.recyclerSemester.beGone()
                binding.layoutEmpty.beVisible()
            }

            semesterAdapter = SemesterAdapter(semesters)
            binding.recyclerSemester.adapter = semesterAdapter

            semesterAdapter.setOnItemClickListener { semesterResponse ->
                profileFragmentListener.onSemesterItemClicked(semesterResponse)
            }
        }
    }

    private val sharedPrefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
            when (key) {
                GRADUATION_CREDIT -> {
                    graduationTotalCredit = sessionManager.getGraduationTotalCredit()
                    subscribeToObserver()
                }
                MANDATORY_CREDIT -> {
                    mandatoryTotalCredit = sessionManager.getMandatoryTotalCredit()
                    subscribeToObserver()
                }
                ELECTIVE_CREDIT -> {
                    electiveTotalCredit = sessionManager.getElectiveTotalCredit()
                    subscribeToObserver()
                }
            }
        }

    companion object {
        private const val SPAN_COUNT = 2
        private const val SPAN_SPACING = 32
        private const val DEFAULT_VALUE = 0

        fun newInstance() = ProfileFragment()
    }

}