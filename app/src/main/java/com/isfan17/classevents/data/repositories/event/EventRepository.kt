package com.isfan17.classevents.data.repositories.event

import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun getEventsClassroom(classroomId: String, sortType: String): Flow<Resource<List<Event>>>
    suspend fun getEventsClassroomSearch(query: String, classroomId: String): Flow<Resource<List<Event>>>
    suspend fun getEvent(id: String): Flow<Resource<Event>>

    suspend fun addEvent(event: Event): Resource<String>
    suspend fun updateEvent(event: Event): Resource<String>
    suspend fun deleteEvent(event: Event): Resource<String>
}