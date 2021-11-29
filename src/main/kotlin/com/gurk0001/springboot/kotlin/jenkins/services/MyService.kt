package com.gurk0001.springboot.kotlin.jenkins.services

import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MyService {

    fun getCurrentDateInSeconds():Long{
        val a:com.gurk0001.dto.Company5
        return Instant.now().epochSecond
    }
}