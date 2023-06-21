package com.isfan17.classevents.data.remote

import com.google.firebase.firestore.ServerTimestamp
import com.isfan17.classevents.data.local.entities.ClassroomEntity
import com.isfan17.classevents.utils.Helper.convertDayToNumber
import java.util.*

data class FirebaseClassroom(
    var id: String = "",
    val user_id: String = "",
    val name: String = "",
    val day: String = "",
    val startTime: String = "",
    val endTime: String = "",
    @ServerTimestamp
    val createdAt: Date = Date()
)

fun List<FirebaseClassroom>.asEntityModel(): List<ClassroomEntity> {
    return map {
        ClassroomEntity(
            id = it.id,
            name = it.name,
            day = convertDayToNumber(it.day),
            startTime = it.startTime,
            endTime = it.endTime,
            createdAt = it.createdAt.toString()
        )
    }
}
