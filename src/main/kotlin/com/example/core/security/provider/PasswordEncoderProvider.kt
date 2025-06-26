package com.example.core.security.provider

import jakarta.inject.Singleton
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Singleton
class PasswordEncoderProvider : PasswordEncoder {

    override fun encode(rawPassword: CharSequence): String {
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
    }
    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }
}