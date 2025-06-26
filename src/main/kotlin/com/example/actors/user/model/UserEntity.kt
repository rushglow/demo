package com.example.actors.user.model

import com.example.core.security.token.model.TokenEntity
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "users")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotNull
    var username: String? = null,

    var password: String? = null,

    var roles: List<String>? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "token_id")
    var token: TokenEntity? = null,

    var banned: Boolean = false
) {
}