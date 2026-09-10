package com.maeldev.conquest.data.classes

import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayElement
import com.maeldev.conquest.data.entity.CosplayPhoto
import com.maeldev.conquest.data.entity.CosplayTask
import com.maeldev.conquest.data.entity.Event
import com.maeldev.conquest.data.entity.EventType
import com.maeldev.conquest.data.entity.ProgressPhoto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

/**
 * Round-trip tests: entity -> dto -> entity must preserve every field that is not an id or a
 * foreign key, since an import inserts fresh rows.
 */
class ExportMappersTest {
    private val initialDate = Date(1_700_000_000_000)
    private val dueDate = Date(1_800_000_000_000)

    @Test
    fun cosplay_roundTripsEveryFieldExceptId() {
        val original =
            Cosplay(
                uid = 42,
                inProgress = true,
                finished = false,
                name = "Nezuko",
                series = "Demon Slayer",
                initialDate = initialDate,
                dueDate = dueDate,
                budget = 250.5,
                overallPercentage = 60,
                tasksCount = 5,
                eventsCount = 2,
                totalSpend = 123.45,
                totalTimeDays = 30L,
                cosplayPhotoPath = "images/nezuko.jpg",
            )

        val restored = original.toDto().toEntity()

        assertEquals(0, restored.uid)
        assertEquals(original.copy(uid = 0), restored)
    }

    @Test
    fun cosplay_nullableFieldsSurviveAsNull() {
        val original =
            Cosplay(
                uid = 1,
                inProgress = false,
                finished = true,
                name = "N",
                series = "S",
                initialDate = initialDate,
                dueDate = null,
                budget = null,
                cosplayPhotoPath = null,
            )

        val restored = original.toDto().toEntity()

        assertEquals(null, restored.dueDate)
        assertEquals(null, restored.budget)
        assertEquals(null, restored.cosplayPhotoPath)
    }

    @Test
    fun element_roundTripsAndReparents() {
        val original =
            CosplayElement(
                id = 7,
                cosplayId = 42,
                name = "Wig",
                cost = 45.0,
                ready = true,
                photoPath = "images/wig.jpg",
                highlight = true,
                bought = true,
                notes = "ordered",
            )

        val restored = original.toDto().toEntity(cosplayId = 99)

        assertEquals(0, restored.id)
        assertEquals(99, restored.cosplayId)
        assertEquals(original.copy(id = 0, cosplayId = 99), restored)
    }

    @Test
    fun task_roundTripsAndReparents() {
        val original =
            CosplayTask(
                id = 3,
                cosplayId = 42,
                taskName = "Sew hem",
                done = true,
                alarm = true,
                notes = "thread",
                date = dueDate,
            )

        val restored = original.toDto().toEntity(cosplayId = 99)

        assertEquals(original.copy(id = 0, cosplayId = 99), restored)
    }

    @Test
    fun photo_roundTripsAndReparents() {
        val original = CosplayPhoto(id = 5, cosplayId = 42, path = "images/a.jpg", notes = "front")

        val restored = original.toDto().toEntity(cosplayId = 99)

        assertEquals(original.copy(id = 0, cosplayId = 99), restored)
    }

    @Test
    fun progressPhoto_roundTripsIncludingCreatedAt() {
        val original =
            ProgressPhoto(
                id = 5,
                cosplayId = 42,
                path = "images/p.jpg",
                notes = null,
                createdAt = initialDate,
            )

        val restored = original.toDto().toEntity(cosplayId = 99)

        assertEquals(initialDate, restored.createdAt)
        assertEquals(original.copy(id = 0, cosplayId = 99), restored)
    }

    @Test
    fun event_roundTripsEveryFieldExceptId() {
        val original =
            Event(
                id = 11,
                eventName = "Comic Con",
                eventLocation = "Berlin",
                eventType = EventType.CONVENTION,
                eventDate = dueDate,
                description = "annual",
                alarm = true,
            )

        val restored = original.toDto().toEntity()

        assertEquals(0, restored.id)
        assertEquals(original.copy(id = 0), restored)
    }
}
