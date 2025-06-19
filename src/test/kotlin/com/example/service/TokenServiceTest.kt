package com.example.service

import com.example.model.entity.TokenEntity
import com.example.model.entity.UserEntity
import com.example.repository.TokenRepository
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.Instant
import java.util.*

@MicronautTest(environments = ["test"])
class TokenServiceTest {
    @Inject
    lateinit private var tokenService: TokenService

    var userService: UserService = mock(UserService::class.java)

    @Inject
    lateinit private var jwtTokenGenerator: JwtTokenGenerator

    var tokenRepository: TokenRepository = mock(TokenRepository::class.java)
    lateinit var token: TokenEntity
    lateinit var userEntity: UserEntity
    var now = Instant.now()
    var auth: Authentication = mock(Authentication::class.java)

    @BeforeEach
    fun setUp() {
        tokenService = TokenService()
        tokenService.tokenRepository = tokenRepository
        tokenService.jwtTokenGenerator = jwtTokenGenerator

        userEntity = UserEntity(
            id = 1L,
            username = "Username",
            password = "password",
            roles = listOf("ROLE_USER"),
            banned = false
        )
        token = TokenEntity(1, "123", userEntity, false, now)

        `when`(auth.name).thenReturn("user")
        `when`(auth.roles).thenReturn(listOf("ROLE_USER"))
    }

    @Test
    fun shouldGenerateAccesToken(){
        val accessToken = jwtTokenGenerator.generateToken(auth, 10).get()
        assertNotNull(accessToken)
    }

    @Test
    fun shouldGenerateRefreshToken(){
        var refreshToken = TokenEntity()
        refreshToken.refresh_token = "123"
        refreshToken.user = userEntity
        refreshToken.revoked = false
        refreshToken.dateCreated = now

        assertNotNull(refreshToken)
        assertEquals(token.refresh_token, refreshToken.refresh_token)
    }

    @Test
    fun shouldDeleteToken(){
        `when`(tokenRepository.save(any(TokenEntity::class.java))).thenReturn(token)
        tokenRepository.save(token)
        tokenService.deleteToken(token)
        verify(tokenRepository, times(1)).delete(token)
        assertEquals(Optional.empty<TokenEntity>(), tokenRepository.findTokenByRefreshToken(token.refresh_token!!))
    }

    @Test
    fun shouldGetTokenByRefreshToken(){
        tokenRepository.save(token)
        `when`(tokenRepository.findTokenByRefreshToken("123")).thenReturn(Optional.of(token))
        var findedToken = tokenService.getTokenByRefreshToken(token.refresh_token!!)
        verify(tokenRepository, times(1)).findTokenByRefreshToken(token.refresh_token!!)
        assertEquals(token, findedToken)
    }

    @Test
    fun shouldThrowExceptionIfTokenNotExist(){
        `when`(tokenRepository.findTokenByRefreshToken("123")).thenReturn(Optional.empty())
        val exception = assertThrows<RuntimeException>{tokenService.getTokenByRefreshToken(token.refresh_token!!)}
        assertEquals("Token Service: Unable to find refresh token", exception.message)
    }
}