package com.isfan17.classevents.data.repositories.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.isfan17.classevents.utils.DATASTORE_NAME
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.isfan17.classevents.utils.CLASSROOM_SORT_DEFAULT
import com.isfan17.classevents.utils.EVENT_SORT_DEFAULT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

class DataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {

    override suspend fun putClassroomSort(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[prefKey] = value
        }
    }

    override fun getClassroomSort(key: String): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey] ?: CLASSROOM_SORT_DEFAULT
        }
    }

    override suspend fun putEventSort(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[prefKey] = value
        }
    }

    override fun getEventSort(key: String): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey] ?: EVENT_SORT_DEFAULT
        }
    }

    override suspend fun clearPreferences(key: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit {
            if (it.contains(prefKey)) {
                it.remove(prefKey)
            }
        }
    }
}