package ru.zrd.vcblr.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.zrd.vcblr.R
import ru.zrd.vcblr.csv.VerbAdapter
import ru.zrd.vcblr.db.Db
import ru.zrd.vcblr.db.VocabularyEntry
import kotlin.random.Random

class VocabularyModel(private val db: Db) : ViewModel() {

    private val items = mutableListOf<VocabularyEntry>()
    private var showAll = true

    private var showEng = true
    private lateinit var item: VocabularyEntry

    private val _word = MutableLiveData<String>()
    val word: LiveData<String> = _word

    private val _translation = MutableLiveData<String?>()
    val translation: LiveData<String?> = _translation

    private val _translationEng = MutableLiveData<String?>()
    val translationEng: LiveData<String?> = _translationEng

    private val _example = MutableLiveData<String?>()
    val example: LiveData<String?> = _example

    private val _buttonText = MutableLiveData<String?>()
    val buttonText: LiveData<String?> = _buttonText


    fun next() {
        if (showAll) {
            item = items.random()
            showEng = Random.nextInt(1, 3) == 1

            _word.postValue(if (showEng) item.word else item.translationRus)
            _translation.postValue(null)
            _translationEng.postValue(null)
            _example.postValue(null)
            _buttonText.postValue("Check")

            showAll = false
        } else {
            _translation.postValue(if (showEng) item.translationRus else item.word)
            _translationEng.postValue(item.translationEng)
            _example.postValue(item.example)
            _buttonText.postValue("Next")

            showAll = true
        }
    }


    fun isEmpty() = items.isEmpty()

    fun addEntries(entries: List<VocabularyEntry>) {
        db.dao().addAll(entries)
    }

    fun refresh(showLearnt: Boolean, types: List<VocabularyEntry.Type>) {
        items.clear()
        showAll = true
        showEng = true

        items.addAll(
            if (showLearnt) {
                db.dao().listWithTypes(types)
            } else {
                db.dao().listWithTypes(false, types)
            }
        )

        if (!isEmpty()) {
            next()
        } else {
            _word.postValue("NO WORDS FOUND")
            _translation.postValue(null)
            _translationEng.postValue(null)
            _example.postValue(null)
        }
    }
}