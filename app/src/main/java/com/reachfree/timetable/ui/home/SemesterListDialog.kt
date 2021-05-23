package com.reachfree.timetable.ui.home

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.databinding.SemesterListDialogBinding
import com.reachfree.timetable.extension.beGone
import com.reachfree.timetable.extension.beVisible
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SemesterListDialog() : DialogFragment() {

    private val timetableViewModel: TimetableViewModel by viewModels()
    private lateinit var semesterListDialogAdapter: SemesterListDialogAdapter

    private var _binding: SemesterListDialogBinding? = null
    private val binding get() = _binding!!

    interface SemesterListDialogListener {
        fun onSemesterItemClicked(semester: Semester)
        fun onSemesterAddButtonClicked()
    }

    private lateinit var semesterListDialogListener: SemesterListDialogListener

    fun setOnSemesterListDialogListener(semesterListDialogListener: SemesterListDialogListener) {
        this.semesterListDialogListener = semesterListDialogListener
    }

    override fun onStart() {
        super.onStart()
        val outMetrics = DisplayMetrics()
        val display: Display?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getRealMetrics(outMetrics)
        }
        val size = Point()
        display?.getRealSize(size)

        dialog?.window?.setLayout((size.x * 0.7).toInt(), (size.y * 0.5).toInt())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  SemesterListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupView() {

    }

    private fun setupRecyclerView() {
        binding.recyclerSemester.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun setupViewHandler() {
        binding.btnAdd.setOnSingleClickListener {
            semesterListDialogListener.onSemesterAddButtonClicked()
            dismiss()
        }
    }

    private fun subscribeToObserver() {
        fetchAllSemesters()
    }

    private fun fetchAllSemesters() {
        timetableViewModel.getAllSemesters()
        timetableViewModel.semesterList.observe(viewLifecycleOwner) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                binding.recyclerSemester.beVisible()
                binding.layoutEmpty.beGone()
            } else {
                binding.recyclerSemester.beGone()
                binding.layoutEmpty.beVisible()
            }

            semesterListDialogAdapter = SemesterListDialogAdapter(semesters)
            binding.recyclerSemester.adapter = semesterListDialogAdapter
            semesterListDialogAdapter.setOnItemClickListener { semester ->
                semesterListDialogListener.onSemesterItemClicked(semester)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SemesterListDialog"
    }
}