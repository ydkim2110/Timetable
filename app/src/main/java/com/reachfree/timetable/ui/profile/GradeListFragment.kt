package com.reachfree.timetable.ui.profile

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GradeListGroup
import com.reachfree.timetable.data.response.GradeListResponse
import com.reachfree.timetable.databinding.FragmentGradeListBinding
import com.reachfree.timetable.extension.setOnSingleClickListener
import com.reachfree.timetable.ui.base.BaseDialogFragment
import com.reachfree.timetable.ui.profile.grade.GradeListHeaderAdapter
import com.reachfree.timetable.util.SessionManager
import com.reachfree.timetable.util.SpacingItemDecoration
import com.reachfree.timetable.viewmodel.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GradeListFragment : BaseDialogFragment<FragmentGradeListBinding>() {

    @Inject lateinit var sessionManager: SessionManager
    private val timetableViewModel: TimetableViewModel by viewModels()
    private lateinit var gradeListHeaderAdapter: GradeListHeaderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGradeListBinding {
        return FragmentGradeListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.txtToolbarTitle.text = "전체 성적"
        binding.appBar.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.appBar.txtToolbarTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.appBar.btnBack.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.icon_back_arrow),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupRecyclerView() {
        binding.recyclerGradeList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            addItemDecoration(SpacingItemDecoration(1, 32))
        }
    }

    private fun subscribeToObserver() {
        timetableViewModel.getAllSubjectsWithSemesterInfo().observe(viewLifecycleOwner) { response ->
            val groupedSubjects = groupingSubjectsBySemester(response)

            val keyList = ArrayList(groupedSubjects.keys)
            val valueList = ArrayList(groupedSubjects.values)

            val newList = ArrayList<GradeListGroup>()
            if (keyList.size == valueList.size) {
                for (i in keyList.indices) {
                    Timber.d("DEBUG: KEY ${keyList[i]}")
                    val subjectGroup = GradeListGroup().apply {
                        key = keyList[i]
                        subjectList = valueList[i]
                    }
                    newList.add(subjectGroup)
                }
            }

            newList.sortByDescending { it.key }

            gradeListHeaderAdapter = GradeListHeaderAdapter(sessionManager)
            binding.recyclerGradeList.adapter = gradeListHeaderAdapter
            gradeListHeaderAdapter.submitList(newList)
        }
    }

    private fun groupingSubjectsBySemester(subjects: List<GradeListResponse>?): HashMap<String, ArrayList<GradeListResponse>> {
        val groupedSubjects: HashMap<String, ArrayList<GradeListResponse>> = HashMap()

        subjects?.let {
            for (i in it.indices) {
                val key = "${it[i].semesterEndDate}#${it[i].semesterTitle}"
                if (groupedSubjects.containsKey(key)) {
                    groupedSubjects[key]?.add(it[i])
                } else {
                    val list = ArrayList<GradeListResponse>()
                    list.add(it[i])
                    groupedSubjects[key] = list
                }
            }
        }
        return groupedSubjects
    }

    companion object {
        const val TAG = "GradeListFragment"
        fun newInstance() = GradeListFragment()
    }
}