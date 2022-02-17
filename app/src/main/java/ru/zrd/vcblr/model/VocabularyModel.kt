package ru.zrd.vcblr.model

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.TypedValue
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.zrd.vcblr.db.Db
import ru.zrd.vcblr.db.VocabularyEntry

class VocabularyModel(private val context: Context) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    enum class DisplayMode {
        SHOW_RUS,
        SHOW_ENG,
        SHOW_ALL;

        companion object {
            private val optionList = listOf(SHOW_RUS, SHOW_ENG)
            fun nextInitMode() = optionList.random()
        }
    }

    companion object {
        const val DEFAULT_COLOR: Int = android.R.color.secondary_text_dark
        const val LEARNED_COLOR: Int = android.R.color.holo_purple
    }

    private val db = Db.instance(context)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    private var currentMode = DisplayMode.nextInitMode()

    private val items = mutableListOf<VocabularyEntry>()
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

    private val _wordColor = MutableLiveData(DEFAULT_COLOR)
    val wordColor: LiveData<Int?> = _wordColor

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)
        refresh()
    }

    fun toggleLearned() {
        item.learned = !item.learned
        viewModelScope.launch(Dispatchers.IO) {
            db.dao().update(item)
        }
        _wordColor.value = if (item.learned) LEARNED_COLOR else DEFAULT_COLOR

        if (!preferences.getBoolean("show_all", true)) {
            items.remove(item) // always remove to avoid creating duplicates in the following if block
            if (!item.learned) {
                items.add(item)
            }
        }
    }


    fun next() = when (currentMode) {
        DisplayMode.SHOW_ALL -> {
            item = items.random()
            currentMode = DisplayMode.nextInitMode()

            _word.value = if (currentMode == DisplayMode.SHOW_ENG) item.word else item.translationRus
            _translation.value = null
            _translationEng.value = null
            _example.value = null
            _buttonText.value = "Check"
            _wordColor.value = DEFAULT_COLOR
        }
        else -> {
            _translation.value = if (currentMode == DisplayMode.SHOW_ENG) item.translationRus else item.word
            _translationEng.value = item.translationEng
            _example.value = item.example
            _buttonText.value = "Next"
            if (item.learned) {
                _wordColor.value = LEARNED_COLOR
            }

            currentMode = DisplayMode.SHOW_ALL
        }
    }

    fun addEntries(entries: List<VocabularyEntry>) {
        viewModelScope.launch(Dispatchers.IO) {
            db.dao().addAll(entries)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "${entries.size} entries have been successfully added", Toast.LENGTH_LONG).show()
                refresh()
            }
        }
    }

    override fun onSharedPreferenceChanged(p: SharedPreferences?, s: String?) = refresh()

    private fun refresh() {
        val activeTypes = mutableListOf<VocabularyEntry.Type>().apply {
            if (preferences.getBoolean("verb", true)) {
                add(VocabularyEntry.Type.VERB)
            }
            if (preferences.getBoolean("pverb", true)) {
                add(VocabularyEntry.Type.PVERB)
            }
            if (preferences.getBoolean("noun", true)) {
                add(VocabularyEntry.Type.NOUN)
            }
            if (preferences.getBoolean("idiom", true)) {
                add(VocabularyEntry.Type.IDIOM)
            }
        }
        val showAll = preferences.getBoolean("show_all", true)

        items.clear()
        viewModelScope.launch(Dispatchers.IO) {
            items.addAll(
                if (showAll) {
                    db.dao().listWithTypes(activeTypes)
                } else {
                    db.dao().listWithTypes(false, activeTypes)
                }
            )

            withContext(Dispatchers.Main) {
                if (items.isNotEmpty()) {
                    currentMode = DisplayMode.SHOW_ALL
                    next()
                } else {
                    // TODO add special screen or disable button
                    _word.value = if (activeTypes.isEmpty()) "ENABLE AT LEAST ONE WORD TYPE" else  "LOAD WORDS USING + MENU BUTTON"
                    _translation.value = null
                    _translationEng.value = null
                    _example.value = null
                }
            }
        }
    }
}