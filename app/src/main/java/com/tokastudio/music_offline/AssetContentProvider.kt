package com.tokastudio.music_offline

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import java.io.FileNotFoundException
import java.io.IOException

class AssetContentProvider : ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
      return null
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
      return null
    }

    override fun onCreate(): Boolean {
       return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
      return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
       return 0
    }

    override fun getType(uri: Uri): String? {
       return null
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        val assetManager = context!!.assets
        val fileName = uri.lastPathSegment ?: throw FileNotFoundException()
        var fileDescriptor: AssetFileDescriptor? = null
        try {
            fileDescriptor=assetManager.openFd(fileName)
        }catch (e: IOException){
         e.printStackTrace()
        }
        return fileDescriptor
    }
}