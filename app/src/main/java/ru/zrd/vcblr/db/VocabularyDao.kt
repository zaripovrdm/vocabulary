package ru.zrd.vcblr.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary")
    fun listAll(): List<VocabularyEntry>

    @Query("SELECT * FROM vocabulary WHERE type IN (:types)")
    fun listWithTypes(types: List<VocabularyEntry.Type>): List<VocabularyEntry>

    @Query("SELECT * FROM vocabulary WHERE type IN (:types) AND learned = :learned")
    fun listWithTypes(learned: Boolean, types: List<VocabularyEntry.Type>): List<VocabularyEntry>

    @Insert
    fun addAll(entries: List<VocabularyEntry>)

    @Update
    fun update(entry: VocabularyEntry)
}