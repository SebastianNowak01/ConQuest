package com.maeldev.conquest.data.classes

import com.maeldev.conquest.data.entity.Cosplay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.Date

class CosplayStatsTest {
    private fun daysAgo(days: Int): Date {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }.time
    }

    private fun cosplay(
        finished: Boolean = false,
        initialDate: Date = daysAgo(10),
        finishedDate: Date? = null,
        totalTimeDays: Long = 0L,
    ): Cosplay {
        return Cosplay(
            uid = 1,
            inProgress = !finished,
            finished = finished,
            name = "Pennywise",
            series = "It",
            initialDate = initialDate,
            dueDate = null,
            budget = null,
            totalTimeDays = totalTimeDays,
            finishedDate = finishedDate,
        )
    }

    @Test
    fun unfinishedCosplayCountsUpToToday() {
        assertEquals(11L, cosplay(initialDate = daysAgo(10)).daysWorked())
    }

    /** Both ends count, so the day a cosplay starts is already one day worked. */
    @Test
    fun unfinishedCosplayStartedTodayIsOne() {
        assertEquals(1L, cosplay(initialDate = daysAgo(0)).daysWorked())
    }

    @Test
    fun cosplayStartedAndFinishedOnTheSameDayIsOne() {
        val today = daysAgo(0)

        assertEquals(
            1L,
            cosplay(finished = true, initialDate = today, finishedDate = today).daysWorked(),
        )
    }

    @Test
    fun finishedCosplayFreezesAtItsFinishDate() {
        val cosplay =
            cosplay(
                finished = true,
                initialDate = daysAgo(30),
                finishedDate = daysAgo(10),
            )

        assertEquals(21L, cosplay.daysWorked())
    }

    /** Rows finished before `finished_date` existed keep the old cached planned span. */
    @Test
    fun finishedCosplayWithoutStampFallsBackToCachedSpan() {
        val cosplay =
            cosplay(
                finished = true,
                initialDate = daysAgo(30),
                finishedDate = null,
                totalTimeDays = 7L,
            )

        assertEquals(8L, cosplay.daysWorked())
    }

    @Test
    fun aFinishDateBeforeTheStartNeverFallsBelowOne() {
        val cosplay =
            cosplay(
                finished = true,
                initialDate = daysAgo(5),
                finishedDate = daysAgo(20),
            )

        assertEquals(1L, cosplay.daysWorked())
    }

    /**
     * The initial date used to be stored with a time of day while every other date is midnight,
     * so a whole-millisecond division dropped a day. Both sides are calendar days now.
     */
    @Test
    fun aTimeOfDayOnTheInitialDateDoesNotLoseADay() {
        val morning =
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -2)
                set(Calendar.HOUR_OF_DAY, 22)
                set(Calendar.MINUTE, 45)
            }.time

        assertEquals(3L, cosplay(initialDate = morning).daysWorked())
    }

    @Test
    fun formStampsTheFinishDateOnlyOnce() {
        val doneForm =
            CosplayFormState(
                characterName = "Pennywise",
                series = "It",
                initialDate = daysAgo(10),
                status = CosplayStatus.Done,
            )

        val created = doneForm.toEntity(uid = 1)
        assertNotNull(created.finishedDate)

        val editedLater =
            doneForm.copy(series = "IT 2").toUpdatedEntity(
                created.copy(finishedDate = daysAgo(5)),
            )
        assertEquals(daysAgo(5).toDayStamp(), editedLater.finishedDate?.toDayStamp())
    }

    @Test
    fun leavingDoneClearsTheFinishDate() {
        val finished = cosplay(finished = true, finishedDate = daysAgo(3))
        val reopened =
            CosplayFormState(
                characterName = "Pennywise",
                series = "It",
                initialDate = daysAgo(10),
                status = CosplayStatus.InProgress,
            ).toUpdatedEntity(finished)

        assertNull(reopened.finishedDate)
    }

    private fun Date.toDayStamp(): String {
        return Calendar.getInstance().apply { time = this@toDayStamp }.let {
            "${it.get(Calendar.YEAR)}-${it.get(Calendar.DAY_OF_YEAR)}"
        }
    }
}
