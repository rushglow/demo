package com.example.actors.user.controller

import com.example.core.security.token.dto.RefreshTokenRq
import com.example.core.security.token.dto.TokenPair
import com.example.actors.user.service.UserService
import com.example.actors.user.dto.RegDto
import com.example.actors.user.dto.UserDto
import com.example.core.security.provider.AuthenticationUserPasswordProvider
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/user")
class UserController {
    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var authenticationProvider: AuthenticationUserPasswordProvider<Any>

    @Post("/registration")
    fun reg(@Body regDto: RegDto): UserDto {
       return userService.createUser(regDto)
    }

    @Post("/login")
    fun login(@Body creds: UsernamePasswordCredentials): TokenPair {
        val authResponse = authenticationProvider.authenticate(null, creds).authentication.orElseThrow({throw AuthenticationException("User Controller: Unauthenticated") })
        return userService.login(authResponse)
    }

    @Post("/refresh")
    fun refresh(@Body refreshToken: RefreshTokenRq): TokenPair {
        return userService.refreshUserToken(refreshToken.refreshToken)
    }

    @Patch("/{id}")
    fun banUser(@PathVariable id: Long): UserDto {
        return userService.banUser(id)
    }
}