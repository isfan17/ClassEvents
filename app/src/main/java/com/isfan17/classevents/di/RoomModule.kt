package com.isfan17.classevents.di

import android.content.Context
import androidx.room.Room
import com.isfan17.classevents.data.local.ClassEventsDB
import com.isfan17.classevents.utils.ROOM_DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomModule {

    @Singleton
    @Provides
    fun provideClassEventsDB(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ClassEventsDB::class.java,
        ROOM_DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideClassEventsDao(db: ClassEventsDB) = db.getClassEventsDao()
}