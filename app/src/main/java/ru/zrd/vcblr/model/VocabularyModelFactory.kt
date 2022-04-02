package ru.zrd.vcblr.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VocabularyModelFactory(private val app: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(VocabularyModel::class.java)) {
            return VocabularyModel(app) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}