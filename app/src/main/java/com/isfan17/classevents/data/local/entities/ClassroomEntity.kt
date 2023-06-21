package com.isfan17.classevents.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isfan17.classevents.data.model.Classroom
import com.isfan17.classevents.utils.Helper.convertNumberToDay

@Entity(tableName = "classrooms")
data class ClassroomEntity(
    @PrimaryKey
    var id: String,
    val name: String,
    val day: Int,
    val startTime: String,
    val endTime: String,
    val createdAt: String
)

fun List<ClassroomEntity>.asDomainModel(): List<Classroom> {
    return map {
        Classroom(
            id = it.id,
            name = it.name,
            day = convertNumberToDay(it.day),
            startTime = it.startTime,
            endTime = it.endTime,
            createdAt = it.createdAt
        )
    }
}

fun ClassroomEntity.asDomainModel() = Classroom(
    id = id,
    name = name,
    day = convertNumberToDay(day),
    startTime = startTime,
    endTime = endTime,
    createdAt = createdAt
)