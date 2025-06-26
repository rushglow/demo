package com.example.actors.user.mapper

import com.example.actors.user.dto.RegDto
import com.example.actors.user.dto.UserDto
import com.example.actors.user.model.UserEntity
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