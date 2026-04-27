package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM cosplay_elements WHERE id = :id LIMIT 1")
    fun getElementById(id: Int): Flow<CosplayElement?>

    @Query("SELECT * FROM cosplay_elements")
    fun getAllElements(): Flow<List<CosplayElement>>

    @Query("SELECT DISTINCT cosplay_id FROM cosplay_elements WHERE id IN (:ids)")
    suspend fun getCosplayIdsForElementIdsOnce(ids: Set<Int>): List<Int>

    @Query("SELECT photo_path FROM cosplay_elements WHERE id IN (:ids) AND photo_path IS NOT NULL")
    suspend fun getPhotoPathsForElementIdsOnce(ids: Set<Int>): List<String>

    @Update
    suspend fun updateElement(cosplayElement: CosplayElement)
}