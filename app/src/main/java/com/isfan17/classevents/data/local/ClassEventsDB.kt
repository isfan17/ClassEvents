package com.isfan17.classevents.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.isfan17.classevents.data.local.entities.ClassroomEntity
import com.isfan17.classevents.data.local.entities.EventEntity
import com.isfan17.classevents.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ClassroomEntity::class,
        EventEntity::class
    ],
    version = 1
)
abstract class ClassEventsDB: RoomDatabase() {
    abstract fun getClassEventsDao(): ClassEventsDao
}