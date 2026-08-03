package com.maeldev.conquest.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileUtilTest {

    @Test
    fun deleteFileByPath_existingFile_returnsTrueAndDeletes() {
        val tempFile = File.createTempFile("test_image", ".jpg")
        assertTrue(tempFile.exists())

        val result = deleteFileByPath(tempFile.absolutePath)
        
        assertTrue(result)
        assertFalse(tempFile.exists())
    }

    @Test
    fun deleteFileByPath_nonExistingFile_returnsTrue() {
        val path = "/this/file/does/not/exist.jpg"
        val file = File(path)
        assertFalse(file.exists())

        val result = deleteFileByPath(path)
        
        assertTrue(result)
    }

    @Test
    fun deleteFileByPath_nullPath_handledGracefully() {
        // Just checking if empty path is handled.
        val result = deleteFileByPath("")
        assertTrue(result)
    }
}
