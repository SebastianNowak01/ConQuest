package com.maeldev.conquest.util

import android.util.Log
import java.io.File

fun deleteFileByPath(path: String): Boolean {
    try {
        val file = File(path)
        if (!file.exists()) {
            return true
        }
        val deleted = file.delete()
        if (!deleted) {
            Log.w("ConQuestFileCleanup", "Failed to delete file at path: $path")
        }
        return deleted
    } catch (e: Exception) {
        Log.e("ConQuestFileCleanup", "Error deleting file at path: $path", e)
        return false
    }
}
