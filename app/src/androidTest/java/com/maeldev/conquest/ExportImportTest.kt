package com.maeldev.conquest

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.maeldev.conquest.data.dao.*
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayElement
import com.maeldev.conquest.util.ExportImportUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Date

@RunWith(AndroidJUnit4::class)
class ExportImportTest {

    private lateinit var db: CosplayDatabase
    private lateinit var cosplayDao: CosplayDao
    private lateinit var elementDao: CosplayElementDao
    private lateinit var taskDao: CosplayTaskDao
    private lateinit var photoDao: CosplayPhotoDao
    private lateinit var progressPhotoDao: ProgressPhotoDao
    private lateinit var eventDao: EventDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CosplayDatabase::class.java).build()
        cosplayDao = db.cosplayDao()
        elementDao = db.cosplayElementDao()
        taskDao = db.cosplayTaskDao()
        photoDao = db.cosplayPhotoDao()
        progressPhotoDao = db.progressPhotoDao()
        eventDao = db.eventDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testExportAndImportCosplays() {
        runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Setup mock data
        val initialDate = Date()
        val cosplay = Cosplay(
            uid = 0,
            inProgress = true,
            finished = false,
            name = "Test Export Cosplay",
            series = "Test Series",
            initialDate = initialDate,
            dueDate = null,
            budget = 100.0,
            cosplayPhotoPath = "images/test_photo.jpg"
        )
        val cosplayId = cosplayDao.insertCosplay(cosplay).toInt()

        val element = CosplayElement(
            id = 0,
            cosplayId = cosplayId,
            name = "Test Element",
            cost = 10.0,
            ready = false,
            photoPath = null,
            bought = false,
            notes = null
        )
        elementDao.insertElement(element)

        // Mock an image file in the expected directory
        val imageDir = File(context.filesDir, "images")
        imageDir.mkdirs()
        val testImageFile = File(imageDir, "test_photo.jpg")
        testImageFile.writeText("fake image content")

        // 2. Export to a temporary ZIP file
        val tempZipFile = File.createTempFile("export_test", ".zip", context.cacheDir)
        val zipUri = Uri.fromFile(tempZipFile)

        val exportResult = ExportImportUtil.exportCosplays(
            context = context,
            cosplayIds = setOf(cosplayId),
            targetUri = zipUri,
            cosplayDao = cosplayDao,
            elementDao = elementDao,
            taskDao = taskDao,
            photoDao = photoDao,
            progressPhotoDao = progressPhotoDao,
            eventDao = eventDao
        )

        assertTrue("Export failed: ${exportResult.exceptionOrNull()?.message}", exportResult.isSuccess)
        assertTrue(tempZipFile.exists())
        assertTrue(tempZipFile.length() > 0)

        // 3. Import from the temporary ZIP file
        val importResult = ExportImportUtil.importCosplays(
            context = context,
            sourceUri = zipUri,
            cosplayDao = cosplayDao,
            elementDao = elementDao,
            taskDao = taskDao,
            photoDao = photoDao,
            progressPhotoDao = progressPhotoDao,
            eventDao = eventDao
        )

        assertTrue("Import failed: ${importResult.exceptionOrNull()?.message}", importResult.isSuccess)

        // 4. Verify data was duplicated (new IDs)
        val allCosplays = cosplayDao.getAllCosplays().first()
        assertEquals(2, allCosplays.size)

        val importedCosplay = allCosplays.find { it.uid != cosplayId }
        requireNotNull(importedCosplay)
        assertEquals("Test Export Cosplay", importedCosplay.name)

        val importedElements = elementDao.getElementsForCosplay(importedCosplay.uid).first()
        assertEquals(1, importedElements.size)
        assertEquals("Test Element", importedElements[0].name)

        // Check if image file exists
        val importedImageFile = File(context.filesDir, "images/test_photo.jpg")
        assertTrue(importedImageFile.exists())

        // Cleanup
        tempZipFile.delete()
        testImageFile.delete()
        }
    }
}
