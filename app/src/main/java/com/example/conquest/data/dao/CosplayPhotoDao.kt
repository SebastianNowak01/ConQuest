package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.conquest.data.entity.CosplayPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayPhotoDao {
    @Insert
    suspend fun insertPhoto(photo: CosplayPhoto)

    @Delete
    suspend fun deletePhotos(photos: List<CosplayPhoto>)

    @Query("SELECT * FROM cosplay_photos WHERE cosplay_id IN (:cosplaysId)")
    suspend fun getPhotosForCosplayOnce(cosplaysId: Set<Int>): List<CosplayPhoto>

    @Query("SELECT * FROM cosplay_photos WHERE cosplay_id = :cosplayId")
    fun getPhotosForCosplay(cosplayId: Int): Flow<List<CosplayPhoto>>

    @Query("SELECT * FROM cosplay_photos WHERE id IN (:ids)")
    suspend fun getPhotosByIdsOnce(ids: Set<Int>): List<CosplayPhoto>

    @Query("DELETE FROM cosplay_photos WHERE id IN (:ids)")
    suspend fun deletePhotosByIds(ids: Set<Int>)

    @Query("SELECT * FROM cosplay_photos WHERE id = :id LIMIT 1")
    fun getPhotoById(id: Int): Flow<CosplayPhoto?>

    @Update
    suspend fun updatePhoto(photo: CosplayPhoto)
}