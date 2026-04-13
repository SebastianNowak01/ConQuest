package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.conquest.data.entity.ProgressPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressPhotoDao {
    @Insert
    suspend fun insertPhoto(photo: ProgressPhoto)

    @Query("SELECT * FROM progress_photos WHERE cosplay_id = :cosplayId ORDER BY created_at DESC")
    fun getPhotosForCosplay(cosplayId: Int): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM progress_photos WHERE id = :id AND cosplay_id = :cosplayId LIMIT 1")
    fun getPhotoById(id: Int, cosplayId: Int): Flow<ProgressPhoto?>

    @Query("SELECT * FROM progress_photos WHERE cosplay_id IN (:cosplaysId)")
    suspend fun getPhotosForCosplayOnce(cosplaysId: Set<Int>): List<ProgressPhoto>

    @Delete
    suspend fun deletePhotos(photos: List<ProgressPhoto>)

    @Query("SELECT * FROM progress_photos WHERE id IN (:ids)")
    suspend fun getPhotosByIdsOnce(ids: Set<Int>): List<ProgressPhoto>

    @Query("DELETE FROM progress_photos WHERE id IN (:ids)")
    suspend fun deletePhotosByIds(ids: Set<Int>)

    @Update
    suspend fun updatePhoto(photo: ProgressPhoto)
}

