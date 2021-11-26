package com.gurk0001.springboot.kotlin.jenkins

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootKotlinJenkinsApplication

fun main(args: Array<String>) {
    runApplication<SpringBootKotlinJenkinsApplication>(*args)
}
