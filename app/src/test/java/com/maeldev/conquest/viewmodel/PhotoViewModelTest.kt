package com.maeldev.conquest.viewmodel

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.MainDispatcherRule
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayPhoto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = ConQuestApplication::class)
class PhotoViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PhotoViewModel
    private lateinit var application: ConQuestApplication
    private lateinit var db: CosplayDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db =
            Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
                .allowMainThreadQueries()
                .setQueryExecutor { it.run() }
                .setTransactionExecutor { it.run() }
                .build()
        viewModel =
            PhotoViewModel(
                application,
                db.cosplayPhotoDao(),
            )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addPhoto_addsToDatabase() =
        runTest {
            val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()

            viewModel.addPhoto(cosplayId, "/test/path/photo.jpg")
            org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            val photos = db.cosplayPhotoDao().getPhotosForCosplay(cosplayId).first()
            assertEquals(1, photos.size)
            assertEquals("/test/path/photo.jpg", photos[0].path)
        }

    @Test
    fun deletePhotosByIds_removesFromDatabase() =
        runTest {
            val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()
            db.cosplayPhotoDao().insertPhoto(CosplayPhoto(0, cosplayId, "/test/path/photo2.jpg"))
            val photosBefore = db.cosplayPhotoDao().getPhotosForCosplay(cosplayId).first()
            val photoId = photosBefore[0].id

            viewModel.deletePhotosByIds(setOf(photoId))
            repeat(10) {
                org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            }

            val photos = db.cosplayPhotoDao().getPhotosForCosplay(cosplayId).first()
            assertEquals(0, photos.size)
        }
}
