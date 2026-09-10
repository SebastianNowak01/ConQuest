package com.maeldev.conquest.viewmodel

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.MainDispatcherRule
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayElement
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
class ElementViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ElementViewModel
    private lateinit var application: ConQuestApplication
    private lateinit var db: CosplayDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db =
            Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
                .allowMainThreadQueries()
                // Run Room's work inline so a DAO call has finished when it returns.
                .setQueryExecutor { it.run() }
                .setTransactionExecutor { it.run() }
                .build()
        viewModel =
            ElementViewModel(
                application,
                db.cosplayElementDao(),
                db.cosplayDao(),
            )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertElement_addsToDatabase() =
        runTest {
            val cosplayId = db.cosplayDao().insertCosplay(Cosplay(0, true, false, "C1", "S1", Date(), null, null)).toInt()

            val element =
                CosplayElement(
                    id = 0,
                    cosplayId = cosplayId,
                    name = "Wig",
                    cost = 20.0,
                    ready = true,
                    photoPath = null,
                    highlight = false,
                    bought = true,
                    notes = null,
                )

            viewModel.insertElement(element)

            val elements = db.cosplayElementDao().getAllElements().first()
            assertEquals(1, elements.size)
            assertEquals("Wig", elements[0].name)
        }
}
