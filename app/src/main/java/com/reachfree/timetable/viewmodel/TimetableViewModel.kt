package com.reachfree.timetable.viewmodel

import androidx.lifecycle.*
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.repository.SemesterRepository
import com.reachfree.timetable.data.repository.SubjectRepository
import com.reachfree.timetable.data.repository.TaskRepository
import com.reachfree.timetable.data.repository.TestRepository
import com.reachfree.timetable.data.response.CalendarTaskResponse
import com.reachfree.timetable.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
        private val semesterRepository: SemesterRepository,
        private val subjectRepository: SubjectRepository,
        private val testRepository: TestRepository,
        private val taskRepository: TaskRepository,
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
            viewModelScope.launch(dispatchers.io) {
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


    /**
     *  Test
     */



    /**
     *  Task
     */
    val taskList = MediatorLiveData<List<Task>>()
    val calendarTaskList = MediatorLiveData<List<CalendarTaskResponse>>()

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    fun getAllTasksMediator(subjects: List<Subject>) {
        for (i in subjects.indices) {
            val task = taskRepository.getAllTasksBySubject(subjects[i].id!!)
            taskList.addSource(task) { result ->
                result?.let { taskList.value = it }
            }
        }
    }

    fun getAllTaskMediator(subjects: List<Subject>) {
        subjects.forEach { subject ->
            val response = taskRepository.getAllTaskBySubject(subject.id!!)
            calendarTaskList.addSource(response) { result ->
                result?.let { calendarTaskList.value = it }
            }
        }
    }

    fun getAllTaskMediator(subjectsId: LongArray) {
        val response = taskRepository.getAllTaskBySubject(subjectsId)
        calendarTaskList.addSource(response) { result ->
            result?.let { calendarTaskList.value = it }
        }
    }

    fun getAllTaskMediator(
        startDay: Long,
        endDay: Long,
        subjectsId: LongArray
    ) {
        val response = taskRepository.getAllTaskBySubject(startDay, endDay, subjectsId)
        calendarTaskList.addSource(response) { result ->
            result?.let { calendarTaskList.value = it }
        }
    }

}