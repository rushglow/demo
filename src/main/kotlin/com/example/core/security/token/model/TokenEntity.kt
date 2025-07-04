package com.example.core.security.token.model

import com.example.actors.user.model.UserEntity
import io.micronaut.data.annotation.DateCreated
import jakarta.persistence.*
import java.time.Instant

@Entity
class TokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long? = null,

    var refresh_token: String? = null,

    @OneToOne(mappedBy = "token")
    var user: UserEntity? = null,

    var revoked: Boolean? = null,

    @DateCreated
    var dateCreated: Instant? = null
) {
}