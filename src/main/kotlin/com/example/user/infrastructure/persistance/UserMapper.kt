package com.example.user.infrastructure.persistance

import com.example.user.dto.RegDto
import com.example.user.dto.UserDto
import jakarta.inject.Singleton

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