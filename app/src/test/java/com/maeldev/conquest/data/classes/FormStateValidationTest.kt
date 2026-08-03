package com.maeldev.conquest.data.classes

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class FormStateValidationTest {

    @Test
    fun taskFormState_isValid() {
        val validState = TaskFormState(taskName = "Fix prop", date = Date())
        assertTrue(validState.isValid)

        val invalidStateNoName = validState.copy(taskName = "   ")
        assertFalse(invalidStateNoName.isValid)

        val invalidStateNoDate = validState.copy(date = null)
        assertFalse(invalidStateNoDate.isValid)
    }

    @Test
    fun eventFormState_isValid() {
        val validState = EventFormState(eventName = "Con", eventLocation = "Hall", eventDate = Date())
        assertTrue(validState.isValid)

        val invalidStateNoName = validState.copy(eventName = "")
        assertFalse(invalidStateNoName.isValid)

        val invalidStateNoLocation = validState.copy(eventLocation = "   ")
        assertFalse(invalidStateNoLocation.isValid)

        val invalidStateNoDate = validState.copy(eventDate = null)
        assertFalse(invalidStateNoDate.isValid)
    }

    @Test
    fun cosplayFormState_isValid() {
        val validState = CosplayFormState(characterName = "Goku", series = "DBZ", initialDate = Date())
        assertTrue(validState.isValid)

        val invalidStateNoName = validState.copy(characterName = "")
        assertFalse(invalidStateNoName.isValid)

        val invalidStateNoSeries = validState.copy(series = "   ")
        assertFalse(invalidStateNoSeries.isValid)

        val invalidStateNoDate = validState.copy(initialDate = null)
        assertFalse(invalidStateNoDate.isValid)
    }
}
