package ru.zrd.vcblr.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VocabularyModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(VocabularyModel::class.java)) {
            return VocabularyModel(context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}