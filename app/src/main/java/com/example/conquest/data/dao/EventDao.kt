package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.conquest.data.entity.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Query("DELETE FROM events WHERE id IN (:ids)")
    suspend fun deleteEventsByIds(ids: Set<Int>)

    @Query("SELECT * FROM events ORDER BY event_date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    fun getEventById(id: Int): Flow<Event?>
}

