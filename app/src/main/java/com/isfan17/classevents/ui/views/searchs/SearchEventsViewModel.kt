package com.isfan17.classevents.ui.views.searchs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfan17.classevents.data.model.Event
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
class SearchEventsViewModel @Inject constructor(
    private val eventRepo: EventRepository
): ViewModel() {

    private val _searchEventsFlow = MutableStateFlow<Resource<List<Event>>?>(null)
    val searchEventsFlow: StateFlow<Resource<List<Event>>?> = _searchEventsFlow

    fun searchEventsClassroom(query: String, classroomId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                eventRepo.getEventsClassroomSearch(query, classroomId)
                    .catch { e ->
                        _searchEventsFlow.value = Resource.Failure(e.message.toString())
                    }
                    .collect { result ->
                        _searchEventsFlow.value = result
                    }
            }
        }
    }
}