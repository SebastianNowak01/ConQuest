package com.maeldev.conquest.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class DateUtilsTest {
    private fun fieldsOf(millis: Long): List<Int> {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return listOf(
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            cal.get(Calendar.SECOND),
            cal.get(Calendar.MILLISECOND),
        )
    }

    private fun dateAt(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Date {
        return Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 30)
            set(Calendar.MILLISECOND, 500)
        }.time
    }

    @Test
    fun atReminderTime_normalizesToTheReminderHour() {
        val afternoon = dateAt(2026, Calendar.MARCH, 14, hour = 15, minute = 45)

        assertEquals(listOf(REMINDER_HOUR_OF_DAY, 0, 0, 0), fieldsOf(afternoon.atReminderTime()))
    }

    @Test
    fun atReminderTime_keepsTheCalendarDay() {
        val date = dateAt(2026, Calendar.MARCH, 14, hour = 23, minute = 59)

        val cal = Calendar.getInstance().apply { timeInMillis = date.atReminderTime() }

        assertEquals(2026, cal.get(Calendar.YEAR))
        assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH))
        assertEquals(14, cal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun atReminderTime_isStableForTimesEitherSideOfTheReminderHour() {
        val early = dateAt(2026, Calendar.MARCH, 14, hour = 1, minute = 0)
        val late = dateAt(2026, Calendar.MARCH, 14, hour = 22, minute = 0)

        assertEquals(early.atReminderTime(), late.atReminderTime())
    }
}
