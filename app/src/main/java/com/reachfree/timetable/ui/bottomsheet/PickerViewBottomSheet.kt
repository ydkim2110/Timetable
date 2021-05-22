package com.reachfree.timetable.ui.bottomsheet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.databinding.PickerViewBottomSheetBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.picker.CustomPickerView
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickerViewBottomSheet(
    private val type: SelectType,
    private val semesterId: Long? = null,
    private val subjectId: Long? = null
) : BottomSheetDialogFragment() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private var _binding: PickerViewBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedSemesterItem: Semester
    private lateinit var selectedSubjectItem: Subject
    private var selectedSemesterPosition = 0
    private var selectedSubjectPosition = 0

    interface SelectSemesterListener {
        fun onSemesterSelected(semester: Semester)
        fun onSubjectSelected(subject: Subject)
    }

    private lateinit var selectSemesterListener: SelectSemesterListener

    fun setOnSelectSemesterListener(selectSemesterListener: SelectSemesterListener) {
        this.selectSemesterListener = selectSemesterListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SelectSemesterBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PickerViewBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewHandler()
        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        when (type) {
            SelectType.SEMESTER -> {
                timetableViewModel.getAllSemestersLiveData().observe(viewLifecycleOwner) { semesters ->
                    if (!semesters.isNullOrEmpty()) {
                        setupSemesterPickerView(semesters)
                    }
                }
            }
            SelectType.SUBJECT -> {
                semesterId?.let {
                    timetableViewModel.getAllSubjectBySemester(it).observe(viewLifecycleOwner) { subjects ->
                        if (!subjects.isNullOrEmpty()) {
                            setupSubjectPickerView(subjects)
                        }
                    }
                }
            }
            else -> throw IllegalStateException("Error...")
        }
    }

    private fun setupSemesterPickerView(semesters: List<Semester>) {
        with(binding.pickerView) {
            selectedItemColor = Color.RED
            unselectedItemColor = Color.RED
            linesColor = Color.GRAY
            offset = 1

            val selection = if (semesterId != null) {
                val semesterIdList = semesters.map { it.id }
                if (semesterIdList.contains(semesterId)) {
                    semesterIdList.indexOf(semesterId)
                } else {
                    0
                }
            } else {
                0
            }

            setSelection(selection)
            setItems(semesters)
        }

        setupGetSelectedItem()
    }

    private fun setupGetSelectedItem() {
        when (binding.pickerView.getSelectedItem) {
            is Semester -> {
                selectedSemesterItem = binding.pickerView.getSelectedItem as Semester
                selectedSemesterPosition = binding.pickerView.getSelectedIndex
            }
            is Subject -> {
                selectedSubjectItem = binding.pickerView.getSelectedItem as Subject
                selectedSubjectPosition = binding.pickerView.getSelectedIndex
            }
        }
    }

    private fun setupSubjectPickerView(subjects: List<Subject>) {
        with(binding.pickerView) {
            selectedItemColor = Color.RED
            unselectedItemColor = Color.RED
            linesColor = Color.GRAY
            offset = 1


            val selection = if (subjectId != null) {
                val subjectIdList = subjects.map { it.id }
                if (subjectIdList.contains(subjectId)) {
                    subjectIdList.indexOf(subjectId)
                } else {
                    0
                }
            } else {
                0
            }

            setSelection(selection)
            setItems(subjects)
        }

        setupGetSelectedItem()
    }

    private fun setupViewHandler() {
        binding.pickerView.onCustomPickerViewListener = object : CustomPickerView.OnCustomPickerViewListener() {
            override fun onSelected(selectedIndex: Int, item: Any) {
                super.onSelected(selectedIndex, item)

                when (item) {
                    is Semester -> {
                        selectedSemesterItem = item
                        selectedSemesterPosition = selectedIndex
                    }
                    is Subject -> {
                        selectedSubjectItem = item
                        selectedSubjectPosition = selectedIndex
                    }
                }
            }
        }

        binding.txtComplete.setOnSingleClickListener {
            when (type) {
                SelectType.SEMESTER -> {
                    selectSemesterListener.onSemesterSelected(selectedSemesterItem)
                    dismiss()
                }
                SelectType.SUBJECT -> {
                    selectSemesterListener.onSubjectSelected(selectedSubjectItem)
                    dismiss()
                }
                else -> {}
            }

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PickerViewBottomSheet"
    }
}