package com.maeldev.conquest.data

import com.maeldev.conquest.data.entity.EventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

class DateConverterTest {
    private val converter = DateConverter()

    @Test
    fun testFromTimestamp() {
        assertNull(converter.fromTimestamp(null))
        val time = 1700000000000L
        val date = converter.fromTimestamp(time)
        assertEquals(time, date?.time)
    }

    @Test
    fun testDateToTimestamp() {
        assertNull(converter.dateToTimestamp(null))
        val date = Date(1700000000000L)
        val timestamp = converter.dateToTimestamp(date)
        assertEquals(1700000000000L, timestamp)
    }

    @Test
    fun testEventTypeToString() {
        assertNull(converter.eventTypeToString(null))
        assertEquals("EXPO", converter.eventTypeToString(EventType.EXPO))
        assertEquals("CONVENTION", converter.eventTypeToString(EventType.CONVENTION))
    }

    @Test
    fun testStringToEventType() {
        assertNull(converter.stringToEventType(null))
        assertEquals(EventType.EXPO, converter.stringToEventType("EXPO"))
        assertEquals(EventType.CONVENTION, converter.stringToEventType("CONVENTION"))
    }

    @Test
    fun testStringToEventTypeIgnoresCase() {
        assertEquals(EventType.CONVENTION, converter.stringToEventType("convention"))
        assertEquals(EventType.PARTY, converter.stringToEventType("Party"))
    }

    /**
     * A row holding a name this build does not know — written by hand, or by a later version —
     * must not throw: the read happens inside Room's query mapping, so an exception here takes
     * down every screen observing events rather than spoiling the one row.
     */
    @Test
    fun testStringToEventTypeFallsBackToOtherForUnknownValue() {
        assertEquals(EventType.OTHER, converter.stringToEventType("con"))
        assertEquals(EventType.OTHER, converter.stringToEventType(""))
        assertEquals(EventType.OTHER, converter.stringToEventType("FESTIVAL"))
    }

    @Test
    fun testEventTypeRoundTripsForEveryValue() {
        EventType.entries.forEach { type ->
            assertEquals(type, converter.stringToEventType(converter.eventTypeToString(type)))
        }
    }
}
