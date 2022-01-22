package com.bydlak.bydlakapi.alarm

import java.time.LocalTime

data class Alarm(
    val userUID: String,
    val alarmId: Int,
    val time: LocalTime,
    val days: List<Day>,
    val recurringDays: List<Boolean>
)
