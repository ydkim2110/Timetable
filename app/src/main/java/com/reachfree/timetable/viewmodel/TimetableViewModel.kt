package com.reachfree.timetable.viewmodel

import androidx.lifecycle.*
import com.reachfree.timetable.data.model.PartTimeJob
import com.reachfree.timetable.data.model.Semester
import com.reachfree.timetable.data.model.Subject
import com.reachfree.timetable.data.model.Task
import com.reachfree.timetable.data.repository.PartTimeJobRepository
import com.reachfree.timetable.data.repository.SemesterRepository
import com.reachfree.timetable.data.repository.SubjectRepository
import com.reachfree.timetable.data.repository.TaskRepository
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
    private val taskRepository: TaskRepository,
    private val partTimeJobRepository: PartTimeJobRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    /**
     *  Semester
     */
    val thisSemesterLiveData: LiveData<Semester> = semesterRepository.getSemesterLiveData(Date().time)


    fun insertSemester(semester: Semester) =
        viewModelScope.launch(dispatchers.io) {
            semesterRepository.insertSemester(semester)
        }

    fun updateSemester(semester: Semester) =
        viewModelScope.launch(dispatchers.io) {
            semesterRepository.updateSemester(semester)
        }

    fun deleteSemesterById(semesterId: Long) =
        viewModelScope.launch(dispatchers.io) {
            semesterRepository.deleteSemesterById(semesterId)
        }

    fun deleteSemesters() =
        viewModelScope.launch(dispatchers.io) {
            semesterRepository.deleteAllSemesters()
        }

    fun getLatestSemester() =
        semesterRepository.getLatestSemester()

    private var _semesterById = MutableLiveData<Semester>()
    val semesterById get() = _semesterById
    fun getSemesterById(semesterId: Long) =
        viewModelScope.launch(dispatchers.io) {
            val result = semesterRepository.getSemesterById(semesterId)
            _semesterById.postValue(result)
        }

    private var _thisSemester = MutableLiveData<Semester>()
    val thisSemester get() = _thisSemester
    fun getSemester(date: Long) =
        viewModelScope.launch(dispatchers.io) {
            val result = semesterRepository.getSemester(date)
            _thisSemester.postValue(result)
        }

    fun getSemesterByIdLiveData(semesterId: Long) =
        semesterRepository.getSemesterByIdLiveData(semesterId)

    private var _semesterByTaskId = MutableLiveData<Semester>()
    val semesterByTaskId get() = _semesterByTaskId

    fun getSemesterByTaskId(taskId: Long) =
        viewModelScope.launch(dispatchers.io) {
            val result = semesterRepository.getSemesterByTaskId(taskId)
            _semesterByTaskId.postValue(result)
        }

    private var _semesterList = MutableLiveData<List<Semester>>()
    val semesterList get() = _semesterList
    fun getAllSemesters() =
        viewModelScope.launch(dispatchers.io) {
            val result = semesterRepository.getAllSemesters()
            _semesterList.postValue(result)
        }

    fun getAllSemestersLiveData() =
        semesterRepository.getAllSemestersLiveData()

    fun getAllSemestersWithTotalCount() =
        semesterRepository.getAllSemestersWithTotalCount()


    /**
     *  Subject
     */
    fun insertSubject(subject: Subject) =
        viewModelScope.launch(dispatchers.io) {
            subjectRepository.insertSubject(subject)
        }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch(dispatchers.io) {
            subjectRepository.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch(dispatchers.io) {
            subjectRepository.deleteSubject(subject)
        }
    }

    fun deleteSubjects() =
        viewModelScope.launch(dispatchers.io) {
            subjectRepository.deleteAllSubjects()
        }

    private var _subject = MutableLiveData<Subject>()
    val subject get() = _subject
    fun getSubjectById(subjectId: Long) =
        viewModelScope.launch(dispatchers.io) {
            Timber.d("DEBUG: getSubjectById $subjectId")
            val result = subjectRepository.getSubjectById(subjectId)
            _subject.postValue(result)
        }

    fun getSubjectByIdLiveData(subjectId: Long) =
        subjectRepository.getSubjectByIdLiveData(subjectId)

    fun getAllSubjects() =
        subjectRepository.getAllSubjects()

    fun getTotalCreditByType() =
        subjectRepository.getTotalCreditByType()

    fun getAllSubjectBySemester(semesterId: Long) =
        subjectRepository.getAllSubjectBySemester(semesterId)

    fun getTotalCreditBySemester(semesterId: Long) =
        subjectRepository.getTotalCreditBySemester(semesterId)

    fun getAllTimetableList(semesterId: Long, currentDate: Long) =
        subjectRepository.getAllTimetableList(semesterId, currentDate)

    /**
     *  Task
     */
    val taskList = MediatorLiveData<List<Task>>()
    val calendarTaskList = MediatorLiveData<List<CalendarTaskResponse>>()

    fun insertTask(task: Task) {
        viewModelScope.launch(dispatchers.io) {
            taskRepository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(dispatchers.io) {
            taskRepository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(dispatchers.io) {
            taskRepository.deleteTask(task)
        }
    }

    private var _task = MutableLiveData<Task>()
    val task get() = _task
    fun getTaskById(taskId: Long)  {
        viewModelScope.launch(dispatchers.io) {
            val result = taskRepository.getTaskById(taskId)
            _task.postValue(result)
        }
    }

    fun getTaskByIdLiveDta(taskId: Long) =
        taskRepository.getTaskByIdLiveData(taskId)

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

    fun getAllTaskMediator(subjectIds: LongArray) {
        val response = taskRepository.getAllTaskBySubject(subjectIds)
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


    /**
     *  ParTimeJob
     */
    fun insertPartTimeJob(partTimeJob: PartTimeJob) =
        viewModelScope.launch(dispatchers.io) {
            partTimeJobRepository.insertPartTimeJob(partTimeJob)
        }

    private val _partTimeJobList = MutableLiveData<List<PartTimeJob>>()
    val partTimeJobList get() = _partTimeJobList
    fun getAllPartTimeJobs(currentDate: Long) =
        viewModelScope.launch(dispatchers.io) {
            val result = partTimeJobRepository.getAllPartTimeJobs(currentDate)
            _partTimeJobList.postValue(result)
        }

}