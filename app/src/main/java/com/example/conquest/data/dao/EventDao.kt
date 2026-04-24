package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.conquest.data.entity.Event
import com.example.conquest.data.entity.EventCosplayCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: Event): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEventCosplayCrossRefs(crossRefs: List<EventCosplayCrossRef>)

    @Query("DELETE FROM event_cosplay_cross_ref WHERE event_id = :eventId")
    suspend fun deleteEventCosplayCrossRefsForEvent(eventId: Int)

    @Query("SELECT cosplay_id FROM event_cosplay_cross_ref WHERE event_id = :eventId")
    suspend fun getCosplayIdsForEventOnce(eventId: Int): List<Int>

    @Query("SELECT DISTINCT cosplay_id FROM event_cosplay_cross_ref WHERE event_id IN (:eventIds)")
    suspend fun getCosplayIdsForEventIdsOnce(eventIds: Set<Int>): List<Int>

    @Update
    suspend fun updateEvent(event: Event)

    @Query("DELETE FROM events WHERE id IN (:ids)")
    suspend fun deleteEventsByIds(ids: Set<Int>)

    @Query("SELECT * FROM events ORDER BY event_date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    fun getEventById(id: Int): Flow<Event?>
}

