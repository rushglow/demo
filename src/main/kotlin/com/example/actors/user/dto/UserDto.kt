package com.example.actors.user.dto

import com.example.core.security.token.model.TokenEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable
data class UserDto(
    var id: Long? = null,

    var username:String? = null,

    @JsonIgnore
    var password:String? = null,

    var roles:List<String>? = null,

    @JsonIgnore
    var token: TokenEntity? = null,

    var banned:Boolean? = null
) {
}