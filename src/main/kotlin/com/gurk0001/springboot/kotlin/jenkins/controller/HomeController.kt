package com.gurk0001.springboot.kotlin.jenkins.controller

import com.gurk0001.springboot.kotlin.jenkins.services.MyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("api/v1")
class HomeController(private val service: MyService) {

    @GetMapping("/home")
    fun home(): Mono<ResponseEntity<String>> {
        return Mono.just(ResponseEntity.ok(service.getCurrentDateInSeconds().toString()))
    }
}