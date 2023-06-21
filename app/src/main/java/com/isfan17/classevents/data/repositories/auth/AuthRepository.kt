package com.isfan17.classevents.data.repositories.auth

import com.isfan17.classevents.data.local.entities.UserEntity
import com.isfan17.classevents.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(email: String, password: String): Resource<String>
    suspend fun register(name: String, email: String, password: String): Resource<String>
    suspend fun logout()

    fun getUser(): Flow<UserEntity>
}