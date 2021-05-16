package com.reachfree.timetable.ui.profile

import android.annotation.SuppressLint
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
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.response.SemesterResponse
import com.reachfree.timetable.databinding.FragmentSemesterDetailBinding
import com.reachfree.timetable.extension.*
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.timetable.TimetableDetailDialog
import com.reachfree.timetable.util.SpacingItemDecoration
import com.reachfree.timetable.viewmodel.TimetableViewModel
import com.reachfree.timetable.util.AppUtils
import com.reachfree.timetable.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class SemesterDetailFragment : BaseDialogFragment<FragmentSemesterDetailBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()
    private lateinit var subjectAdapter: SemesterDetailSubjectAdapter

    private var passedSemester: SemesterResponse? = null

    interface SemesterDetailFragmentListener {
        fun onEditButtonClicked(semesterId: Long)
        fun onSubjectItemClicked(subject: Subject)
        fun onAddButtonClicked()
        fun onSemesterDeleteButtonClicked()
    }

    private lateinit var semesterDetailFragmentListener: SemesterDetailFragmentListener

    fun setOnSemesterDetailFragmentListener(semesterDetailFragmentListener: SemesterDetailFragmentListener){
        this.semesterDetailFragmentListener = semesterDetailFragmentListener
    }

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

        setupDefaultView()
        setupToolbar()
        setupRecyclerView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupDefaultView() {
        passedSemester?.let { passedSemester ->
            passedSemester.id?.let { semesterId ->
                timetableViewModel.getSemesterByIdLiveData(semesterId).observe(viewLifecycleOwner) { semester ->
                    binding.txtSemesterTitle.text = semester.title

                    val startDate = DateUtils.semesterShortDateFormat.format(semester.startDate)
                    val endDate = DateUtils.semesterShortDateFormat.format(semester.endDate)
                    binding.txtSemesterDate.text = "($startDate ~ $endDate)"

                    val days = ChronoUnit.DAYS.between(DateUtils.convertDateToLocalDateTime(Date(semester.startDate)),
                        DateUtils.convertDateToLocalDateTime(Date(semester.endDate))) + 1L

                    if (LocalDateTime.now().isAfter(DateUtils.convertDateToLocalDateTime(Date(semester.startDate))) &&
                        LocalDateTime.now().isBefore(DateUtils.convertDateToLocalDateTime(Date(semester.endDate)))) {
                        val passedDays = ChronoUnit.DAYS.between(DateUtils.convertDateToLocalDateTime(Date(semester.startDate)),
                            LocalDateTime.now()) + 1L
                        Timber.d("DEBUG: 날짜차이 : $passedDays")
                        val percentage = AppUtils.calculatePercentage(passedDays.toInt(), days.toInt())
                        binding.progressbarPassedDays.animateProgressBar(percentage)
                        binding.txtPassedPercent.text = "$percentage%"
                        binding.txtPassedDaysComment.text = setupCommentByPercentage(percentage)
                    } else if (!LocalDateTime.now().isAfter(DateUtils.convertDateToLocalDateTime(Date(semester.startDate)))){
                        binding.progressbarPassedDays.animateProgressBar(0)
                        binding.txtPassedPercent.text = "0%"
                        binding.txtPassedDaysComment.text = setupCommentByPercentage(0)
                    } else if (!LocalDateTime.now().isBefore(DateUtils.convertDateToLocalDateTime(Date(semester.endDate)))) {
                        binding.progressbarPassedDays.animateProgressBar(100)
                        binding.txtPassedPercent.text = "100%"
                        binding.txtPassedDaysComment.text = setupCommentByPercentage(100)
                    }
                }
            }
        }
    }

    private fun setupCommentByPercentage(percent: Int): String {
        return when (percent) {
            0 -> { requireActivity().resources.getString(R.string.text_passed_days_0) }
            in 1..24 -> { requireActivity().resources.getString(R.string.text_passed_days_25) }
            in 25..49 -> { requireActivity().resources.getString(R.string.text_passed_days_50) }
            in 50..74 -> { requireActivity().resources.getString(R.string.text_passed_days_99) }
            in 75..99 -> { requireActivity().resources.getString(R.string.text_passed_days_99) }
            100 -> { requireActivity().resources.getString(R.string.text_passed_days_100) }
            else -> "화이팅하세요~!@"
        }
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
        binding.btnEditSemester.setOnSingleClickListener {
            passedSemester?.id?.let { semesterId ->
                semesterDetailFragmentListener.onEditButtonClicked(semesterId)
            }
        }

        binding.btnAddSubject.setOnSingleClickListener {
            semesterDetailFragmentListener.onAddButtonClicked()
        }
    }
    
    private fun subscribeToObserver() {
        passedSemester?.let { semester ->
            binding.appBar.txtToolbarTitle.text = semester.title
            semester.id?.let { semesterId ->
                timetableViewModel.getAllSubjectBySemester(semesterId).observe(viewLifecycleOwner) { subjects ->
                    if (!subjects.isNullOrEmpty()) {
                        binding.recyclerSubject.beVisible()
                        binding.layoutEmpty.beGone()
                    } else {
                        binding.recyclerSubject.beGone()
                        binding.layoutEmpty.beVisible()
                    }

                    val totalCredit = subjects.sumBy { it.credit }
                    binding.txtSemesterTotalCredit.text =
                        requireActivity().resources.getString(R.string.text_input_subject_total_credit, totalCredit)

                    subjectAdapter = SemesterDetailSubjectAdapter(subjects)
                    binding.recyclerSubject.adapter = subjectAdapter
                    subjectAdapter.setOnItemClickListener { subject ->
                        semesterDetailFragmentListener.onSubjectItemClicked(subject)
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
                            semesterDetailFragmentListener.onSemesterDeleteButtonClicked()
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