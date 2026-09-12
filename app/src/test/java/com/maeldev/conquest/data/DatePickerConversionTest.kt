package com.maeldev.conquest.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * The picker holds UTC midnight; the app holds local [Date]s. These conversions are the boundary,
 * so they are checked either side of UTC and at the times of day where a one-sided conversion
 * lands on the wrong day: late evening east of UTC, early morning west of it.
 */
class DatePickerConversionTest {
    private val defaultZone: TimeZone = TimeZone.getDefault()

    @After
    fun restoreZone() {
        TimeZone.setDefault(defaultZone)
    }

    private fun localDate(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Date {
        return Calendar.getInstance().apply {
            clear()
            set(year, month, day, hour, minute)
        }.time
    }

    private fun dayFieldsOf(date: Date): Triple<Int, Int, Int> {
        val cal = Calendar.getInstance().apply { time = date }
        return Triple(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH),
        )
    }

    private fun simulatePicker(millis: Long): Long {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private val zones =
        listOf(
            "UTC",
            "Europe/Warsaw",
            "Pacific/Kiritimati",
            "America/New_York",
            "Pacific/Midway",
        )

    @Test
    fun lateEveningEastOfUtcKeepsItsOwnDay() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"))

        val aug6LateEvening = localDate(2026, Calendar.AUGUST, 6, 23, 30)
        val picked = simulatePicker(aug6LateEvening.toPickerMillis())
        val roundTripped = pickerMillisToDate(picked)

        assertEquals(Triple(2026, Calendar.AUGUST, 6), dayFieldsOf(roundTripped))
    }

    @Test
    fun earlyMorningWestOfUtcKeepsItsOwnDay() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))

        val aug6EarlyMorning = localDate(2026, Calendar.AUGUST, 6, 0, 30)
        val picked = simulatePicker(aug6EarlyMorning.toPickerMillis())
        val roundTripped = pickerMillisToDate(picked)

        assertEquals(Triple(2026, Calendar.AUGUST, 6), dayFieldsOf(roundTripped))
    }

    @Test
    fun roundTripKeepsTheDayInEveryZoneAtEveryHour() {
        zones.forEach { zoneId ->
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))

            (0..23).forEach { hour ->
                val date = localDate(2026, Calendar.AUGUST, 6, hour, 30)
                val picked = simulatePicker(date.toPickerMillis())
                val roundTripped = pickerMillisToDate(picked)

                assertEquals(
                    "$zoneId at $hour:30 should stay on 6 Aug",
                    Triple(2026, Calendar.AUGUST, 6),
                    dayFieldsOf(roundTripped),
                )
            }
        }
    }

    @Test
    fun pickerMillisIsUtcMidnight() {
        zones.forEach { zoneId ->
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))

            val millis = localDate(2026, Calendar.AUGUST, 6, 23, 30).toPickerMillis()
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = millis

            assertEquals(zoneId, 6, utc.get(Calendar.DAY_OF_MONTH))
            assertEquals(zoneId, 0, utc.get(Calendar.HOUR_OF_DAY))
            assertEquals(zoneId, 0, utc.get(Calendar.MINUTE))
        }
    }

    @Test
    fun todayIsNotBeforeToday() {
        zones.forEach { zoneId ->
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))

            val todayAtMidnight =
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

            assertFalse(zoneId, todayAtMidnight.isBeforeToday())
        }
    }

    @Test
    fun yesterdayIsBeforeTodayAndTomorrowIsNot() {
        zones.forEach { zoneId ->
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))

            val yesterday =
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -1)
                }.time
            val tomorrow =
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                }.time

            assertTrue(zoneId, yesterday.isBeforeToday())
            assertFalse(zoneId, tomorrow.isBeforeToday())
        }
    }

    @Test
    fun conversionsAreInverses() {
        zones.forEach { zoneId ->
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))

            val millis = localDate(2026, Calendar.AUGUST, 6, 12, 0).toPickerMillis()
            val roundTripped = pickerMillisToDate(simulatePicker(millis))

            assertEquals(zoneId, millis, roundTripped.toPickerMillis())
        }
    }
}
