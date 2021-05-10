package com.reachfree.timetable.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.databinding.FragmentAddTaskBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.bottomsheet.SelectSemesterBottomSheet
import com.reachfree.timetable.ui.bottomsheet.SelectType
import com.reachfree.timetable.ui.setup.DatePickerFragment
import com.reachfree.timetable.ui.setup.SetupActivity
import com.reachfree.timetable.util.DateUtils
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddTaskFragment : BaseDialogFragment<FragmentAddTaskBinding>() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    private lateinit var selectedSemester: Semester
    private lateinit var selectedSubject: Subject
    private lateinit var selectSemesterBottomSheet: SelectSemesterBottomSheet

    private var selectedSemesterId: Long? = null
    private var selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddTaskBinding {
        return FragmentAddTaskBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        binding.appBar.txtToolbarTitle.text = "과제 등록"
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupView() {
        binding.btnDate.text = DateUtils.defaultDateFormat.format(selectedDate.time.time)
    }

    private fun setupViewHandler() {
        binding.btnSemester.setOnSingleClickListener {
            selectSemesterBottomSheet = SelectSemesterBottomSheet(SelectType.SEMESTER)
            selectSemesterBottomSheet.isCancelable = true
            selectSemesterBottomSheet.show(childFragmentManager, SelectSemesterBottomSheet.TAG)

            setupBottomSheetListener()
        }
        binding.btnSubject.setOnSingleClickListener {
            selectedSemesterId?.let {
                selectSemesterBottomSheet = SelectSemesterBottomSheet(SelectType.SUBJECT, it)
                selectSemesterBottomSheet.isCancelable = true
                selectSemesterBottomSheet.show(childFragmentManager, SelectSemesterBottomSheet.TAG)

                setupBottomSheetListener()
            }
        }

        binding.btnDate.setOnSingleClickListener {
            showDatePicker(SetupActivity.START_TIME)
        }

        binding.deleteSaveBtnLayout.btnSave.setOnSingleClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        val taskTitle = binding.edtTaskTitle.text.toString().trim()
        val taskDescription = binding.edtTaskDescription.text.toString().trim()

        selectedSemesterId?.let {
            val task = Task(
                id = null,
                title = taskTitle,
                description = taskDescription,
                date = selectedDate.time.time,
                subjectId = it
            )

            timetableViewModel.insertTask(task)
        } ?: return


    }

    private fun showDatePicker(typeName: String) {
        DatePickerFragment.newInstance(typeName).apply {
            dateSelected = { year, month, dayOfMonth, type -> dateSet(year, month, dayOfMonth, type) }
        }.show(childFragmentManager, DatePickerFragment.TAG)
    }

    private fun dateSet(year: Int, month: Int, dayOfMonth: Int, type: String) {
        when (type) {
            SetupActivity.START_TIME -> {
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.btnDate.text = DateUtils.defaultDateFormat.format(selectedDate.time)
            }
        }
    }

    private fun setupBottomSheetListener() {
        selectSemesterBottomSheet.setOnSelectSemesterListener(object : SelectSemesterBottomSheet.SelectSemesterListener {
            override fun onSemesterSelected(semester: Semester) {
                selectedSemester = semester
                selectedSemesterId = selectedSemester.id
                binding.btnSemester.text = selectedSemester.title

                getAllSubjects()
            }

            override fun onSubjectSelected(subject: Subject) {
                selectedSubject = subject
                binding.btnSubject.text = selectedSubject.title
            }
        })
    }


    private fun subscribeToObserver() {
        timetableViewModel.thisSemester.observe(viewLifecycleOwner) { semester ->
            if (semester != null) {
                selectedSemester = semester
                selectedSemesterId = selectedSemester.id
                binding.btnSemester.text = selectedSemester.title
                getAllSubjects()
            } else {
                timetableViewModel.getAllSemesters().observe(viewLifecycleOwner) { semesters ->
                    if (!semesters.isNullOrEmpty()) {
                        //TODO: 날짜 비교하여 해당 학기로 세팅
                        selectedSemester = semesters[0]
                        selectedSemesterId = selectedSemester.id
                        binding.btnSemester.text = selectedSemester.title
                        getAllSubjects()
                    }
                }
            }
        }
    }

    private fun getAllSubjects() {
        selectedSemesterId?.let {
            timetableViewModel.getAllSubjectBySemester(it).observe(viewLifecycleOwner) { subjects ->
                if (!subjects.isNullOrEmpty()) {
                    binding.btnSubject.text = subjects[0].title
                } else {
                    binding.btnSubject.text = "과목명"
                }
            }
        }
    }

    companion object {
        fun newInstance() = AddTaskFragment()
    }

}