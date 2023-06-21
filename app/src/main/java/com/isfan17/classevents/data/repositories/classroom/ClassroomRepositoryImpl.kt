package com.isfan17.classevents.data.repositories.classroom

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isfan17.classevents.data.local.ClassEventsDao
import com.isfan17.classevents.data.local.entities.asDomainModel
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.data.model.asEntityModel
import com.isfan17.classevents.data.remote.FirebaseClassroom
import com.isfan17.classevents.data.remote.asEntityModel
import com.isfan17.classevents.utils.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassroomRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val classEventsDao: ClassEventsDao
) : ClassroomRepository {

    override suspend fun getClassrooms(
        sortType: String
    ): Flow<Resource<List<Classroom>>> = flow {
        try {
            emit(Resource.Loading)

            val networkClassrooms = fetchFirebaseClassrooms()
            classEventsDao.clearClassrooms()
            classEventsDao.insertClassrooms(networkClassrooms.asEntityModel())
        } catch (e: Exception) {
            emit(Resource.Failure(e.message.toString()))
        }

        val localClassrooms = when (sortType) {
            CLASSROOM_SORT_NAME_ASC -> classEventsDao.getClassroomsByNameAsc()
            CLASSROOM_SORT_NAME_DESC -> classEventsDao.getClassroomsByNameDesc()
            CLASSROOM_SORT_DAYTIME_ASC -> classEventsDao.getClassroomsByDayTimeAsc()
            CLASSROOM_SORT_DAYTIME_DESC -> classEventsDao.getClassroomsByDayTimeDesc()
            else -> { classEventsDao.getClassrooms() }
        }

        emit(Resource.Success(localClassrooms.asDomainModel()))
    }

    override suspend fun getClassroomsSearch(query: String): Flow<Resource<List<Classroom>>> = flow {
        emit(Resource.Loading)
        try {
            val classrooms = classEventsDao.getClassroomsSearch(query)
            emit(Resource.Success(classrooms.asDomainModel()))
        } catch (e: Exception) {
            emit(Resource.Failure(e.message.toString()))
        }
    }

    private suspend fun fetchFirebaseClassrooms(): List<FirebaseClassroom> {
        val result = database.collection(CLASSROOM_DOC)
            .whereEqualTo(USER_ID_FIELD, firebaseAuth.currentUser!!.uid)
            .get()
            .await()
        val firebaseClassrooms = arrayListOf<FirebaseClassroom>()
        for (snapshot in result) {
            val classroom = snapshot.toObject(FirebaseClassroom::class.java)
            firebaseClassrooms.add(classroom)
        }
        return firebaseClassrooms
    }

    override suspend fun getClassroom(id: String) = flow {
        emit(Resource.Loading)
        try {
            val classroom = classEventsDao.getClassroom(id)
            emit(Resource.Success(classroom.asDomainModel()))
        } catch (e: Exception) {
            emit(Resource.Failure(e.message.toString()))
        }
    }

    override suspend fun addClassroom(classroom: Classroom): Resource<String> {
        return try {
            val firebaseClassroom = FirebaseClassroom(
                id= classroom.id,
                user_id= firebaseAuth.currentUser!!.uid,
                name= classroom.name,
                day= classroom.day,
                startTime= classroom.startTime,
                endTime= classroom.endTime,
            )

            val document = database.collection(CLASSROOM_DOC).document()
            firebaseClassroom.id = document.id
            document.set(firebaseClassroom).await()

            Resource.Success("Class has been added successfully")
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun updateClassroom(classroom: Classroom): Resource<String> {
        return try {
            val firebaseClassroom = FirebaseClassroom(
                id= classroom.id,
                user_id= firebaseAuth.currentUser!!.uid,
                name= classroom.name,
                day= classroom.day,
                startTime= classroom.startTime,
                endTime= classroom.endTime,
            )

            val document = database.collection(CLASSROOM_DOC).document(classroom.id)
            document.set(firebaseClassroom).await()

            classEventsDao.updateClassroom(classroom.asEntityModel())

            Resource.Success("Class has been updated successfully")
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun deleteClassroom(classroom: Classroom): Resource<String> {
        return try {
            val classroomDocument = database.collection(CLASSROOM_DOC).document(classroom.id)
            classroomDocument.delete().await()

            Resource.Success("Class has been deleted successfully")
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }
}