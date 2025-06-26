package com.example.actors.user.repository

import com.example.actors.user.model.UserEntity
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): Optional<UserEntity>

    @Query("update UserEntity u set u.token = null WHERE u.id = :userId")
    fun deleteTokenByUserId(userId: Long)
}