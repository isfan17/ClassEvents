package com.isfan17.classevents.ui.views.home

import androidx.lifecycle.*
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.data.repositories.auth.AuthRepository
import com.isfan17.classevents.data.repositories.classroom.ClassroomRepository
import com.isfan17.classevents.data.repositories.datastore.DataStoreRepository
import com.isfan17.classevents.utils.CLASSROOM_SORT_KEY
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val classroomRepo: ClassroomRepository,
    private val dataStoreRepo: DataStoreRepository,
    authRepo: AuthRepository,
): ViewModel() {

    /**
     * CLASSROOM　DATA
     **/
    private val _classroomsFlow = MutableStateFlow<Resource<List<Classroom>>?>(null)
    val classroomsFlow: StateFlow<Resource<List<Classroom>>?> = _classroomsFlow

    val classroomSortFlow = dataStoreRepo.getClassroomSort(CLASSROOM_SORT_KEY)
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)

    fun getClassrooms(sortType: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                classroomRepo.getClassrooms(sortType)
                    .catch { e ->
                        _classroomsFlow.value = Resource.Failure(e.message.toString())
                    }
                    .collect { result ->
                        _classroomsFlow.value = result
                    }
            }
        }
    }

    fun storeClassroomSort(value: String) {
        viewModelScope.launch {
            dataStoreRepo.putClassroomSort(CLASSROOM_SORT_KEY, value)
        }
    }

    /**
    USER　DATA
     **/
    val userFlow = authRepo.getUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)
}