package com.isfan17.classevents.utils

sealed class Resource<out R> {
    data class Success<out R>(val data: R): Resource<R>()
    data class Failure(val error: String?): Resource<Nothing>()
    object Loading: Resource<Nothing>()
}