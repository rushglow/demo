package com.example.testcontainers

import io.micronaut.test.support.TestPropertyProvider
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class TestDbContainer : TestPropertyProvider {

    companion object {
        @Container
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16.0").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            withReuse(true)
            start()
        }
    }

    override fun getProperties(): Map<String, String> {
        return mapOf(
            "datasources.default.url" to postgres.jdbcUrl,
            "datasources.default.username" to postgres.username,
            "datasources.default.password" to postgres.password,
            "datasources.default.driver-class-name" to "org.postgresql.Driver"
        )
    }
}