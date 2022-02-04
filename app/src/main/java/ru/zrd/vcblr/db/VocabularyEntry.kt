package ru.zrd.vcblr.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "vocabulary")
data class VocabularyEntry(
    @PrimaryKey val id: Int? = null,
    var word: String,
    val translationRus: String,
    val translationEng: String? = null,
    val example: String? = null,
    val type: Type,
    var learned: Boolean = false
) {

    enum class Type { VERB, NOUN, PVERB, IDIOM }

    class TypeConverters {

        @TypeConverter
        fun fromType(value: Type?): String? = value?.toString()

        @TypeConverter
        fun toType(value: String?): Type? = if (value != null) Type.valueOf(value) else null
    }
}
