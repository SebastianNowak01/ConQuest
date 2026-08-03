package com.maeldev.conquest.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.data.classes.CosplaySortOption
import com.maeldev.conquest.data.entity.Cosplay
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
import com.maeldev.conquest.data.database.CosplayDatabase

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = ConQuestApplication::class)
class CosplayViewModelTest {

    private lateinit var viewModel: CosplayViewModel
    private lateinit var application: ConQuestApplication
    private lateinit var db: CosplayDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db = Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        viewModel = CosplayViewModel(
            application,
            db.cosplayDao(),
            db.cosplayPhotoDao(),
            db.progressPhotoDao()
        )
    }

    @Test
    fun setMainScreenSort_updatesStateFlow() {
        assertEquals(CosplaySortOption.Character, viewModel.mainScreenSort.value)
        viewModel.setMainScreenSort(CosplaySortOption.Series)
        assertEquals(CosplaySortOption.Series, viewModel.mainScreenSort.value)
    }

    @Test
    fun insertCosplay_addsToDatabase() = runBlocking {
        val cosplay = Cosplay(
            uid = 0,
            inProgress = true,
            finished = false,
            name = "Naruto",
            series = "Naruto",
            initialDate = Date(),
            dueDate = null,
            budget = null
        )
        
        viewModel.insertCosplay(cosplay)
        Thread.sleep(100)
        
        val cosplays = db.cosplayDao().getAllCosplays().first()
        assertEquals(1, cosplays.size)
        assertEquals("Naruto", cosplays[0].name)
    }
}
