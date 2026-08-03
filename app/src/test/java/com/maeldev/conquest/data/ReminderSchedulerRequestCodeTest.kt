package com.maeldev.conquest.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderSchedulerRequestCodeTest {
    @Test
    fun entityType_ordinals_startAtZero() {
        assertEquals(0, ReminderEntityType.TASK.ordinal)
        assertEquals(1, ReminderEntityType.EVENT.ordinal)
    }
}
