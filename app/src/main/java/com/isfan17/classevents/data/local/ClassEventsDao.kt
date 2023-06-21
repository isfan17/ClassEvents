package com.isfan17.classevents.data.local

import androidx.room.*
import com.isfan17.classevents.data.local.entities.ClassroomEntity
import com.isfan17.classevents.data.local.entities.EventEntity
import com.isfan17.classevents.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassEventsDao {

    /**
     * CRUD USERS
     */
    @Query("SELECT * FROM users")
    fun getUser(): Flow<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearUser()

    /**
     * CRUD CLASSROOMS
     */
    // Classrooms List
    @Query("SELECT * FROM classrooms")
    fun getClassrooms(): List<ClassroomEntity>

    @Query("SELECT * FROM classrooms ORDER BY name ASC")
    fun getClassroomsByNameAsc(): List<ClassroomEntity>

    @Query("SELECT * FROM classrooms ORDER BY name DESC")
    fun getClassroomsByNameDesc(): List<ClassroomEntity>

    @Query("SELECT * FROM classrooms ORDER BY day ASC, CAST(startTime AS INTEGER) ASC, CAST(endTime AS INTEGER) ASC")
    fun getClassroomsByDayTimeAsc(): List<ClassroomEntity>

    @Query("SELECT * FROM classrooms ORDER BY day DESC, CAST(startTime AS INTEGER) DESC, CAST(endTime AS INTEGER) DESC")
    fun getClassroomsByDayTimeDesc(): List<ClassroomEntity>

    // Classrooms Search
    @Query("SELECT * FROM classrooms WHERE name LIKE '%' || :query || '%'")
    fun getClassroomsSearch(query: String): List<ClassroomEntity>

    // Classroom data
    @Query("SELECT * FROM classrooms WHERE id = :id LIMIT 1")
    fun getClassroom(id: String): ClassroomEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassrooms(classrooms: List<ClassroomEntity>)

    @Update
    suspend fun updateClassroom(classroom: ClassroomEntity)

    @Query("DELETE FROM classrooms")
    suspend fun clearClassrooms()

    /**
     * CRUD EVENTS
     */
    // Events Classroom List
    @Query("SELECT * FROM events WHERE classroom_id = :classroomId")
    fun getEventsClassroom(classroomId: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE classroom_id = :classroomId ORDER BY name ASC")
    fun getEventsClassroomByNameAsc(classroomId: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE classroom_id = :classroomId ORDER BY name DESC")
    fun getEventsClassroomByNameDesc(classroomId: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE classroom_id = :classroomId ORDER BY date ASC, CAST(time AS INTEGER) ASC")
    fun getEventsClassroomByTimeAsc(classroomId: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE classroom_id = :classroomId ORDER BY date DESC, CAST(time AS INTEGER) DESC")
    fun getEventsClassroomByTimeDesc(classroomId: String): List<EventEntity>

    // Events Search
    @Query("SELECT * FROM events WHERE classroom_id = :classroomId AND name LIKE '%' || :query || '%'")
    fun getEventsClassroomSearch(classroomId: String, query: String): List<EventEntity>

    // Event Data
    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    fun getEvent(id: String): EventEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE classroom_id = :classroomId")
    suspend fun clearEventsClassroom(classroomId: String)

}