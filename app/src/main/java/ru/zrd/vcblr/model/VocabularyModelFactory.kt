package ru.zrd.vcblr.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.zrd.vcblr.db.Db

class VocabularyModelFactory(private val db: Db) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(VocabularyModel::class.java)) {
            return VocabularyModel(db) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}