package com.example.repository

import com.example.data.model.Note
import com.example.data.model.User

interface UserRepo {
    suspend fun registerUser(user: User):Unit
    suspend fun findUserByEmail(email: String) : User?
    suspend fun isEmailExist(email: String): Boolean
    suspend fun addNote(note: Note, email: String):Unit
    suspend fun updateNote(note: Note, email: String):Unit
    suspend fun getAllNotes(email: String): List<Note>
    suspend fun deleteNote(id: String, email: String):Unit
}