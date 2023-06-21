package com.isfan17.classevents.data.remote

import com.google.firebase.firestore.ServerTimestamp
import com.isfan17.classevents.data.local.entities.EventEntity
import com.isfan17.classevents.utils.Helper.convertStringToSortableDate
import java.util.*

data class FirebaseEvent (
    var id: String = "",
    val classroom_id: String = "",
    val name: String = "",
    val date: String = "",
    val time: String = "",
    @ServerTimestamp
    val createdAt: Date = Date()
)

fun List<FirebaseEvent>.asEntityModel(): List<EventEntity> {
    return map {
        EventEntity(
            id = it.id,
            classroom_id = it.classroom_id,
            name = it.name,
            date = convertStringToSortableDate(it.date),
            time = it.time,
            createdAt = it.createdAt.toString()
        )
    }
}