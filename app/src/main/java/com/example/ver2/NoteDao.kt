package com.example.ver2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {

    @Insert
    suspend fun insertNote(note: Note)

    @Query (value = "SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY id DESC")
    suspend fun getNotesByCategory(category: String): List<Note>

    @Query("SELECT DISTINCT category FROM notes ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>
}