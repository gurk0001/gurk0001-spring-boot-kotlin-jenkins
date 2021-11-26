package com.gurk0001.springboot.kotlin.jenkins.controller

import com.gurk0001.springboot.kotlin.jenkins.services.MyService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant

@WebFluxTest
internal class HomeControllerTest {

    @MockBean
    private lateinit var myService: MyService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun testHomeController() {

        Mockito.`when`(myService.getCurrentDateInSeconds()).thenReturn(Instant.now().epochSecond)

        var res = webTestClient.get()
            .uri("/api/v1/home")
            .exchange().expectStatus().isOk
            .expectBody(String::class.java)
            .value {
                res1 ->
                assert(res1 != null) {
                    "result cannot be blank or empty"
                }
                assert(res1.toLong() >= 0)
            }

        verify(myService, times(1)).getCurrentDateInSeconds()
    }


}