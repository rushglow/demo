package com.example.controller

import com.example.model.dto.RefreshTokenRq
import com.example.model.dto.RegDto
import com.example.model.dto.TokenPair
import com.example.model.dto.UserDto
import com.example.service.TokenService
import com.example.service.UserService
import com.sun.security.auth.UserPrincipal
import example.micronaut.AuthenticationProviderUserPassword
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal

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