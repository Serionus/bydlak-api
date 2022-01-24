package com.bydlak.bydlakapi

import com.bydlak.bydlakapi.alarm.Alarm
import com.bydlak.bydlakapi.commons.security.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Service


@Service
class UserService {
    private val loggedUsers = mutableListOf<User>()

    fun getAllUsers() = loggedUsers

    fun clearServerData() = loggedUsers.clear()

    fun removeFromServer(userUID: String) = loggedUsers.removeIf { it.uid == userUID }

    fun addToServer(userUID: String): User? {
        val firestore = FirestoreClient.getFirestore(FirebaseApp.getInstance())
        val document = firestore.collection("users").document(userUID).get().get()
        val userFromDb: User? = if (document.exists()) {
            User(
                uid = document["uid"] as String,
                email = document["email"] as String,
                isParent = document["parent"] as Boolean,
                parentID = document["parentID"] as String
            )
        } else null

        return if (userFromDb != null) {
            if (loggedUsers.find { it.uid == userUID } == null) {
                loggedUsers.add(userFromDb)
            }
            userFromDb
        } else {
            throw UserNotFoundException()
        }
    }

    fun deleteUserAccount(userUID: String) {
        val user = loggedUsers.find { it.uid == userUID } ?: return
        val firebase = FirebaseAuth.getInstance()
        val firestore = FirestoreClient.getFirestore(FirebaseApp.getInstance())
        if (user.isParent) {
            loggedUsers.filter { it.parentID == userUID }.forEach {
                firebase.deleteUser(it.uid)
                firestore.collection("users").document(it.uid).delete()
            }
            loggedUsers.removeIf { it.parentID == userUID }
        }
        firebase.deleteUser(userUID)
        firestore.collection("users").document(userUID).delete()
        loggedUsers.removeIf { it.uid == userUID }
    }

    fun addChild(userUID: String, childEmail: String, childPassword: String) {
        val user = loggedUsers.find { it.uid == userUID } ?: return
        if (user.isParent) {
            val firebase = FirebaseAuth.getInstance()
            val firestore = FirestoreClient.getFirestore(FirebaseApp.getInstance())
            val childInstance =
                firebase.createUser(UserRecord.CreateRequest().setEmail(childEmail).setPassword(childPassword))
            val child = User(childInstance.uid, childEmail, false, userUID)
            firestore.collection("users").document(childInstance.uid).set(child)
            loggedUsers.add(child)
        } else {
            throw UserIsNotParentException()
        }
    }

    fun getParentChildren(userUID: String) = loggedUsers.filter { it.parentID == userUID }

    fun getUserAlarms(userUID: String): List<Alarm> = loggedUsers.getUser(userUID)!!.alarms

    fun createAlarm(userUID: String, alarm: Alarm) = loggedUsers.getUser(userUID)!!.alarms.add(alarm)

    fun updateAlarm(userUID: String, updatedAlarm: Alarm) =
        loggedUsers.getUser(userUID)!!.alarms.replaceAll { if (it.id == updatedAlarm.id) updatedAlarm else it }

    fun removeAlarm(userUID: String, alarmToBeRemovedID: Long) =
        loggedUsers.getUser(userUID)!!.alarms.removeIf { it.id == alarmToBeRemovedID }
}

class UserIsNotParentException : RuntimeException()
class UserNotFoundException : RuntimeException()

private fun MutableList<User>.getUser(userUID: String) = this.find { it.uid == userUID }