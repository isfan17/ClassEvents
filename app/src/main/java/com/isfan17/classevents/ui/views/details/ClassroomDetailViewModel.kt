package com.isfan17.classevents.ui.views.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.data.repositories.classroom.ClassroomRepository
import com.isfan17.classevents.data.repositories.datastore.DataStoreRepository
import com.isfan17.classevents.data.repositories.event.EventRepository
import com.isfan17.classevents.utils.EVENT_SORT_KEY
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClassroomDetailViewModel @Inject constructor(
    private val classroomRepo: ClassroomRepository,
    private val eventRepo: EventRepository,
    private val dataStoreRepo: DataStoreRepository,
): ViewModel() {

    private val _classroomFlow = MutableStateFlow<Resource<Classroom>?>(null)
    val classroomFlow: StateFlow<Resource<Classroom>?> = _classroomFlow

    private val _deleteClassroomFlow = MutableStateFlow<Resource<String>?>(null)
    val deleteClassroomFlow: StateFlow<Resource<String>?> = _deleteClassroomFlow

    private val _eventsFlow = MutableStateFlow<Resource<List<Event>>?>(null)
    val eventsFlow: StateFlow<Resource<List<Event>>?> = _eventsFlow

    val eventsSortFlow = dataStoreRepo.getEventSort(EVENT_SORT_KEY)
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)

    fun getClassroom(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                classroomRepo.getClassroom(id)
                    .catch { e ->
                        _classroomFlow.value = Resource.Failure(e.message.toString())
                    }
                    .collect { result ->
                        _classroomFlow.value = result
                    }
            }
        }
    }

    fun deleteClassroom(classroom: Classroom) = viewModelScope.launch {
        _deleteClassroomFlow.value = Resource.Loading
        _deleteClassroomFlow.value = classroomRepo.deleteClassroom(classroom)
    }

    fun getEvents(classroomId: String, sortType: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                eventRepo.getEventsClassroom(classroomId, sortType)
                    .catch { e ->
                        _eventsFlow.value = Resource.Failure(e.message.toString())
                    }
                    .collect { result ->
                        _eventsFlow.value = result
                    }
            }
        }
    }

    fun storeEventSort(value: String) {
        viewModelScope.launch {
            dataStoreRepo.putEventSort(EVENT_SORT_KEY, value)
        }
    }
}