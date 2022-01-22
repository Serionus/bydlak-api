package com.bydlak.bydlakapi

import com.bydlak.bydlakapi.alarm.Alarm
import org.springframework.web.bind.annotation.*

@RestController
class MainController(private val service: MainService) {

    @PostMapping("/login")
    fun login(@RequestParam userUID: String) = service.login(userUID)

    @PostMapping("/logout")
    fun logout(@RequestParam userUID: String) = service.logout(userUID)

    @PostMapping("/children")
    fun addChild(@RequestParam userUID: String, @RequestParam childEmail: String, @RequestParam childPassword: String) =
        service.addChild(userUID, childEmail, childPassword)

    @GetMapping("/alarms")
    fun getAlarms(@RequestParam userUID: String): List<Alarm> = service.getUserAlarms(userUID)

    @PostMapping("/alarms")
    fun createAlarm(@RequestParam userUID: String, @RequestBody alarm: Alarm) =
        service.createAlarmForUser(userUID, alarm)

    @PutMapping("/alarms")
    fun updateAlarm(@RequestParam userUID: String, @RequestBody alarm: Alarm) =
        service.updateUserAlarm(userUID, alarm)

    @DeleteMapping("/alarms")
    fun removeAlarm(@RequestParam userUID: String, @RequestParam alarmId: Int) =
        service.removeUserAlarm(userUID, alarmId)
}
