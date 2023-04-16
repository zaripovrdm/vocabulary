package ru.zrd.vcblr.csv

import android.content.ContentResolver
import ru.zrd.vcblr.db.VocabularyEntry

class MiscellaneousAdapter(resolver: ContentResolver) : VocabularyEntryAdapter(resolver) {

    override fun convert(line: List<String?>): List<VocabularyEntry> {
        val entry = VocabularyEntry(
            word = line[0]!!,
            translation = line[1]!!,
            lang = VocabularyEntry.Lang.valueOf(line[2]!!.uppercase()),
            type = when(line[3]) {
                "v" -> VocabularyEntry.Type.VERB
                "n" -> VocabularyEntry.Type.NOUN
                "aj" -> VocabularyEntry.Type.ADJECTIVE
                "av" -> VocabularyEntry.Type.ADVERB
                else -> VocabularyEntry.Type.MISC
            }
        )

        return listOf(entry)
    }
}