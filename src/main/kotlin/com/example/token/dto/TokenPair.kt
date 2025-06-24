package com.example.token.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable
data class TokenPair(
    var accessToken: String,
    var refreshToken: String
) {
}