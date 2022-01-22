package com.bydlak.bydlakapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BydlakApiApplication

fun main(args: Array<String>) {
    println("dupa")
    runApplication<BydlakApiApplication>(*args)
}
