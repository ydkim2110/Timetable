package com.reachfree.timetable.viewmodel

import androidx.lifecycle.*
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.repository.SemesterRepository
import com.reachfree.timetable.data.repository.SubjectRepository
import com.reachfree.timetable.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
        private val semesterRepository: SemesterRepository,
        private val subjectRepository: SubjectRepository,
        private val dispatchers: DispatcherProvider
) : ViewModel() {

    val thisSemester: LiveData<Semester> = semesterRepository.getSemester(Date().time)

    /**
     *  Semester
     */
    fun insertSemester(semester: Semester) =
            viewModelScope.launch(dispatchers.io) {
                semesterRepository.insertSemester(semester)
            }

    fun deleteSemesters() =
            viewModelScope.launch(dispatchers.io) {
                semesterRepository.deleteAllSemesters()
            }

    fun getAllSemesters() =
        semesterRepository.getAllSemesters()

    fun getAllSemestersWithTotalCount() =
        semesterRepository.getAllSemestersWithTotalCount()

    /**
     *  Subject
     */
    fun insertSubject(subject: Subject) =
            viewModelScope.launch {
                subjectRepository.insertSubject(subject)
            }

    fun deleteSubjects() =
            viewModelScope.launch(dispatchers.io) {
                subjectRepository.deleteAllSubjects()
            }

    fun getAllSubjectBySemester(semesterId: Long) =
            subjectRepository.getAllSubjectBySemester(semesterId)

    fun getTotalCreditBySemester(semesterId: Long) =
        subjectRepository.getTotalCreditBySemester(semesterId)
}