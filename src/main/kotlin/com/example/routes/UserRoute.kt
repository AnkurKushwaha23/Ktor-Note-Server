package com.example.routes

import com.example.auth.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.UserRepo
import com.example.utils.hash
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

fun Application.configureUserRouting(
    db: UserRepo
) {
    routing {
        post(REGISTER_REQUEST) {
            val requestBody = try {
                call.receive<RegisterRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing fields"))
                return@post
            }

            try {
                if (db.isEmailExist(requestBody.email)) {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "Email Already Register"))
                } else {
                    val user = User(
                        email = requestBody.email,
                        name = requestBody.name,
                        hashPassword = hash(requestBody.password)
                    )
                    db.registerUser(user)
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, JwtService.createToken(user)))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred"))
                return@post
            }
        }

        post(LOGIN_REQUEST) {
            val loginRequest = try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing fields"))
                return@post
            }

            try {
                val user = db.findUserByEmail(loginRequest.email)
                if (user == null) {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "Wrong Email Id"))
                } else {
                    if (user.hashPassword == hash(loginRequest.password)) {
                        call.respond(HttpStatusCode.OK, SimpleResponse(true, JwtService.createToken(user)))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, SimpleResponse(false, "Incorrect Password!"))
                    }
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred"))
                return@post
            }
        }
    }
}
