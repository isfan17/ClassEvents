package com.isfan17.classevents.data.model

import android.os.Parcelable
import com.isfan17.classevents.data.local.entities.EventEntity
import com.isfan17.classevents.utils.Helper.convertStringToSortableDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    var id: String,
    val classroom_id: String,
    val name: String,
    val date: String,
    val time: String,
    val createdAt: String? = ""
): Parcelable

fun Event.asEntityModel() = EventEntity(
    id = id,
    classroom_id = classroom_id,
    name = name,
    date = convertStringToSortableDate(date),
    time = time,
    createdAt = createdAt ?: ""
)