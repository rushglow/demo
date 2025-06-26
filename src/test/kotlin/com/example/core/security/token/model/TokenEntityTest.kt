package com.example.core.security.token.model


import com.example.actors.user.model.UserEntity
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(environments = ["test"])
class TokenEntityTest {
    lateinit var user: UserEntity
    lateinit var token: TokenEntity
    lateinit var instant: Instant

    @BeforeAll
    fun setUp() {
        instant = Instant.now()
        user = UserEntity()
        token = TokenEntity(0, "123", user, false, instant)
    }

    @Test
    fun tokenEntityTest(){
        assertEquals(0, token.id)
        assertEquals("123", token.refresh_token)
        assertEquals(user, token.user)
        assertEquals(false, token.revoked)
        assertEquals(instant, token.dateCreated)
    }
}