package com.isfan17.classevents.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isfan17.classevents.data.model.Event
import com.isfan17.classevents.utils.Helper.convertSortableDateToString

@Entity(tableName = "events")
data class EventEntity (
    @PrimaryKey
    var id: String,
    val classroom_id: String,
    val name: String,
    val date: Long,
    val time: String,
    val createdAt: String
)

fun List<EventEntity>.asDomainModel(): List<Event> {
    return map {
        Event(
            id = it.id,
            classroom_id = it.classroom_id,
            name = it.name,
            date = convertSortableDateToString(it.date),
            time = it.time,
            createdAt = it.createdAt
        )
    }
}

fun EventEntity.asDomainModel() = Event(
    id = id,
    classroom_id = classroom_id,
    name = name,
    date = convertSortableDateToString(date),
    time = time,
    createdAt = createdAt
)