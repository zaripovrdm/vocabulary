package ru.zrd.vcblr.csv

import android.content.ContentResolver
import ru.zrd.vcblr.db.VocabularyEntry

class PhrasalVerbAdapter(resolver: ContentResolver) : VocabularyEntryAdapter(resolver) {

    override fun convert(line: List<String?>): List<VocabularyEntry> = listOf(
        VocabularyEntry(
            word = if (line[0].isNullOrBlank()) "" else line[0]!!,
            translation = line[1]!!,
            example = line[3],
            type = VocabularyEntry.Type.PVERB
        )
    )
}