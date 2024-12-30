package com.example.di

import com.example.repository.UserRepo
import com.example.repository.UserRepoImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(module {
            factory<UserRepo> { UserRepoImpl() }
        })
    }
}