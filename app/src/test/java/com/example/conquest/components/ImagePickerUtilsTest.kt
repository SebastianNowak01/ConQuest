package com.example.conquest.components

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ImagePickerUtilsTest {

    @Test
    fun buildManagedImageRelativePath_usesImagesDirectory() {
        val path = buildManagedImageRelativePath(
            fileNamePrefix = "cosplay_cover",
            extension = "jpg",
            timestamp = 123L,
        )

        assertEquals("images/cosplay_cover_123.jpg", path)
    }

    @Test
    fun resolveStoredImagePath_convertsRelativePathToAbsoluteInternalPath() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        val resolved = resolveStoredImagePath(context, "images/cosplay_cover_123.jpg")

        assertTrue(resolved.endsWith("${File.separator}images${File.separator}cosplay_cover_123.jpg"))
    }

    @Test
    fun deleteStoredImageByPath_deletesRelativeInternalFile() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val relativePath = "images/test_delete_${System.currentTimeMillis()}.jpg"
        val targetFile = File(context.filesDir, relativePath)
        targetFile.parentFile?.mkdirs()
        targetFile.writeText("temp")

        val deleted = deleteStoredImageByPath(context, relativePath)

        assertTrue(deleted)
        assertFalse(targetFile.exists())
    }
}

