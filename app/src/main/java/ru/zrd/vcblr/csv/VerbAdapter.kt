package ru.zrd.vcblr.csv

import android.content.ContentResolver
import android.content.res.Resources
import ru.zrd.vcblr.db.VocabularyEntry

class VerbAdapter(resolver: ContentResolver) : VocabularyEntryAdapter(resolver) {

    override fun convert(line: List<String?>): List<VocabularyEntry> {

        val verb = VocabularyEntry(
            word = if (line[0].isNullOrBlank()) "" else line[0]!!,
            translation = line[1]!!,
            description = if (line[2].isNullOrBlank()) null else line[2],
            type = VocabularyEntry.Type.VERB
        )

        return if (!line[3].isNullOrBlank()) {
            val noun = VocabularyEntry(
                word = line[3]!!,
                translation = line[4]!!,
                type = VocabularyEntry.Type.VERB
            )
            listOf(verb, noun)
        } else {
            listOf(verb)
        }
    }
}