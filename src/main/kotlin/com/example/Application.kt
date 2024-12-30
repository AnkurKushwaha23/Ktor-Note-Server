package com.example

import com.example.auth.configureSecurity
import com.example.db.DatabaseFactory
import com.example.di.configureKoin
import com.example.repository.UserRepo
import com.example.repository.UserRepoImpl
import com.example.routes.configureNoteRouting
import com.example.routes.configureUserRouting
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()

    configureKoin()

    val db: UserRepo by inject()

    install(ContentNegotiation) {
        jackson()
    }

    configureSecurity()
    configureNoteRouting(db)
    configureUserRouting(db)
}
