package com.isfan17.classevents.data.repositories.classroom

import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ClassroomRepository {
    suspend fun getClassrooms(sortType: String): Flow<Resource<List<Classroom>>>
    suspend fun getClassroomsSearch(query: String): Flow<Resource<List<Classroom>>>
    suspend fun getClassroom(id: String): Flow<Resource<Classroom>>

    suspend fun addClassroom(classroom: Classroom): Resource<String>
    suspend fun updateClassroom(classroom: Classroom): Resource<String>
    suspend fun deleteClassroom(classroom: Classroom): Resource<String>
}