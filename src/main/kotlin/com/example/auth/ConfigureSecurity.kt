package com.example.auth

import com.example.auth.JwtService.Companion.REALM
import com.example.data.model.SimpleResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {

    install(Authentication) {
        jwt("jwt") {
            realm = REALM
            verifier(JwtService.verifier)
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, SimpleResponse(false, "Invalid or expired token."))
            }
        }
    }
}