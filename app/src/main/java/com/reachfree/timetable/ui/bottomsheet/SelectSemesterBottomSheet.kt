package com.reachfree.timetable.ui.bottomsheet

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.databinding.SelectSemesterBottomSheetBinding
import com.reachfree.timetable.databinding.SelectTypeBottomSheetBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.util.picker.CustomPickerView
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SelectSemesterBottomSheet : BottomSheetDialogFragment() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private var _binding: SelectSemesterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedItem: Semester
    private var selectedItemPosition = 0

    interface SelectSemesterListener {
        fun onSelected(semester: Semester)
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
        _binding = SelectSemesterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewHandler()
        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        timetableViewModel.getAllSemesters().observe(viewLifecycleOwner) { semesters ->
            if (!semesters.isNullOrEmpty()) {
                setupPickerView(semesters)
            }
        }
    }

    private fun setupPickerView(semesters: List<Semester>) {
        with(binding.pickerView) {
            selectedItemColor = Color.RED
            unselectedItemColor = Color.RED
            linesColor = Color.GRAY
            offset = 1

            val semesterTitleList = semesters.map { it.title }

            val defaultValue = semesterTitleList[0]

            if (semesterTitleList.contains(defaultValue)) {
                val valueIndex = semesterTitleList.indexOf(defaultValue)
                setSelection(valueIndex)
            } else {
                setSelection(0)
            }

            setItems(semesters)
        }

        selectedItem = binding.pickerView.getSelectedItem
        selectedItemPosition = binding.pickerView.getSelectedIndex
    }

    private fun setupViewHandler() {
        binding.pickerView.onCustomPickerViewListener = object : CustomPickerView.OnCustomPickerViewListener() {
            override fun onSelected(selectedIndex: Int, item: Semester) {
                super.onSelected(selectedIndex, item)
                Timber.d("DEBUG:  selectedItem $item")
                selectedItem = item
                selectedItemPosition = selectedIndex
            }
        }

        binding.txtComplete.setOnSingleClickListener {
            selectSemesterListener.onSelected(selectedItem)
            dismiss()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SelectSemesterBottomSheetBinding"
    }
}