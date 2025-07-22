package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.conquest.data.entity.CosplayElement
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayElementDao {
    @Insert
    suspend fun insertElement(element: CosplayElement): Long

    @Delete
    suspend fun deleteElement(element: CosplayElement)

    @Query("SELECT * FROM cosplay_elements WHERE cosplay_id = :cosplayId")
    fun getElementsForCosplay(cosplayId: Int): Flow<List<CosplayElement>>
}