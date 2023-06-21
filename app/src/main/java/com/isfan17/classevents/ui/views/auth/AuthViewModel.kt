package com.isfan17.classevents.ui.views.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfan17.classevents.data.repositories.auth.AuthRepository
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<String>?>(null)
    val loginFlow : StateFlow<Resource<String>?> = _loginFlow

    private val _registerFlow = MutableStateFlow<Resource<String>?>(null)
    val registerFlow : StateFlow<Resource<String>?> = _registerFlow

    val userFlow = repository.getUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)


    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result = repository.login(email, password)
        _loginFlow.value = result
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _registerFlow.value = Resource.Loading
        val result = repository.register(name, email, password)
        _registerFlow.value = result
    }

    fun logout() = viewModelScope.launch {
        repository.logout()
        _loginFlow.value = null
        _registerFlow.value = null
    }
}