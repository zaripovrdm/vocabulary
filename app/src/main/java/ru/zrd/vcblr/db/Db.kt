package ru.zrd.vcblr.db

import android.content.Context
import androidx.room.*

@Database(entities = [VocabularyEntry::class], version = 1)
@TypeConverters(VocabularyEntry.TypeConverters::class)
abstract class Db : RoomDatabase() {
    
    companion object {

        @Volatile
        private var INSTANCE: Db? = null

        fun instance(context: Context): Db = INSTANCE ?: synchronized(this) {
            if (INSTANCE != null) INSTANCE!! else {
                val instance: Db = Room.databaseBuilder(context, Db::class.java, "vocabulary").build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun dao(): VocabularyDao
}

