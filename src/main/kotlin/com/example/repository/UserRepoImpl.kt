package com.example.repository

import com.example.data.model.Note
import com.example.data.model.User
import com.example.data.table.NoteTable
import com.example.data.table.UserTable
import com.example.db.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserRepoImpl : UserRepo {
    // Auth
    override suspend fun registerUser(user: User) {
        dbQuery {
            UserTable.insert { userTable ->
                userTable[UserTable.email] = user.email
                userTable[UserTable.name] = user.name
                userTable[UserTable.hashPassword] = user.hashPassword
            }
        }
    }

    override suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.selectAll().where { UserTable.email.eq(email) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun isEmailExist(email: String): Boolean {
        return findUserByEmail(email) != null
    }

    private fun rowToUser(row: ResultRow?): User? {
        return if (row == null) null
        else User(
            email = row[UserTable.email],
            name = row[UserTable.name],
            hashPassword = row[UserTable.hashPassword]
        )
    }

    //Note
    override suspend fun addNote(note: Note, email: String) {
        dbQuery {
            NoteTable.insert { nt ->
                nt[NoteTable.id] = note.id
                nt[NoteTable.userEmail] = email
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }
        }
    }

    override suspend fun updateNote(note: Note, email: String) {
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) { nt ->
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }
        }
    }

    override suspend fun getAllNotes(email: String): List<Note> = dbQuery {
        NoteTable.selectAll().where { NoteTable.userEmail.eq(email) }
            .mapNotNull { rowToNote(it) }
    }

    override suspend fun deleteNote(id: String, email: String) {
        dbQuery {
            NoteTable.deleteWhere {
                NoteTable.userEmail.eq(email) and NoteTable.id.eq(id)
            }
        }
    }

    private fun rowToNote(row: ResultRow?): Note? {
        return if (row == null) null
        else Note(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )
    }
}