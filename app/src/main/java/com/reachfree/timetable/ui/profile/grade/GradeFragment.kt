package com.reachfree.timetable.ui.profile.grade

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GradeCreditType
import com.reachfree.timetable.data.model.GradeType
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.FragmentGradeBinding
import com.reachfree.timetable.extension.*
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.task.CalendarDayDialog
import com.reachfree.timetable.util.SessionManager
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GradeFragment : BaseDialogFragment<FragmentGradeBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager
    private val timetableViewModel: TimetableViewModel by viewModels()
    private lateinit var gradeAdapter: GradeAdapter

    private lateinit var gradeDialog: GradeDialog

    private var passedSemesterId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        passedSemesterId = requireArguments().getLong(SEMESTER_ID)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGradeBinding {
        return FragmentGradeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupOptions()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.txtToolbarTitle.text = getString(R.string.text_grade)
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.appBar.btnBack.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.icon_back_arrow),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupRecyclerView() {
        binding.recyclerGrade.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupOptions() {
        binding.txtGradeCreditOption.text = GradeCreditType.values()
            .find { it.ordinal == sessionManager.getGradeCreditOption() }?.stringRes?.let {
                getString(it)
            } ?: getString(GradeCreditType.CREDIT_4_3.stringRes)
    }

    private fun subscribeToObserver() {
        passedSemesterId?.let { semesterId ->
            timetableViewModel.getSemesterById(semesterId)
            timetableViewModel.semesterById.observe(viewLifecycleOwner) {
                binding.appBar.txtToolbarTitle.text = getString(R.string.text_input_toolbar_grade, it.title)
            }

            timetableViewModel.getAllSubjectBySemester(semesterId).observe(viewLifecycleOwner) { subjects ->
                setupView(subjects)
                gradeAdapter = GradeAdapter(subjects)
                binding.recyclerGrade.adapter = gradeAdapter
                gradeAdapter.setOnItemClickListener { subject ->
                    showGradeDialog(subject)
                }
            }
        } ?: run {
            dismiss()
        }
    }

    private fun setupView(subjects: List<Subject>?) {
        if (!subjects.isNullOrEmpty()) {
            binding.layoutEmpty.beGone()
            binding.txtTotalCredit.text = getString(R.string.text_input_subject_credit, subjects.sumBy { it.credit })
            val averageCredit = if (sessionManager.getGradeCreditOption() == GradeCreditType.CREDIT_4_3.ordinal) {
                GradeType.calculateAverageGradeBy43(subjects)
            } else {
                GradeType.calculateAverageGradeBy45(subjects)
            }
            binding.txtAverageGrade.text = String.format("%.2f", averageCredit)
        } else {
            binding.layoutEmpty.beVisible()
            binding.txtTotalCredit.text = getString(R.string.text_input_subject_credit, 0)
            binding.txtAverageGrade.text = String.format("%.2f", 0f)
        }
    }

    private fun showGradeDialog(subject: Subject) {
        gradeDialog = GradeDialog(subject)
        gradeDialog.isCancelable = true
        gradeDialog.show(childFragmentManager, CalendarDayDialog.TAG)
        gradeDialog.setOnSelectTypeListener(object : GradeDialog.GradeDialogListener {
            override fun onSaveButtonClicked(subject: Subject) {
                timetableViewModel.updateSubject(subject)
                runDelayed(500L) {
                    requireActivity().longToast(getString(R.string.toast_save_complete_message))
                }
            }
        })
    }

    companion object {
        const val TAG = "GradeFragment"
        private const val SEMESTER_ID = "semester_id"
        fun newInstance(semesterId: Long) = GradeFragment().apply {
            arguments = bundleOf(SEMESTER_ID to semesterId)
        }
    }


}