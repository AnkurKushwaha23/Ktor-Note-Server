package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.User

class JwtService {

    companion object {
        val SECRET = System.getenv("SECRET")// In production, use environment variables
        const val ISSUER = "noteServer"
        const val AUDIENCE = "note-service"
        const val REALM = "Note Server"

        val verifier: JWTVerifier = JWT.require(Algorithm.HMAC512(SECRET))
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .build()

        fun createToken(user: User): String {
            return JWT.create()
                .withAudience(AUDIENCE)
                .withSubject(user.email)
                .withIssuer(ISSUER)
                .withClaim("email", user.email)
                .withClaim("name", user.name)
//                .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET))
        }
    }
}