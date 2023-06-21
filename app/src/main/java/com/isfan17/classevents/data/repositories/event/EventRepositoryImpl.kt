package com.isfan17.classevents.data.repositories.event

import com.google.firebase.firestore.FirebaseFirestore
import com.isfan17.classevents.data.local.ClassEventsDao
import com.isfan17.classevents.data.local.entities.asDomainModel
import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.data.model.asEntityModel
import com.isfan17.classevents.data.remote.FirebaseEvent
import com.isfan17.classevents.data.remote.asEntityModel
import com.isfan17.classevents.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val classEventsDao: ClassEventsDao
) : EventRepository {

    override suspend fun getEventsClassroom(
        classroomId: String,
        sortType: String
    ): Flow<Resource<List<Event>>> = flow {
        try {
            emit(Resource.Loading)

            val networkEvents = fetchFirebaseEvents(classroomId)
            Timber.d(networkEvents.toString())
            classEventsDao.clearEventsClassroom(classroomId)
            classEventsDao.insertEvents(networkEvents.asEntityModel())
        } catch (e: Exception) {
            emit(Resource.Failure(e.message.toString()))
        }

        val localEvents = when (sortType) {
            EVENT_SORT_NAME_ASC -> classEventsDao.getEventsClassroomByNameAsc(classroomId)
            EVENT_SORT_NAME_DESC -> classEventsDao.getEventsClassroomByNameDesc(classroomId)
            EVENT_SORT_TIME_ASC -> classEventsDao.getEventsClassroomByTimeAsc(classroomId)
            EVENT_SORT_TIME_DESC -> classEventsDao.getEventsClassroomByTimeDesc(classroomId)
            else -> { classEventsDao.getEventsClassroom(classroomId) }
        }
        Timber.d(localEvents.toString())

        emit(Resource.Success(localEvents.asDomainModel()))
    }

    private suspend fun fetchFirebaseEvents(classroomId: String): List<FirebaseEvent> {
        val result = database.collection(EVENT_DOC)
            .whereEqualTo(CLASSROOM_ID_FIELD, classroomId)
            .get()
            .await()
        val firebaseEvents = arrayListOf<FirebaseEvent>()
        for (snapshot in result) {
            val event = snapshot.toObject(FirebaseEvent::class.java)
            firebaseEvents.add(event)
        }
        return firebaseEvents
    }

    override suspend fun getEventsClassroomSearch(
        query: String,
        classroomId: String
    ): Flow<Resource<List<Event>>> = flow {
        emit(Resource.Loading)
        try {
            val events = classEventsDao.getEventsClassroomSearch(classroomId, query)
            emit(Resource.Success(events.asDomainModel()))
        } catch (e: Exception) {
            emit(Resource.Failure(e.message.toString()))
        }
    }

    override suspend fun getEvent(id: String) = flow {
        emit(Resource.Loading)
        try {
            val event = classEventsDao.getEvent(id)
            emit(Resource.Success(event.asDomainModel()))
        } catch (e: Exception) {
            emit(Resource.Failure(e.message.toString()))
        }
    }

    override suspend fun addEvent(event: Event): Resource<String> {
        return try {
            val firebaseEvent = FirebaseEvent(
                id = event.id,
                classroom_id = event.classroom_id,
                name = event.name,
                date = event.date,
                time = event.time
            )

            val document = database.collection(EVENT_DOC).document()
            firebaseEvent.id = document.id
            document.set(firebaseEvent).await()

            Resource.Success("Event has been added successfully")
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun updateEvent(event: Event): Resource<String> {
        return try {
            val firebaseEvent = FirebaseEvent(
                id = event.id,
                classroom_id = event.classroom_id,
                name = event.name,
                date = event.date,
                time = event.time
            )

            val document = database.collection(EVENT_DOC).document(event.id)
            document.set(firebaseEvent).await()

            classEventsDao.updateEvent(event.asEntityModel())

            Resource.Success("Event has been updated successfully")
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun deleteEvent(event: Event): Resource<String> {
        return try {
            val document = database.collection(EVENT_DOC).document(event.id)
            document.delete().await()

            Resource.Success("Event has been deleted successfully")
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }
}