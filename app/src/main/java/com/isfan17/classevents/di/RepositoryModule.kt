package com.isfan17.classevents.di

import com.isfan17.classevents.data.repositories.auth.AuthRepository
import com.isfan17.classevents.data.repositories.auth.AuthRepositoryImpl
import com.isfan17.classevents.data.repositories.classroom.ClassroomRepository
import com.isfan17.classevents.data.repositories.classroom.ClassroomRepositoryImpl
import com.isfan17.classevents.data.repositories.event.EventRepository
import com.isfan17.classevents.data.repositories.event.EventRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideClassroomRepository(impl: ClassroomRepositoryImpl): ClassroomRepository = impl

    @Provides
    @Singleton
    fun provideEventRepository(impl: EventRepositoryImpl): EventRepository = impl
}