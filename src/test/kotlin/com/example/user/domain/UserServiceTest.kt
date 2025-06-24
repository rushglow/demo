package com.example.user.domain

import com.example.user.infrastructure.persistance.UserMapper
import com.example.user.dto.RegDto
import com.example.user.dto.UserDto
import com.example.user.infrastructure.persistance.UserEntity
import com.example.user.infrastructure.persistance.UserRepository
import com.example.security.provider.PasswordEncoderProvider
import com.example.user.service.UserService
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.*

@MicronautTest(environments = ["test"])
class UserServiceTest {
    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var userMapper: UserMapper

    @Inject
    lateinit var passwordEncoder: PasswordEncoderProvider

    val userRepository: UserRepository = mock(UserRepository::class.java)

    lateinit var regDto: RegDto

    lateinit var userEntity: UserEntity

    lateinit var userDto: UserDto

    @BeforeEach
    fun setUp(){
        userService = UserService()
        userService.userRepository = userRepository
        userService.userMapper = userMapper
        userService.passwordEncoder = passwordEncoder

        regDto = RegDto("Username", "password")
        userEntity = UserEntity(
            id = 1L,
            username = "Username",
            password = "password",
            roles = listOf("ROLE_USER"),
            banned = false
        )
        userDto = UserDto(
            id = 1L,
            username = "Username",
            roles = listOf("ROLE_USER"),
            banned = false
        )
    }

    @Test
    fun shouldCreateUserIfNotExists() {

        `when`(userRepository.findByUsername("Username")).thenReturn(Optional.empty())
        `when`(userRepository.save(Mockito.any(UserEntity().javaClass))).thenReturn(userEntity)

        var result: UserDto = userService.createUser(regDto)

        assertEquals(userEntity.username, result.username)
        assertNotEquals(userEntity.password, result.password)
        Mockito.verify(userRepository).findByUsername("Username")
        Mockito.verify(userRepository).save(Mockito.any(UserEntity::class.java))
    }

    @Test
    fun shouldThrowExceptionIfUserIsNotFound() {
        `when`(userRepository.findByUsername("Username")).thenReturn(Optional.empty())

        val exception = assertThrows<RuntimeException>{userService.getUserByUsername("Username")}
        assertEquals("User with this username not found", exception.message)

        Mockito.verify(userRepository).findByUsername("Username")
    }

    @Test
    fun shouldThrowExceptionIfUserAlreadyExists() {
        `when`(userRepository.findByUsername("Username")).thenReturn(Optional.of(userEntity))

        val exception = assertThrows<RuntimeException>{userService.createUser(regDto)}
        assertEquals("User with this username already exists", exception.message)
        Mockito.verify(userRepository).findByUsername("Username")
    }

}