package com.example.security.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType.TEXT_PLAIN
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import java.security.Principal

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/authenticated")
class HomeController {

    @Produces(TEXT_PLAIN)
    @Get
    fun index(authentication: Authentication): String {
        return (authentication.name + " : " + authentication.roles + " : " + authentication.attributes)
    }
}