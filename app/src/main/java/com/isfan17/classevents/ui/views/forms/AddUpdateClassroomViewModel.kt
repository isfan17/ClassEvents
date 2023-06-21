package com.isfan17.classevents.ui.views.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.data.repositories.classroom.ClassroomRepository
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateClassroomViewModel @Inject constructor(
    private val classroomRepo: ClassroomRepository
): ViewModel() {

    private val _addClassroomFlow = MutableStateFlow<Resource<String>?>(null)
    val addClassroomFlow: StateFlow<Resource<String>?> = _addClassroomFlow

    private val _updateClassroomFlow = MutableStateFlow<Resource<String>?>(null)
    val updateClassroomFlow: StateFlow<Resource<String>?> = _updateClassroomFlow

    fun addClassroom(classroom: Classroom) = viewModelScope.launch {
        _addClassroomFlow.value = Resource.Loading
        _addClassroomFlow.value = classroomRepo.addClassroom(classroom)
    }

    fun updateClassroom(classroom: Classroom) = viewModelScope.launch {
        _updateClassroomFlow.value = Resource.Loading
        _updateClassroomFlow.value = classroomRepo.updateClassroom(classroom)
    }
}