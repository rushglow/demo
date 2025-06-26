package com.example.core.security.token.repository

import com.example.core.security.token.model.TokenEntity
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface TokenRepository: JpaRepository<TokenEntity, Long> {
    @Query("select te from TokenEntity te where te.refresh_token = :refreshToken")
    fun findTokenByRefreshToken(refreshToken: String): Optional<TokenEntity>
}