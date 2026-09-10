package com.maeldev.conquest.data.classes

import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.screens.matchesMainScreenFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class CosplayStatusTest {
    private fun cosplay(
        inProgress: Boolean,
        finished: Boolean,
    ): Cosplay {
        return Cosplay(
            uid = 1,
            inProgress = inProgress,
            finished = finished,
            name = "Goku",
            series = "DBZ",
            initialDate = Date(0),
            dueDate = null,
            budget = null,
        )
    }

    private fun form(status: CosplayStatus): CosplayFormState {
        return CosplayFormState(
            characterName = "Goku",
            series = "DBZ",
            initialDate = Date(0),
            status = status,
        )
    }

    @Test
    fun fromEntity_readsStoredFlags() {
        assertEquals(CosplayStatus.Planned, CosplayStatus.fromEntity(cosplay(false, false)))
        assertEquals(CosplayStatus.InProgress, CosplayStatus.fromEntity(cosplay(true, false)))
        assertEquals(CosplayStatus.Done, CosplayStatus.fromEntity(cosplay(false, true)))
    }

    /** A row carrying both flags is legacy data; finished wins, as the filter has always read it. */
    @Test
    fun fromEntity_prefersFinishedOverInProgress() {
        assertEquals(CosplayStatus.Done, CosplayStatus.fromEntity(cosplay(true, true)))
    }

    @Test
    fun statusSurvivesEveryRoundTrip() {
        CosplayStatus.entries.forEach { status ->
            assertEquals(status, CosplayStatus.fromEntity(form(status).toEntity()))
        }
    }

    /**
     * The bug this replaced: creating and then editing without touching the control moved a
     * cosplay from Planned to Completed, because New forced `finished = false` while Edit wrote
     * `finished = !inProgress`.
     */
    @Test
    fun editingWithoutChangingStatusKeepsIt() {
        CosplayStatus.entries.forEach { status ->
            val created = form(status).toEntity()
            val reopened = CosplayFormState.fromEntity(created)
            val saved = reopened.toUpdatedEntity(created)

            assertEquals(status, CosplayStatus.fromEntity(saved))
            assertEquals(created, saved)
        }
    }

    @Test
    fun eachStatusIsFoundByItsOwnFilter() {
        CosplayStatus.entries.forEach { status ->
            val entity = form(status).toEntity()
            val filter = CosplayStatusFilter.entries.first { it.status == status }

            assertTrue(
                "$status should match the ${filter.label} filter",
                matchesMainScreenFilter(entity, filter),
            )
            assertTrue(matchesMainScreenFilter(entity, CosplayStatusFilter.All))

            CosplayStatusFilter.entries
                .filter { it.status != null && it.status != status }
                .forEach { other ->
                    assertTrue(
                        "$status should not match the ${other.label} filter",
                        !matchesMainScreenFilter(entity, other),
                    )
                }
        }
    }
}
