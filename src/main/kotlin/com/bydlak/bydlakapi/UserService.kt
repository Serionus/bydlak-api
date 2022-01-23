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
    private val existingUsers = mutableListOf<User>()

    fun getAllUsers() = existingUsers

    fun clearServerData() = existingUsers.clear()

    fun removeFromServer(userUID: String) = existingUsers.removeIf { it.uid == userUID }

    fun addToServer(userUID: String): User? {
        val firestore = FirestoreClient.getFirestore(FirebaseApp.getInstance())
        val document = firestore.collection("users").document(userUID).get().get()
        val userFromDb: User? = if (document.exists()) {
            User(
                uid = document["uid"] as String,
                email = document["email"] as String,
                isParent = document["isParent"] as Boolean,
                parentID = document["parentID"] as String
            )
        } else null

        return if (userFromDb != null) {
            if (existingUsers.find { it.uid == userUID } == null) {
                existingUsers.add(userFromDb)
            }
            userFromDb
        } else {
            throw UserNotFoundException()
        }
    }

    fun deleteUserAccount(userUID: String) {
        val user = existingUsers.find { it.uid == userUID } ?: return
        val firebase = FirebaseAuth.getInstance()
        val firestore = FirestoreClient.getFirestore(FirebaseApp.getInstance())
        if (user.isParent) {
            existingUsers.filter { it.parentID == userUID }.forEach {
                firebase.deleteUser(it.uid)
                firestore.collection("users").document(it.uid).delete()
            }
            existingUsers.removeIf { it.parentID == userUID }
        }
        firebase.deleteUser(userUID)
        firestore.collection("users").document(userUID).delete()
        existingUsers.removeIf { it.uid == userUID }
    }

    fun addChild(userUID: String, childEmail: String, childPassword: String) {
        val user = existingUsers.find { it.uid == userUID } ?: return
        if (user.isParent) {
            val firebase = FirebaseAuth.getInstance()
            val firestore = FirestoreClient.getFirestore(FirebaseApp.getInstance())
            val childInstance =
                firebase.createUser(UserRecord.CreateRequest().setEmail(childEmail).setPassword(childPassword))
            val child = User(childInstance.uid, childEmail, false, userUID)
            firestore.collection("users").document(childInstance.uid).set(child)
            existingUsers.add(child)
        } else {
            throw UserIsNotParentException()
        }
    }

    fun getParentChildren(userUID: String) = existingUsers.filter { it.parentID == userUID }

    fun getUserAlarms(userUID: String): List<Alarm> = existingUsers.getUser(userUID)!!.alarms

    fun createAlarm(userUID: String, alarm: Alarm) {
        existingUsers.getUser(userUID)!!.alarms.add(alarm)
    }

    fun updateAlarm(userUID: String, updatedAlarm: Alarm) =
        existingUsers.getUser(userUID)!!.alarms.replaceAll { if (it.alarmId == updatedAlarm.alarmId) updatedAlarm else it }

    fun removeAlarm(userUID: String, alarmToBeRemovedID: Int) =
        existingUsers.getUser(userUID)!!.alarms.removeIf { it.alarmId == alarmToBeRemovedID }
}

class UserIsNotParentException : RuntimeException()
class UserNotFoundException : RuntimeException()

private fun MutableList<User>.getUser(userUID: String) = this.find { it.uid == userUID }