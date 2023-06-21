package com.isfan17.classevents.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isfan17.classevents.data.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String
)

fun UserEntity.asDomainModel() = User(
    id = id,
    name = name,
    email = email
)
