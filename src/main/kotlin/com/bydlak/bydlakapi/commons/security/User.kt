package com.bydlak.bydlakapi.commons.security

import com.bydlak.bydlakapi.alarm.Alarm

data class User(val uid: String, val email: String, val parent: Boolean) {
    val children: MutableList<String>? = if (parent) mutableListOf() else null
    val parentID: MutableList<String>? = if (parent) null else mutableListOf()
    val alarms: MutableList<Alarm> = mutableListOf()
}
