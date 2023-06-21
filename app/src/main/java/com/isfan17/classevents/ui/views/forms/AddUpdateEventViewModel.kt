package com.isfan17.classevents.ui.views.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.data.repositories.event.EventRepository
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateEventViewModel @Inject constructor(
    private val eventRepo: EventRepository
): ViewModel() {

    private val _addEventFlow = MutableStateFlow<Resource<String>?>(null)
    val addEventFlow: StateFlow<Resource<String>?> = _addEventFlow

    private val _updateEventFlow = MutableStateFlow<Resource<String>?>(null)
    val updateEventFlow: StateFlow<Resource<String>?> = _updateEventFlow

    fun addEvent(event: Event) = viewModelScope.launch {
        _addEventFlow.value = Resource.Loading
        _addEventFlow.value = eventRepo.addEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        _updateEventFlow.value = Resource.Loading
        _updateEventFlow.value = eventRepo.updateEvent(event)
    }
}