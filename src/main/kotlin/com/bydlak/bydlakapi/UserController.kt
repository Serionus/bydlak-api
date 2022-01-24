package com.bydlak.bydlakapi

import com.bydlak.bydlakapi.alarm.Alarm
import com.bydlak.bydlakapi.commons.security.User
import org.springframework.web.bind.annotation.*

@RestController
class UserController(private val userService: UserService) {

    @GetMapping("/users")
    fun getUsers(): MutableList<User> = userService.getAllUsers()
    // /all because of conflict between two mappings on same endpoint
    @DeleteMapping("/users/all")
    fun clearUsers() = userService.clearServerData()

    @PostMapping("/users")
    fun addUser(@RequestParam userUID: String): User? = userService.addToServer(userUID)

    @DeleteMapping("/users")
    fun removeUser(@RequestParam userUID: String) = userService.removeFromServer(userUID)

    @DeleteMapping("/accounts")
    fun deleteAccount(@RequestParam userUID: String) = userService.deleteUserAccount(userUID)

    @PostMapping("/children")
    fun addChild(@RequestParam userUID: String, @RequestParam childEmail: String, @RequestParam childPassword: String) =
        userService.addChild(userUID, childEmail, childPassword)

    @GetMapping("/children")
    fun getChildren(@RequestParam userUID: String) = userService.getParentChildren(userUID)

    @GetMapping("/alarms")
    fun getAlarms(@RequestParam userUID: String): List<Alarm> = userService.getUserAlarms(userUID)

    @PostMapping("/alarms")
    fun createAlarm(@RequestParam userUID: String, @RequestBody alarm: Alarm) =
        userService.createAlarm(userUID, alarm)

    @PutMapping("/alarms")
    fun updateAlarm(@RequestParam userUID: String, @RequestBody alarm: Alarm) =
        userService.updateAlarm(userUID, alarm)

    @DeleteMapping("/alarms")
    fun removeAlarm(@RequestParam userUID: String, @RequestParam alarmId: Long) =
        userService.removeAlarm(userUID, alarmId)
}
