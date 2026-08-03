package com.maeldev.conquest.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.ProgressPhoto
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
class ProgressPhotoViewModelTest {

    private lateinit var viewModel: ProgressPhotoViewModel
    private lateinit var application: ConQuestApplication
    private lateinit var db: CosplayDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db = Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        viewModel = ProgressPhotoViewModel(
            application,
            db.progressPhotoDao()
        )
    }

    @Test
    fun addProgressPhoto_addsToDatabase() = runBlocking {
        val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()
        
        viewModel.addProgressPhoto(cosplayId, "/test/path/progress.jpg")
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        
        val photos = db.progressPhotoDao().getPhotosForCosplay(cosplayId).first()
        assertEquals(1, photos.size)
        assertEquals("/test/path/progress.jpg", photos[0].path)
    }

    @Test
    fun deleteProgressPhotosByIds_removesFromDatabase() = runBlocking {
        val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()
        db.progressPhotoDao().insertPhoto(ProgressPhoto(0, cosplayId, "/test/path/progress2.jpg"))
        val photosBefore = db.progressPhotoDao().getPhotosForCosplay(cosplayId).first()
        val photoId = photosBefore[0].id
        
        viewModel.deleteProgressPhotosByIds(setOf(photoId))
        repeat(10) {
            org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            Thread.sleep(50)
        }
        
        val photos = db.progressPhotoDao().getPhotosForCosplay(cosplayId).first()
        assertEquals(0, photos.size)
    }
}
