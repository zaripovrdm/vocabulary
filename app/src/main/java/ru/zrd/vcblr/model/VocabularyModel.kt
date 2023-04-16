package ru.zrd.vcblr.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.zrd.vcblr.db.Db
import ru.zrd.vcblr.db.VocabularyEntry
import kotlin.random.Random

class VocabularyModel(app: Application) : AndroidViewModel(app), SharedPreferences.OnSharedPreferenceChangeListener {

    enum class DisplayMode {
        // first step - word is displayed either in original or translated form
        TRANSLATION,
        WORD,
        // second step - show both translation and original word
        ALL;

        companion object {
            private val optionList = listOf(TRANSLATION, WORD)
            fun nextRandomInitMode() = optionList[(System.currentTimeMillis() % 2).toInt()]
        }
    }

    companion object {
        const val DEFAULT_COLOR: Int = android.R.color.secondary_text_dark
        const val LEARNED_COLOR: Int = android.R.color.holo_purple
    }

    private val context: Context = app.applicationContext

    private val db = Db.instance(context)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)


    private val items = mutableListOf<VocabularyEntry>()
    private lateinit var item: VocabularyEntry

    private var currentDisplayMode: DisplayMode = DisplayMode.ALL
    private var initDisplayMode: DisplayMode? = null

    private val _word = MutableLiveData<String>()
    val word: LiveData<String> = _word

    private val _translation = MutableLiveData<String?>()
    val translation: LiveData<String?> = _translation

    private val _description = MutableLiveData<String?>()
    val description: LiveData<String?> = _description

    private val _example = MutableLiveData<String?>()
    val example: LiveData<String?> = _example

    private val _buttonText = MutableLiveData<String?>()
    val buttonText: LiveData<String?> = _buttonText

    private val _wordColor = MutableLiveData(DEFAULT_COLOR)
    val wordColor: LiveData<Int?> = _wordColor

    private val _direction = MutableLiveData<String?>()
    val direction: LiveData<String?> = _direction

    private val _type = MutableLiveData<String?>()
    val type: LiveData<String?> = _type

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

    fun next() = when (currentDisplayMode) {
        DisplayMode.ALL -> {
            item = items[(System.currentTimeMillis() % items.size).toInt()]
            currentDisplayMode = initDisplayMode ?: DisplayMode.nextRandomInitMode()

            if (currentDisplayMode == DisplayMode.WORD) {
                _word.value = item.word
                _direction.value = "${item.lang} \u27a1"
            } else {
                _word.value = item.translation
                _direction.value = "\u27a1 ${item.lang}"
            }

            _type.value = item.type.name.lowercase()
            _translation.value = null
            _description.value = null
            _example.value = null
            _buttonText.value = "Check"
            _wordColor.value = DEFAULT_COLOR
        }
        else -> {
            _translation.value = if (currentDisplayMode == DisplayMode.WORD) item.translation else item.word
            _description.value = item.description
            _example.value = item.example
            _buttonText.value = "Next"
            if (item.learned) {
                _wordColor.value = LEARNED_COLOR
            }

            currentDisplayMode = DisplayMode.ALL
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

    fun resetLearned() {
        viewModelScope.launch(Dispatchers.IO) {
            db.dao().resetLearned(activeTypes(), activeLanguages())
            refresh()
        }
    }

    override fun onSharedPreferenceChanged(p: SharedPreferences?, s: String?) = refresh()

    private fun refresh() {
        val activeTypes = activeTypes()
        val activeLanguages = activeLanguages()
        val showAll = preferences.getBoolean("show_all", true)

        // set up display modes
        val allowedDisplayModes = mutableListOf<DisplayMode>().apply {
            if (preferences.getBoolean("translation_direct", true)) {
                add(DisplayMode.WORD)
            }
            if (preferences.getBoolean("translation_reverse", true)) {
                add(DisplayMode.TRANSLATION)
            }
        }
        initDisplayMode = if (allowedDisplayModes.size == 1) allowedDisplayModes[0] else null

        // set up valid items
        items.clear()
        viewModelScope.launch(Dispatchers.IO) {
            items.addAll(
                if (showAll) {
                    db.dao().listWithTypes(activeTypes, activeLanguages)
                } else {
                    db.dao().listWithTypes(false, activeTypes, activeLanguages)
                }
            )

            withContext(Dispatchers.Main) {
                if (items.isNotEmpty()) {
                    currentDisplayMode = DisplayMode.ALL
                    next()
                } else {
                    // TODO add special screen or disable button
                    _word.value = if (activeTypes.isEmpty()) "ENABLE AT LEAST ONE WORD TYPE" else  "LOAD WORDS USING + MENU BUTTON"
                    _translation.value = null
                    _description.value = null
                    _example.value = null
                }
            }
        }
    }

    private fun activeTypes(): List<VocabularyEntry.Type> = mutableListOf<VocabularyEntry.Type>().apply {
        if (preferences.getBoolean("verb", true)) {
            add(VocabularyEntry.Type.VERB)
        }
        if (preferences.getBoolean("pverb", true)) {
            add(VocabularyEntry.Type.PVERB)
        }
        if (preferences.getBoolean("noun", true)) {
            add(VocabularyEntry.Type.NOUN)
        }
        if (preferences.getBoolean("adjective", true)) {
            add(VocabularyEntry.Type.ADJECTIVE)
        }
        if (preferences.getBoolean("adverb", true)) {
            add(VocabularyEntry.Type.ADVERB)
        }
        if (preferences.getBoolean("idiom", true)) {
            add(VocabularyEntry.Type.IDIOM)
        }
        if (preferences.getBoolean("miscellaneous", true)) {
            add(VocabularyEntry.Type.MISC)
        }
    }

    private fun activeLanguages(): List<VocabularyEntry.Lang> = mutableListOf<VocabularyEntry.Lang>().apply {
        if (preferences.getBoolean("en", true)) {
            add(VocabularyEntry.Lang.EN)
        }
        if (preferences.getBoolean("fr", true)) {
            add(VocabularyEntry.Lang.FR)
        }
    }
}