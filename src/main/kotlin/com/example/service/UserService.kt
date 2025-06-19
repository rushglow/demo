package com.example.service

import com.example.mapper.UserMapper
import com.example.model.dto.RegDto
import com.example.model.dto.TokenPair
import com.example.model.dto.UserDto
import com.example.model.entity.TokenEntity
import com.example.model.entity.UserEntity
import com.example.repository.UserRepository
import com.example.security.PasswordEncoderProvider
import example.micronaut.AuthenticationProviderUserPassword
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.validator.RefreshTokenValidator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.springframework.security.crypto.password.PasswordEncoder

@Singleton
class UserService(

) {
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var userMapper: UserMapper
    @Inject
    lateinit var passwordEncoder: PasswordEncoderProvider
    @Inject
    lateinit var tokenService: TokenService

    fun createUser(regDto: RegDto): UserDto {
        userRepository.findByUsername(regDto.username).ifPresent({
            throw RuntimeException("User with this username already exists")
        })
        regDto.password = passwordEncoder.encode(regDto.password)
        return userMapper.toUserDto(userRepository.save(userMapper.toUserEntity(regDto)))
    }

    fun getUserByUsername(username: String): UserDto {
        return userMapper.toUserDto(userRepository.findByUsername(username).orElseThrow {
            throw RuntimeException("User with this username not found")
        })
    }

    fun login(auth: Authentication): TokenPair {
        var user = userRepository.findByUsername(auth.name).orElseThrow {throw RuntimeException("User Service: User with this username not found")}
        if (user.banned == true) {
            throw RuntimeException("User Service: User has been banned")
        }
        if (user.token != null) {
            deleteUserToken(user.id!!)
            tokenService.deleteToken(user.token!!)
        }
        var accessToken: String = tokenService.generateAccessToken(auth)
        var refreshToken: TokenEntity = tokenService.generateRefreshToken(user)

        user.token = refreshToken
        userRepository.update(user)

        return TokenPair(accessToken, refreshToken.refresh_token!!)
    }

    fun deleteUserToken(userId: Long){
        userRepository.deleteTokenByUserId(userId)
    }

    fun refreshUserToken(refreshToken: String): TokenPair {
        var tokenEntity = tokenService.getTokenByRefreshToken(refreshToken)
        var user = tokenEntity.user!!
        if (user.banned == true) {
            throw RuntimeException("User Service: User has been banned")
        }
        var authentication: Authentication = Authentication.build(user.username, user.roles)
        var accessToken = tokenService.generateAccessToken(authentication)
        return TokenPair(accessToken, refreshToken)
    }

    fun banUser(id:Long): UserDto{
        var bannedUser = userRepository.findById(id).orElseThrow{throw RuntimeException("UserService: User not found")}
        bannedUser.banned = true
        userRepository.update(bannedUser)
        return userMapper.toUserDto(bannedUser)
    }


}