package com.example.security

import jakarta.inject.Singleton
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Singleton
class PasswordEncoderProvider : PasswordEncoder {
    private val delegate = BCryptPasswordEncoder()

    override fun encode(rawPassword: CharSequence): String = delegate.encode(rawPassword)
    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean =
        delegate.matches(rawPassword, encodedPassword)
}