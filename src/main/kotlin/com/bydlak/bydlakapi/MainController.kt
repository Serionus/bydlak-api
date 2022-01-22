package com.bydlak.bydlakapi

import com.bydlak.bydlakapi.alarm.Alarm
import com.bydlak.bydlakapi.commons.security.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import org.springframework.web.bind.annotation.*

@RestController
class MainController{
    val users = mutableListOf<User>()

    @PostMapping("/login")
    fun login(@RequestParam userUID: String){
        users.add(FirebaseAuth.getInstance().getUser(userUID))
    }

    @PostMapping("/logout")
    fun logout(@RequestParam userUID: String){
        users.remove(FirebaseAuth.getInstance().getUser(userUID))
    }

    @GetMapping("/alarms")
    fun getAlarms(@RequestParam userUID: String): List<Alarm>{
        val user = users.find { it.uid == userUID }
        return user!!.alarms.filter { it.userUID == user.uid }
    }

    @PostMapping("/alarms")
    fun createAlarm(@RequestParam userUID: String, @RequestBody alarm: Alarm){
        val user = users.find { it.uid == userUID }
        user!!.alarms.add(alarm)
    }

    @PutMapping("/alarms")
    fun updateAlarm(@RequestParam userUID: String, @RequestBody alarm: Alarm){
        val user = users.find { it.uid == userUID }
        user!!.alarms.replaceAll { if(it.alarmId == alarm.alarmId) alarm else it }
    }

    @DeleteMapping("/alarms")
    fun removeAlarm(@RequestParam userUID: String, @RequestParam alarmId: Int){
        val user = users.find { it.uid == userUID }
        user!!.alarms.removeIf { it.alarmId == alarmId }
    }
}

private fun MutableList<User>.add(user: UserRecord) {
    this.add(User(user.uid, user.email))
}
private fun MutableList<User>.remove(user: UserRecord) {
    this.remove(User(user.uid, user.email))
}
