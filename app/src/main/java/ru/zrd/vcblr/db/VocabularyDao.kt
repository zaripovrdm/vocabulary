package ru.zrd.vcblr.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary")
    fun listAll(): List<VocabularyEntry>

    @Query("SELECT * FROM vocabulary WHERE type IN (:types) AND lang in (:langs)")
    fun listWithTypes(types: List<VocabularyEntry.Type>, langs: List<VocabularyEntry.Lang>): List<VocabularyEntry>

    @Query("UPDATE vocabulary SET learned = 0 WHERE type IN (:types) AND lang in (:langs)")
    fun resetLearned(types: List<VocabularyEntry.Type>, langs: List<VocabularyEntry.Lang>): Int

    @Query("SELECT * FROM vocabulary WHERE type IN (:types)  AND lang in (:langs) AND learned = :learned")
    fun listWithTypes(learned: Boolean, types: List<VocabularyEntry.Type>, langs: List<VocabularyEntry.Lang>): List<VocabularyEntry>

    @Insert
    fun addAll(entries: List<VocabularyEntry>)

    @Update
    fun update(entry: VocabularyEntry)
}