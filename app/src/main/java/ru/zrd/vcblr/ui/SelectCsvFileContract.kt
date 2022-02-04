package ru.zrd.vcblr.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract

class SelectCsvFileContract : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit?) = Intent(Intent.ACTION_GET_CONTENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
    }

//        val x =  Intent(Intent.ACTION_GET_CONTENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "*/*"
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOCUMENTS)
//        }
//        return x
//        return Intent.createChooser(x, "Select file")
//    }

    override fun parseResult(resultCode: Int, intent: Intent?) = if (resultCode == Activity.RESULT_OK) intent?.data else null
}