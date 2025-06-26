package com.example.core.controller

import com.example.actors.user.dto.RegDto
import com.example.core.security.token.dto.TokenPair
import com.example.actors.user.dto.UserDto
import com.example.core.security.token.repository.TokenRepository
import com.example.actors.user.repository.UserRepository
import com.example.testcontainers.TestDbContainer
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(environments = ["test"])
class HomeControllerTest: TestDbContainer() {
    @Client("/")
    @Inject
    lateinit var client: HttpClient

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    lateinit var userRepository: UserRepository

    @BeforeEach
    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
        tokenRepository.deleteAll()
    }

    @Test
    fun testAuthenticatedUser(){
        val regDto = RegDto("user", "password")
        val request = HttpRequest.POST("/user/registr", regDto)
        val userDto = client.toBlocking().retrieve(request, UserDto::class.java)

        val creds = UsernamePasswordCredentials("user", "password")
        val loginRequest = HttpRequest.POST("/user/login", creds)
        val tokenPair = client.toBlocking().retrieve(loginRequest, TokenPair::class.java)

        val authTestRq = HttpRequest.GET<String>("/authenticated").bearerAuth(tokenPair.accessToken)

        val authResponse = client.toBlocking().retrieve(authTestRq, String::class.java)

        assertNotNull(authResponse)
    }

    @Test
    fun testUnAuthenticatedUser(){
        val authTestRq = HttpRequest.GET<String>("/authenticated").bearerAuth("1123")

        val exception = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(authTestRq, Any::class.java)
        }

        assertEquals(HttpStatus.UNAUTHORIZED, exception.status)
    }
}