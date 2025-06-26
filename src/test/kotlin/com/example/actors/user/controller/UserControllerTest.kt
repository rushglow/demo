package com.example.actors.user.controller;

import com.example.core.security.token.dto.RefreshTokenRq
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows

@MicronautTest(environments = ["test"])
class UserControllerTest: TestDbContainer() {

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
    fun testRegisterAndLoginUser() {

        val regDto = RegDto("user", "password")
        val request = HttpRequest.POST("/user/registration", regDto)
        val userDto = client.toBlocking().retrieve(request, UserDto::class.java)
        assertEquals("user",userDto.username)


        val creds = UsernamePasswordCredentials("user", "password")
        val loginRequest = HttpRequest.POST("/user/login", creds)
        val tokenPair = client.toBlocking().retrieve(loginRequest, TokenPair::class.java)

        assertTrue(tokenPair.accessToken.isNotBlank())
        assertTrue(tokenPair.refreshToken.isNotBlank())
    }

    @Test
    fun testRegisterUserWithBlank() {
        val regDto = RegDto("", "")
        val request = HttpRequest.POST("/user/registration", regDto)
        val exception = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().retrieve(request, String::class.java)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
        println("Error body: ${exception.response.body()}")
    }

    @Test
    fun testLoginUserWithBlank() {
        val creds = UsernamePasswordCredentials("", "")
        val loginRequest = HttpRequest.POST("/user/login", creds)
        val exception = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().retrieve(loginRequest, String::class.java)
        }

        assertEquals(HttpStatus.UNAUTHORIZED, exception.status)
        println("Error body: ${exception.response.body()}")
    }

    @Test
    fun testRefreshToken(){
        val regDto = RegDto("user", "password")
        val request = HttpRequest.POST("/user/registration", regDto)
        val userDto = client.toBlocking().retrieve(request, UserDto::class.java)

        val creds = UsernamePasswordCredentials("user", "password")
        val loginRequest = HttpRequest.POST("/user/login", creds)
        val tokenPair = client.toBlocking().retrieve(loginRequest, TokenPair::class.java)

        val originAccess = tokenPair.accessToken

        Thread.sleep(1000) // Я не знаю почему так происходит, но если слишком быстро отправлять запросы на рефреш то новый токен не генерируется

        val refreshToken = RefreshTokenRq(tokenPair.refreshToken)
        val refreshRequest = HttpRequest.POST("/user/refresh", refreshToken)
        val tokenPairRefreshRq = client.toBlocking().retrieve(refreshRequest, TokenPair::class.java)

        val newAccess = tokenPairRefreshRq.accessToken

        assertNotEquals(originAccess, newAccess)
    }

    @Test
    fun testBanUser(){
        val regDto = RegDto("user", "password")
        val request = HttpRequest.POST("/user/registration", regDto)
        val userDto = client.toBlocking().retrieve(request, UserDto::class.java)

        val response = client.toBlocking().exchange(
            HttpRequest.PATCH("/user/${userDto.id}", null),
            UserDto::class.java
        ).body()
        assertNotEquals(userDto.banned, response.banned)
    }

    @Test
    fun testRefreshTokenForBannedUser(){
        val regDto = RegDto("user", "password")
        val request = HttpRequest.POST("/user/registration", regDto)
        val userDto = client.toBlocking().retrieve(request, UserDto::class.java)

        val creds = UsernamePasswordCredentials("user", "password")
        val loginRequest = HttpRequest.POST("/user/login", creds)
        val tokenPair = client.toBlocking().retrieve(loginRequest, TokenPair::class.java)

        val response = client.toBlocking().exchange(
            HttpRequest.PATCH("/user/${userDto.id}", null),
            UserDto::class.java
        ).body()

        Thread.sleep(300) // Я не знаю почему так происходит, но если слишком быстро отправлять запросы на рефреш то новый токен не генерируется

        val refreshToken = RefreshTokenRq(tokenPair.refreshToken)
        val refreshRequest = HttpRequest.POST("/user/refresh", refreshToken)

        val exception = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(refreshRequest, TokenPair::class.java)
        }
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.status)
    }

}
