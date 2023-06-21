package com.isfan17.classevents.data.repositories.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun putClassroomSort(key: String, value: String)
    fun getClassroomSort(key: String): Flow<String>

    suspend fun putEventSort(key: String, value: String)
    fun getEventSort(key: String): Flow<String>

    suspend fun clearPreferences(key: String)
}