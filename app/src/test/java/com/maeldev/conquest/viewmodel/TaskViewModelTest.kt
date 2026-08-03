package com.maeldev.conquest.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayTask
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
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private lateinit var application: ConQuestApplication
    private lateinit var db: CosplayDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db = Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        viewModel = TaskViewModel(
            application,
            db.cosplayTaskDao(),
            db.cosplayDao()
        )
    }

    @Test
    fun insertTask_addsToDatabase() = runBlocking {
        val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()
        
        val task = CosplayTask(
            id = 0,
            cosplayId = cosplayId,
            taskName = "Sewing",
            done = false,
            alarm = true,
            notes = null,
            date = Date()
        )
        
        viewModel.insertTask(task)
        Thread.sleep(100)
        
        val tasks = db.cosplayTaskDao().getAllTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("Sewing", tasks[0].taskName)
    }
}
