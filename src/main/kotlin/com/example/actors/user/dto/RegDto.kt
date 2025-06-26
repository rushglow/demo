package com.example.actors.user.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.NotBlank

@Serdeable
@Introspected
data class RegDto(
    var username:String,

    var password:String
) {

}