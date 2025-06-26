package com.example.core.security.provider

import com.example.actors.user.dto.UserDto
import com.example.actors.user.model.UserEntity
import com.example.core.security.token.service.TokenService
import com.example.actors.user.service.UserService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationFailureReason
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AuthenticationUserPasswordProvider<B> : HttpRequestAuthenticationProvider<B> {

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var tokenService: TokenService

    @Inject
    lateinit var passwordEncoderProvider: PasswordEncoderProvider

    override fun authenticate(
        httpRequest: HttpRequest<B>?,
        authenticationRequest: AuthenticationRequest<String, String>
    ): AuthenticationResponse {
            var user: UserEntity = userService.getUserByUsername(authenticationRequest.identity)
            if(passwordEncoderProvider.matches(authenticationRequest.secret.toString(), user.password!!)){
                return AuthenticationResponse.success(authenticationRequest.identity, listOf("ROLE_USER"))
            } else {
                return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH)
            }

    }
}