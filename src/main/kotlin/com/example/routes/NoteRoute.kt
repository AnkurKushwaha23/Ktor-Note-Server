package com.example.routes

import com.example.data.model.Note
import com.example.data.model.SimpleResponse
import com.example.repository.UserRepo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val DELETE_NOTES = "$NOTES/delete"


fun Application.configureNoteRouting(
    db: UserRepo
) {
    routing {
        authenticate("jwt") {
            post(CREATE_NOTES) {
                val note = try {
                    call.receive<Note>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing fields"))
                    return@post
                }

                try {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal?.getClaim("email", String::class)!!
                    db.addNote(note, email)
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note added successfully."))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Conflict Occurred"))
                }
            }

            get(NOTES) {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal?.getClaim("email", String::class)!!
                    val notes = db.getAllNotes(email)
                    call.respond(HttpStatusCode.OK, notes)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, emptyList<Note>())
                }
            }

            put(CREATE_NOTES) {
                val note = try {
                    call.receive<Note>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing fields"))
                    return@put
                }

                try {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal?.getClaim("email", String::class)!!
                    db.updateNote(note, email)
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note updated successfully."))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Conflict Occurred"))
                }
            }

            delete(DELETE_NOTES) {
                val noteId = try {
                    call.request.queryParameters["id"]!!
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing fields"))
                    return@delete
                }

                try {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal?.getClaim("email", String::class)!!
                    db.deleteNote(noteId, email)
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note deleted successfully."))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Conflict Occurred"))
                }
            }
        }
    }
}
