package com.isfan17.classevents.data.repositories.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.isfan17.classevents.data.local.ClassEventsDao
import com.isfan17.classevents.data.local.entities.UserEntity
import com.isfan17.classevents.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val classEventsDao: ClassEventsDao
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Resource<String> {
        return try {
            // Try login to firebaseAuth
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            // Store firebaseUser result in local db
            val objUser = UserEntity(
                id = result.user!!.uid,
                name = result.user!!.displayName!!,
                email = result.user!!.email!!
            )
            classEventsDao.insertUser(objUser)

            Resource.Success("Login Successful")
        } catch (e: Exception) {
            Resource.Failure(e.message)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<String> {
        return try {
            // Try register to firebaseAuth
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()

            // Store firebaseUser result in local db
            val objUser = UserEntity(
                id = result.user!!.uid,
                name = result.user!!.displayName!!,
                email = result.user!!.email!!
            )
            classEventsDao.insertUser(objUser)

            Resource.Success("Account Created Successfully")
        } catch (e: Exception) {
            Resource.Failure(e.message)
        }
    }

    override suspend fun logout() {
        classEventsDao.clearUser()
        firebaseAuth.signOut()
    }

    override fun getUser() = classEventsDao.getUser()
}