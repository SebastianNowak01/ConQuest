package com.maeldev.conquest.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.data.classes.EventSortOption
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.Event
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
class EventViewModelTest {

    private lateinit var viewModel: EventViewModel
    private lateinit var application: ConQuestApplication
    private lateinit var db: CosplayDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db = Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        viewModel = EventViewModel(
            application,
            db.eventDao(),
            db.cosplayDao()
        )
    }

    @Test
    fun setEventsSortOption_updatesStateFlow() {
        assertEquals(EventSortOption.Date, viewModel.eventsSortOption.value)
        viewModel.setEventsSortOption(EventSortOption.Alphabetical)
        assertEquals(EventSortOption.Alphabetical, viewModel.eventsSortOption.value)
    }

    @Test
    fun insertEvent_addsToDatabase() = runBlocking {
        // Insert a Cosplay first for relation testing
        val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()
        
        val event = Event(
            id = 0,
            eventName = "Comic Con",
            eventLocation = "NY",
            eventType = com.maeldev.conquest.data.entity.EventType.EXPO,
            eventDate = Date(),
            description = null,
            alarm = true
        )
        
        viewModel.insertEvent(event, setOf(cosplayId))
        Thread.sleep(100)
        
        val events = db.eventDao().getAllEvents().first()
        assertEquals(1, events.size)
        assertEquals("Comic Con", events[0].eventName)
    }
}
