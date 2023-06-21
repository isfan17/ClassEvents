package com.isfan17.classevents.ui.views.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.data.repositories.classroom.ClassroomRepository
import com.isfan17.classevents.data.repositories.event.EventRepository
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val classroomRepo: ClassroomRepository,
    private val eventRepo: EventRepository
): ViewModel() {

    private val _classroomFlow = MutableStateFlow<Resource<Classroom>?>(null)
    val classroomFlow: StateFlow<Resource<Classroom>?> = _classroomFlow

    private val _eventFlow = MutableStateFlow<Resource<Event>?>(null)
    val eventFlow: StateFlow<Resource<Event>?> = _eventFlow

    private val _deleteEventFlow = MutableStateFlow<Resource<String>?>(null)
    val deleteEventFlow: StateFlow<Resource<String>?> = _deleteEventFlow

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

    fun getEvent(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                eventRepo.getEvent(id)
                    .catch { e ->
                        _eventFlow.value = Resource.Failure(e.message.toString())
                    }
                    .collect { result ->
                        _eventFlow.value = result
                    }
            }
        }
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        _deleteEventFlow.value = Resource.Loading
        _deleteEventFlow.value = eventRepo.deleteEvent(event)
    }
}