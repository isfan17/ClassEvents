package com.isfan17.classevents.ui.views.searchs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.data.repositories.classroom.ClassroomRepository
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
class SearchClassroomsViewModel @Inject constructor(
    private val classroomRepo: ClassroomRepository,
): ViewModel() {

    private val _searchClassroomsFlow = MutableStateFlow<Resource<List<Classroom>>?>(null)
    val searchClassroomsFlow: StateFlow<Resource<List<Classroom>>?> = _searchClassroomsFlow

    fun searchClassrooms(query: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                classroomRepo.getClassroomsSearch(query)
                    .catch { e ->
                        _searchClassroomsFlow.value = Resource.Failure(e.message.toString())
                    }
                    .collect { result ->
                        _searchClassroomsFlow.value = result
                    }
            }
        }
    }
}