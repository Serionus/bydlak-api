package com.bydlak.bydlakapi.alarm

import java.time.LocalDateTime

data class Alarm(
    val id: Int,
    val title: String,
    val hour: LocalDateTime,
    val recurring: HashMap<Day, Boolean>
)
