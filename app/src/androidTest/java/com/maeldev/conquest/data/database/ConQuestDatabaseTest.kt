package com.maeldev.conquest.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayTask
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class ConQuestDatabaseTest {
    private lateinit var db: CosplayDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetCosplay() = runBlocking {
        val dao = db.cosplayDao()
        val cosplay = Cosplay(
            uid = 1,
            inProgress = true,
            finished = false,
            name = "Test Cosplay",
            series = "Test Series",
            initialDate = Date(),
            dueDate = null,
            budget = null
        )
        dao.insertCosplay(cosplay)
        
        val allCosplays = dao.getAllCosplays().first()
        assertEquals(1, allCosplays.size)
        assertEquals("Test Cosplay", allCosplays[0].name)
    }

    @Test
    fun cascadeDeleteCosplayDeletesTasks() = runBlocking {
        val cosplayDao = db.cosplayDao()
        val taskDao = db.cosplayTaskDao()

        val cosplay = Cosplay(uid = 1, inProgress = true, finished = false, name = "C", series = "S", initialDate = Date(), dueDate = null, budget = null)
        cosplayDao.insertCosplay(cosplay)

        val task = CosplayTask(id = 1, cosplayId = 1, taskName = "T", done = false, alarm = false, notes = null, date = null)
        taskDao.insertTask(task)

        val initialTasks = taskDao.getTasksForCosplay(1).first()
        assertEquals(1, initialTasks.size)

        cosplayDao.deleteCosplaysByIds(setOf(cosplay.uid))

        val remainingTasks = taskDao.getTasksForCosplay(1).first()
        assertEquals(0, remainingTasks.size)
    }

    @Test
    fun getTasksWithActiveAlarms() = runBlocking {
        val cosplayDao = db.cosplayDao()
        val taskDao = db.cosplayTaskDao()
        val cosplay = Cosplay(uid = 1, inProgress = true, finished = false, name = "C", series = "S", initialDate = Date(), dueDate = null, budget = null)
        cosplayDao.insertCosplay(cosplay)

        val pastDate = Date(System.currentTimeMillis() - 100000)
        val futureDate = Date(System.currentTimeMillis() + 100000)
        
        val pastTask = CosplayTask(id = 1, cosplayId = 1, taskName = "T1", done = false, alarm = true, notes = null, date = pastDate)
        val futureTask = CosplayTask(id = 2, cosplayId = 1, taskName = "T2", done = false, alarm = true, notes = null, date = futureDate)
        val futureNoAlarmTask = CosplayTask(id = 3, cosplayId = 1, taskName = "T3", done = false, alarm = false, notes = null, date = futureDate)
        
        taskDao.insertTask(pastTask)
        taskDao.insertTask(futureTask)
        taskDao.insertTask(futureNoAlarmTask)

        val activeTasks = taskDao.getTasksWithActiveAlarms(System.currentTimeMillis())
        assertEquals(1, activeTasks.size)
    }
}
