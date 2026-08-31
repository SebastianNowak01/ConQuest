---
name: write-tests
description: Guide for writing unit tests (Robolectric, Room DAOs, ViewModels) and Jetpack Compose UI tests in the ConQuest project.
---

# Write Tests Skill

This skill outlines how to write unit, database, ViewModel, and Jetpack Compose UI tests for ConQuest.

## Test Types & Directory Structure

- **Unit & Robolectric Tests**: `app/src/test/java/com/maeldev/conquest/`
- **Instrumented UI Tests**: `app/src/androidTest/java/com/maeldev/conquest/`

Configured dependencies: JUnit 4, Robolectric, Room Testing, Kotlin Coroutines Test (`kotlinx.coroutines.test`), Jetpack Compose UI Testing (`androidx.compose.ui.test`).

## Core Testing Conventions

1. **Arrange-Act-Assert**: Structure all test functions cleanly into setup, execution, and verification phases.
2. **Naming Convention**: Name test methods descriptively:
   `fun entityName_operation_expectedResult()`
   (e.g., `fun insertCosplay_withValidData_persistsInDatabase()`, `fun deleteElementsByIds_deletesManagedImages()`)

---

## 1. ViewModel Testing

- Run with Robolectric: `@RunWith(RobolectricTestRunner::class)` and `@Config(application = ConQuestApplication::class)`.
- Build an in-memory Room database in `@Before`:
  ```kotlin
  val context = ApplicationProvider.getApplicationContext<ConQuestApplication>()
  db = Room.inMemoryDatabaseBuilder(context, CosplayDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  ```
- Instantiate the ViewModel directly by injecting test DAOs from the test database.
- Use `runTest` (and `StandardTestDispatcher` / `UnconfinedTestDispatcher` if overriding dispatchers) for coroutine execution.
- Assert on `StateFlow` values using `.value` for immediate state or `.first()` for Flow collections.

```kotlin
package com.maeldev.conquest.viewmodel

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.ConQuestApplication
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(application = ConQuestApplication::class)
class CosplayViewModelTest {

    private lateinit var db: CosplayDatabase
    private lateinit var viewModel: CosplayViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<ConQuestApplication>()
        db = Room.inMemoryDatabaseBuilder(app, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        viewModel = CosplayViewModel(
            application = app,
            dao = db.cosplayDao(),
            photoDao = db.cosplayPhotoDao(),
            progressPhotoDao = db.progressPhotoDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertCosplay_validCosplay_addsToDatabase() = runTest {
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

        val cosplays = db.cosplayDao().getAllCosplays().first()
        assertEquals(1, cosplays.size)
        assertEquals("Naruto", cosplays[0].name)
    }
}
```

---

## 2. Room DAO Testing

- Test DAOs using in-memory Room database with `@RunWith(RobolectricTestRunner::class)` (or `@RunWith(AndroidJUnit4::class)`).
- Verify CRUD operations (Insert, Query, Update, Delete) and cascading deletion constraints.
- Wrap suspend calls in `runTest { ... }`.
- For Room `Flow` queries, call `.first()` to retrieve the emitted dataset.

```kotlin
package com.maeldev.conquest.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.maeldev.conquest.data.database.CosplayDatabase
import com.maeldev.conquest.data.entity.Cosplay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Date

@RunWith(RobolectricTestRunner::class)
class CosplayDaoTest {

    private lateinit var db: CosplayDatabase
    private lateinit var dao: CosplayDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.cosplayDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertCosplay_andGetById_returnsMatchingCosplay() = runTest {
        val cosplay = Cosplay(
            uid = 1,
            inProgress = true,
            finished = false,
            name = "Zelda",
            series = "The Legend of Zelda",
            initialDate = Date(),
            dueDate = null,
            budget = 150.0
        )
        dao.insertCosplay(cosplay)

        val result = dao.getCosplayById(1).first()
        assertEquals("Zelda", result?.name)
    }
}
```

---

## 3. Jetpack Compose UI Testing

- Apply `@get:Rule val composeTestRule = createComposeRule()`.
- Load component under test via `composeTestRule.setContent { ... }`.
- Target elements using UI finders:
  - `composeTestRule.onNodeWithText("...")`
  - `composeTestRule.onNodeWithContentDescription("...")`
  - `composeTestRule.onNodeWithTag("...")`
- Assert state and interaction results:
  - `assertIsDisplayed()`
  - `assertExists()`
  - `assertDoesNotExist()`
  - `performClick()`
  - `performTextInput("...")`

```kotlin
package com.maeldev.conquest.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyInputFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myInputField_displaysLabelAndAcceptsTextInput() {
        var enteredText = ""

        composeTestRule.setContent {
            MyInputField(
                label = "Cosplay Name",
                value = enteredText,
                onValueChange = { enteredText = it }
            )
        }

        composeTestRule.onNodeWithText("Cosplay Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cosplay Name").performTextInput("Edward Elric")
        assertEquals("Edward Elric", enteredText)
    }
}
```
