package com.isfan17.classevents.data.model

import android.os.Parcelable
import com.isfan17.classevents.data.local.entities.ClassroomEntity
import com.isfan17.classevents.utils.Helper.convertDayToNumber
import kotlinx.parcelize.Parcelize

@Parcelize
data class Classroom(
    var id: String,
    val name: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val createdAt: String? = ""
): Parcelable

fun Classroom.asEntityModel() = ClassroomEntity(
    id = id,
    name = name,
    day = convertDayToNumber(day),
    startTime = startTime,
    endTime = endTime,
    createdAt = createdAt ?: ""
)