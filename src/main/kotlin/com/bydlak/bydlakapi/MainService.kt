package com.bydlak.bydlakapi

import com.bydlak.bydlakapi.alarm.Alarm
import com.bydlak.bydlakapi.commons.security.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Service

@Service
class MainService {
    private val loggedUsers = mutableListOf<User>()

    fun login(userUID: String) : User? {
        val firestore = FirestoreClient.getFirestore(FirebaseApp.getInstance())
        val data = firestore.collection("users").listDocuments()
        val userFromDb = data.find { it.get().get()["uid"] == userUID }!!.get().get().toObject(User::class.java)
        return if (userFromDb != null) {
            loggedUsers.add(userFromDb)
            userFromDb
        } else throw UserNotFoundException()
    }

    fun logout(userUID: String) {
        loggedUsers.removeIf { it.uid == userUID }
    }

    fun addChild(userUID: String, childEmail: String, childPassword: String) {
        val firebase = FirebaseAuth.getInstance()
        val user = loggedUsers.find { it.uid == userUID }
        if (user!!.parent) {
            firebase.createUser(UserRecord.CreateRequest().setEmail(childEmail).setPassword(childPassword))
        } else {
            throw UserIsNotParentException()
        }
    }

    fun getUserAlarms(userUID: String): List<Alarm> =
        loggedUsers.getUser(userUID)!!.alarms

    fun createAlarmForUser(userUID: String, alarm: Alarm) {
        loggedUsers.getUser(userUID)!!.alarms.add(alarm)
    }

    fun updateUserAlarm(userUID: String, updatedAlarm: Alarm) =
        loggedUsers.getUser(userUID)!!.alarms.replaceAll { if (it.alarmId == updatedAlarm.alarmId) updatedAlarm else it }

    fun removeUserAlarm(userUID: String, alarmToBeUpdatedID: Int) =
        loggedUsers.getUser(userUID)!!.alarms.removeIf { it.alarmId == alarmToBeUpdatedID }
}

class UserIsNotParentException : RuntimeException()
class UserNotFoundException : RuntimeException()

private fun MutableList<User>.getUser(userUID: String) = this.find { it.uid == userUID }

