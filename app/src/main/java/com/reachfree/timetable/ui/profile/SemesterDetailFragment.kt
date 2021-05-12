package com.reachfree.timetable.ui.profile

import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.data.response.SemesterResponse
import com.reachfree.timetable.databinding.FragmentSemesterDetailBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.util.SpacingItemDecoration
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.weekview.runDelayed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SemesterDetailFragment : BaseDialogFragment<FragmentSemesterDetailBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()
    private lateinit var subjectAdapter: SemesterDetailSubjectAdapter

    private var passedSemester: SemesterResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        passedSemester = requireArguments().getParcelable(SEMESTER_RESPONSE)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSemesterDetailBinding {
        return FragmentSemesterDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.icon_back_arrow), SRC_ATOP)
        binding.appBar.btnDelete.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.icon_delete), SRC_ATOP)
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
        binding.appBar.btnDelete.setOnSingleClickListener { showConfirmDeleteAlert() }
    }

    private fun setupRecyclerView() {
        binding.recyclerSubject.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(SpacingItemDecoration(1, 32))
        }
    }

    private fun setupViewHandler() {
        
    }
    
    private fun subscribeToObserver() {
        passedSemester?.let { semester ->
            binding.appBar.txtToolbarTitle.text = semester.title
            semester.id?.let { semesterId ->
                timetableViewModel.getAllSubjectBySemester(semesterId).observe(viewLifecycleOwner) { subjects ->
                    val totalCredit = subjects.sumBy { it.credit }
                    binding.txtSemesterTotalCredit.text =
                        requireActivity().resources.getString(R.string.text_input_subject_total_credit, totalCredit)

                    subjectAdapter = SemesterDetailSubjectAdapter(subjects)
                    binding.recyclerSubject.apply {
                        adapter = subjectAdapter
                    }
                }
            }
        }
    }
    
    private fun showConfirmDeleteAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.text_alert_title_semester_delete_title))
            .setMessage(getString(R.string.text_alert_title_semester_delete_message))
            .setNegativeButton(getString(R.string.text_alert_button_cancel), null)
            .setPositiveButton(getString(R.string.text_alert_button_delete)) { dialog, which ->
                passedSemester?.let { semester ->
                    semester.id?.let { semesterId ->
                        runDelayed(500L) {
                            timetableViewModel.deleteSemesterById(semesterId)
                            Toast.makeText(requireActivity(), "삭제 완료!", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }
            }
            .create()
            .show()
    }

    companion object {
        const val TAG = "SubjectListFragment"
        private const val SEMESTER_RESPONSE = "semester_response"
        fun newInstance(semesterResponse: SemesterResponse) = SemesterDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(SEMESTER_RESPONSE, semesterResponse)
            }
        }
    }

}