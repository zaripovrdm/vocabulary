package ru.zrd.vcblr.csv

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import org.supercsv.io.CsvListReader
import org.supercsv.prefs.CsvPreference
import ru.zrd.vcblr.db.VocabularyEntry
import java.io.InputStreamReader

abstract class VocabularyEntryAdapter(private val resolver: ContentResolver) {

    fun entries(uri: Uri): List<VocabularyEntry> = resolver.openInputStream(uri).use { it ->
        InputStreamReader(it).use { reader ->
            val csvReader = CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE)

            val list = mutableListOf<VocabularyEntry>()
            var line = csvReader.read()
            var prevValue: VocabularyEntry? = null

            while (line != null) {
                val entries = convert(line)

                prevValue?.let {
                    entries.forEach { entry ->
                        if (entry.word.isEmpty()) {
                            entry.word = prevValue!!.word
                        }
                    }
                }

                prevValue = entries.first()

                list.addAll(entries)
                line = csvReader.read()
            }
            return list
        }
    }

    protected abstract fun convert(line: List<String?>): List<VocabularyEntry>
}