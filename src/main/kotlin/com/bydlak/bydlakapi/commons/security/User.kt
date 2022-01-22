package com.bydlak.bydlakapi.commons.security

import com.bydlak.bydlakapi.alarm.Alarm

data class User(val uid: String, val email: String){
    val alarms: MutableList<Alarm> = mutableListOf()
}
