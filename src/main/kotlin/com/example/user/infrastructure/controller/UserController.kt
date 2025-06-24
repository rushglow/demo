package com.example.user.infrastructure.controller

import com.example.token.dto.RefreshTokenRq
import com.example.token.dto.TokenPair
import com.example.user.service.UserService
import com.example.user.dto.RegDto
import com.example.user.dto.UserDto
import com.example.security.provider.AuthenticationProviderUserPassword
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
    lateinit var authenticationProvider: AuthenticationProviderUserPassword<Any>

    @Post("/registr")
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