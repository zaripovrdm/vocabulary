package ru.zrd.vcblr.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(
    tableName = "vocabulary",
    indices = [Index(value = ["word", "type", "lang"], unique = true)]
)
data class VocabularyEntry(

    @PrimaryKey val id: Int? = null,

    var word: String, // word to be translated. Language of the word is #lang
    val translation: String, // word translation. can be on any language. Generally English or Russian
    // Full word description, generally 1-2 sentences. English or Russian
    // It is supplementary info, not for learning
    val description: String? = null,
    val example: String? = null, // Sentence that shows typical word usage. Uses the same #land as the #word
    val type: Type = Type.MISC,
    val lang: Lang = Lang.EN,
    var learned: Boolean = false
) {

    enum class Type { VERB, NOUN, ADJECTIVE, ADVERB, PVERB, IDIOM, MISC }

    enum class Lang { EN, FR }

    class TypeConverters {

        @TypeConverter
        fun fromType(value: Type?): String? = value?.toString()

        @TypeConverter
        fun toType(value: String?): Type? = if (value != null) Type.valueOf(value) else Type.MISC

        @TypeConverter
        fun fromLang(value: Lang?): String? = value?.toString()

        @TypeConverter
        fun toLang(value: String?): Lang? = if (value != null) Lang.valueOf(value) else Lang.EN
    }
}
