package com.reachfree.timetable.ui.profile

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.data.model.GradeCreditType
import com.reachfree.timetable.data.model.GradeType
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.GradeDayDialogBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.SessionManager
import com.reachfree.timetable.util.SpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GradeDialog(
    private val subject: Subject
) : DialogFragment() {

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var gradeDialogAdapter: GradeDialogAdapter

    private var _binding: GradeDayDialogBinding? = null
    private val binding get() = _binding!!

    interface GradeDialogListener {
        fun onSaveButtonClicked(subject: Subject)
    }

    private lateinit var gradeDialogListener: GradeDialogListener

    fun setOnSelectTypeListener(gradeDialogListener: GradeDialogListener) {
        this.gradeDialogListener = gradeDialogListener
    }

    override fun onStart() {
        super.onStart()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        dialog?.window?.setLayout((size.x * 0.7).toInt(), (size.y * 0.5).toInt())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GradeDayDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewHandler()
    }

    private fun setupRecyclerView() {
        binding.recyclerGradeItem.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(
                requireActivity(),
                SPAN_COUNT,
                LinearLayoutManager.VERTICAL,
                false
            )
            var position = 0
            if (subject.grade.isNotBlank()) {
                GradeType.values().forEach {
                    if (it.title == subject.grade) {
                        position = it.ordinal
                    }
                }
            }

            val gradeTypeList = mutableListOf<GradeType>()
            gradeTypeList.clear()
            if (sessionManager.getGradeCreditOption() == GradeCreditType.CREDIT_4_3.ordinal) {
                gradeTypeList.addAll(GradeType.values().toList())
            } else {
                gradeTypeList.addAll(GradeType.values().filter { it.point45 != null }.toList())
            }

            gradeDialogAdapter = GradeDialogAdapter(gradeTypeList, position)
            addItemDecoration(SpacingItemDecoration(SPAN_COUNT, 32))
            adapter = gradeDialogAdapter
        }
    }

    private fun setupViewHandler() {
        binding.btnSave.setOnSingleClickListener {
            if (gradeDialogAdapter.getSelected() == GradeType.DELETE) {
                subject.grade = ""
                gradeDialogListener.onSaveButtonClicked(subject)
            } else {
                gradeDialogAdapter.getSelected()?.title?.let { grade ->
                    subject.grade = grade
                    gradeDialogListener.onSaveButtonClicked(subject)
                }
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SPAN_COUNT = 3
        const val TAG = "GradeDialog"
    }
}