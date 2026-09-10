package com.maeldev.conquest.data

import org.junit.After
import org.junit.Assert.assertEquals
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

    /**
     * What Material's picker does with the millis it is handed: it reads them as UTC, shows that
     * UTC day, and hands back that day's UTC midnight. Routing the round trips through this is
     * what makes them meaningful — comparing our two functions directly only proves they are
     * inverses of each other, which a pair of no-ops also satisfies.
     */
    private fun simulatePicker(millis: Long): Long {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // UTC itself, then either side of it, out to the extremes: Kiritimati is UTC+14 and Midway
    // UTC-11. Warsaw is the zone the off-by-one day was first seen on.
    private val zones =
        listOf(
            "UTC",
            "Europe/Warsaw",
            "Pacific/Kiritimati",
            "America/New_York",
            "Pacific/Midway",
        )

    /** The bug seen on device: 23:00 in Warsaw is the previous day in UTC. */
    @Test
    fun lateEveningEastOfUtcKeepsItsOwnDay() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"))

        val aug6LateEvening = localDate(2026, Calendar.AUGUST, 6, 23, 30)
        val picked = simulatePicker(aug6LateEvening.toPickerMillis())
        val roundTripped = pickerMillisToDate(picked)

        assertEquals(Triple(2026, Calendar.AUGUST, 6), dayFieldsOf(roundTripped))
    }

    /** The mirror image: UTC midnight is still the previous day in New York. */
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

    /** What the picker is handed must be midnight UTC, or it highlights the neighbouring day. */
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

    /** A date the user never touched must survive being loaded and saved unchanged. */
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
