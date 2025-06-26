package com.example.actors.user.repository

import com.example.core.security.token.model.TokenEntity
import com.example.actors.user.model.UserEntity
import com.example.testcontainers.TestDbContainer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*


@MicronautTest(environments = ["test"])
class UserRepositoryTest: TestDbContainer() {
    @Inject
    lateinit var userRepository : UserRepository
    lateinit var user: UserEntity
    lateinit var token: TokenEntity

    @BeforeEach
    fun setUp() {
        token = TokenEntity(null, "123")
        user = UserEntity(null, "User", "password", listOf("ROLE_USER"), token,false)
    }

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun addUserInDatabase() {
        var createdUser = userRepository.save(user)
        assertNotNull(createdUser)
        assertNotNull(createdUser.id)
        assertEquals(createdUser, user)
    }

    @Test
    fun findUserById() {
        var createdUser = userRepository.save(user)
        var findedUser = userRepository.findById(createdUser.id).get()
        assertNotNull(findedUser)
        assertEquals(findedUser, createdUser)
    }

    @Test
    fun findByUsername() {
        var createdUser = userRepository.save(user)
        var findedUser = userRepository.findByUsername(createdUser.username!!)
        assertNotNull(findedUser)
        assertEquals(findedUser.get(), createdUser)
    }

    @Test
    fun dontFindByUsername() {
        var findedUser = userRepository.findByUsername("Username")
        assertTrue(findedUser.isEmpty)
    }

    @Test
    fun dontFindUserById() {
        var findedUser = userRepository.findByUsername("Username")
        assertTrue(findedUser.isEmpty)
    }

    @Test
    fun updateUser() {
        var createdUser = userRepository.save(user)
        createdUser.username = "Updated User"
        createdUser.banned = true
        var updatedUser = userRepository.save(createdUser)
        assertNotNull(updatedUser)
        assertEquals("Updated User", updatedUser.username)
        assertEquals(true, updatedUser.banned)
    }
}