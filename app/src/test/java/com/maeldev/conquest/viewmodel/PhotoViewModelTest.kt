package com.maeldev.conquest.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayPhoto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date
import androidx.room.Room

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = ConQuestApplication::class)
class PhotoViewModelTest {

    private lateinit var viewModel: PhotoViewModel
    private lateinit var application: ConQuestApplication
    private lateinit var db: CosplayDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db = Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        viewModel = PhotoViewModel(
            application,
            db.cosplayPhotoDao()
        )
    }

    @Test
    fun addPhoto_addsToDatabase() = runBlocking {
        val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()
        
        viewModel.addPhoto(cosplayId, "/test/path/photo.jpg")
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        
        // Need to set cosplay ID to collect photos flow, but can just query DAO directly
        val photos = db.cosplayPhotoDao().getPhotosForCosplay(cosplayId).first()
        assertEquals(1, photos.size)
        assertEquals("/test/path/photo.jpg", photos[0].path)
    }

    @Test
    fun deletePhotosByIds_removesFromDatabase() = runBlocking {
        val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()
        db.cosplayPhotoDao().insertPhoto(CosplayPhoto(0, cosplayId, "/test/path/photo2.jpg"))
        val photosBefore = db.cosplayPhotoDao().getPhotosForCosplay(cosplayId).first()
        val photoId = photosBefore[0].id
        
        viewModel.deletePhotosByIds(setOf(photoId))
        repeat(10) {
            org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            Thread.sleep(50)
        }
        
        val photos = db.cosplayPhotoDao().getPhotosForCosplay(cosplayId).first()
        assertEquals(0, photos.size)
    }
}
