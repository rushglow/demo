package com.example.core.security.token.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable
@Introspected
data class RefreshTokenRq (
    var refreshToken: String
){
}