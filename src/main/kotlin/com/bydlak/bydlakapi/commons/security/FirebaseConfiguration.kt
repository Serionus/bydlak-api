package com.bydlak.bydlakapi.commons.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfiguration(@Value("\${fb.accountKey}") val accountKey: String) {

//    @PostConstruct
//    fun init() {
//        FirebaseApp.initializeApp(
//            FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(accountKey.byteInputStream())).build()
//        )
//    }
}