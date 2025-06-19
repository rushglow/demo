package com.example.mapper

import com.example.model.dto.RegDto
import com.example.model.dto.UserDto
import com.example.model.entity.UserEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Singleton
class UserMapper {
    fun toUserEntity(regDto: RegDto): UserEntity {
        var user = UserEntity()
        user.username = regDto.username
        user.password = regDto.password
        user.roles = listOf("ROLE_USER")
        user.banned = false
        return user
    }

    fun toUserDto(userEntity: UserEntity): UserDto {
        var userDto = UserDto()
        userDto.id = userEntity.id
        userDto.username = userEntity.username
        userDto.password = userEntity.password
        userDto.roles = userEntity.roles
        userDto.banned = userEntity.banned
        return userDto
    }
}