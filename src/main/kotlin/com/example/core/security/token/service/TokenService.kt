package com.example.core.security.token.service

import com.example.core.security.token.generator.CustomJwtTokenGenerator
import com.example.core.security.token.model.TokenEntity
import com.example.core.security.token.repository.TokenRepository
import com.example.actors.user.model.UserEntity
import io.micronaut.security.authentication.Authentication
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.Instant
import java.util.*

@Singleton
class TokenService {
    @Inject
    lateinit var jwtTokenGenerator: CustomJwtTokenGenerator

    @Inject
    lateinit var tokenRepository: TokenRepository

    fun generateAccessToken(auth: Authentication): String {

        val accessToken = jwtTokenGenerator.generateToken(auth,  100).orElseThrow {
            RuntimeException("Token Service: Unable to generate access token")
        }

        return accessToken
    }

    fun generateRefreshToken(user: UserEntity): TokenEntity {
        var refreshToken = TokenEntity()
        refreshToken.refresh_token = UUID.randomUUID().toString()
        refreshToken.user = user
        refreshToken.revoked = false
        refreshToken.dateCreated = Instant.now()

        return refreshToken
    }

    fun deleteToken(tokenEntity: TokenEntity) {
        tokenRepository.delete(tokenEntity)
    }
//
//    fun refreshAccessToken(refreshTokenRq: String): TokenEntity {
//        var tokenEntity = tokenRepository.findTokenByRefreshToken(refreshTokenRq).orElseThrow {
//            RuntimeException("Token Service: Unable to find refresh token")
//        }
//        if (tokenEntity.revoked!! == true) {throw RuntimeException("Token Service: User has been banned")}
//        var auth = Authentication.build(tokenEntity.user!!.username!!)
//        tokenEntity.access_token = jwtTokenGenerator.generateToken(auth, 10).orElseThrow {
//            RuntimeException("Token Service: Unable to generate access token")
//        }
//        return tokenRepository.save(tokenEntity)
//    }
//
    fun getTokenByRefreshToken(refreshTokenRq: String): TokenEntity {
        return tokenRepository.findTokenByRefreshToken(refreshTokenRq).orElseThrow {
            RuntimeException("Token Service: Unable to find refresh token")
        }
    }
//
//    fun getTokenByUsername(username: String): Optional<TokenEntity> {
//        return tokenRepository.findTokenByUsername(username)
//    }
}