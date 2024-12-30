package com.example.db

import com.example.data.table.NoteTable
import com.example.data.table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

object DatabaseFactory {

    fun init(){
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NoteTable)
        }
    }

    private fun hikari(): HikariDataSource {
//        val uri = URI(System.getenv("PSDB_URL"))
//        val username = uri.userInfo.split(":")[0]
//        val password = uri.userInfo.split(":")[1]
//
//        val database = uri.path.substring(1)
//
//        val jdbcURL =
//            "jdbc:postgresql://${uri.host}:${uri.port}/$database?sslmode=require&user=$username&password=$password"


        val config = HikariConfig().apply {
            driverClassName = System.getenv("JDBC_DRIVER") // In production, use environment variables
            jdbcUrl = System.getenv("DB_URL") // In production, use environment variables
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block:()->T): T = withContext(Dispatchers.IO){
        transaction {
            block()
        }
    }
}