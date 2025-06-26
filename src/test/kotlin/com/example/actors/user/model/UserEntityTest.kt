package com.example.actors.user.model

import com.example.core.security.token.model.TokenEntity
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@MicronautTest(environments = ["test"])
class UserEntityTest {

    lateinit var user: UserEntity
    lateinit var token: TokenEntity

    @BeforeAll
    fun setUp() {
        token = TokenEntity()
        user = UserEntity(0, "User", "password", listOf("ROLE_USER"), token, false)
    }

    @Test
    fun testUser() {
        assertEquals(0, user.id)
        assertEquals("User", user.username)
        assertEquals("password", user.password)
        assertEquals(listOf("ROLE_USER"), user.roles)
        assertEquals(token, user.token)
        assertEquals(false, user.banned)
    }
}
