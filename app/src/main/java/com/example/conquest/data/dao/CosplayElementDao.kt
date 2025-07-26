package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.conquest.data.entity.CosplayElement
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayElementDao {
    @Insert
    suspend fun insertElement(element: CosplayElement): Long

    @Query("DELETE FROM cosplay_elements WHERE id IN (:ids)")
    suspend fun deleteElementsByIds(ids: Set<Int>)

    @Query("SELECT * FROM cosplay_elements WHERE cosplay_id = :cosplayId")
    fun getElementsForCosplay(cosplayId: Int): Flow<List<CosplayElement>>
}