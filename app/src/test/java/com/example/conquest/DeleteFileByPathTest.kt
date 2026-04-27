package com.example.conquest

import java.nio.file.Files
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteFileByPathTest {

    @Test
    fun deleteFileByPath_deletesExistingFile() {
        val tempFile = Files.createTempFile("conquest-delete", ".jpg").toFile()
        tempFile.writeText("temporary")

        val deleted = deleteFileByPath(tempFile.absolutePath)

        assertTrue(deleted)
        assertFalse(tempFile.exists())
    }

    @Test
    fun deleteFileByPath_returnsTrueWhenFileDoesNotExist() {
        val missingPath = Files.createTempFile("conquest-missing", ".jpg").toFile().apply {
            delete()
        }.absolutePath

        val deleted = deleteFileByPath(missingPath)

        assertTrue(deleted)
    }
}

