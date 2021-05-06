package com.reachfree.timetable.viewmodel

import androidx.lifecycle.*
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.repository.TimetableRepository
import com.reachfree.timetable.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val repository: TimetableRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    fun insertSemester(semester: Semester) =
        viewModelScope.launch(dispatchers.io) {
            repository.insertSemester(semester)
        }

}