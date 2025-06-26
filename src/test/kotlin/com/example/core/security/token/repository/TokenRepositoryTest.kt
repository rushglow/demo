package com.example.core.security.token.repository

import com.example.core.security.token.model.TokenEntity
import com.example.actors.user.model.UserEntity
import com.example.testcontainers.TestDbContainer
import com.example.core.security.token.repository.TokenRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

@MicronautTest(environments = ["test"])
class TokenRepositoryTest: TestDbContainer() {
    @Inject
    lateinit var tokenRepository : TokenRepository
    lateinit var user: UserEntity
    lateinit var token: TokenEntity

    @BeforeEach
    fun setUp() {
        token = TokenEntity(null, "123", null, false, Instant.now())
        user = UserEntity(null, "User", "password", listOf("ROLE_USER"), token,false)
        token.user = user
    }

    @AfterEach
    fun tearDown() {
        tokenRepository.deleteAll()
    }

    @Test
    fun addTokenInDatabase() {
        var addedToken = tokenRepository.save(token)
        assertNotNull(addedToken)
        assertNotNull(addedToken.id)
        assertEquals(addedToken, token)
    }

    @Test
    fun deleteTokenById(){
        var addedToken = tokenRepository.save(token)
        tokenRepository.deleteById(addedToken.id)
        assertTrue(tokenRepository.findById(addedToken.id).isEmpty)
    }

    @Test
    fun deleteToken(){
        var addedToken = tokenRepository.save(token)
        tokenRepository.delete(addedToken)
        assertTrue(tokenRepository.findById(addedToken.id).isEmpty)
    }

    @Test
    fun updateTokenById(){
        var addedToken = tokenRepository.save(token)
        var updatedToken = addedToken
        updatedToken.refresh_token = "321"
        tokenRepository.save(updatedToken)
        assertEquals("321", tokenRepository.findById(updatedToken.id).get().refresh_token)
    }

    @Test
    fun findTokenByRefreshToken(){
        var addedToken = tokenRepository.save(token)
        var findedToken = tokenRepository.findTokenByRefreshToken(token.refresh_token!!)
        assertNotNull(findedToken)
        assertEquals(addedToken, findedToken.get())
    }

    @Test
    fun dontFindTokenByRefreshToken(){
        var findedToken = tokenRepository.findTokenByRefreshToken(token.refresh_token!!)
        assertTrue(findedToken.isEmpty)
    }

}