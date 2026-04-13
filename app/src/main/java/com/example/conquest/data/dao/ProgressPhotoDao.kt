package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.conquest.data.entity.ProgressPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressPhotoDao {
    @Insert
    suspend fun insertPhoto(photo: ProgressPhoto)

    @Query("SELECT * FROM progress_photos ORDER BY created_at DESC")
    fun getAllPhotos(): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM progress_photos WHERE id = :id LIMIT 1")
    fun getPhotoById(id: Int): Flow<ProgressPhoto?>

    @Query("SELECT * FROM progress_photos WHERE id IN (:ids)")
    suspend fun getPhotosByIdsOnce(ids: Set<Int>): List<ProgressPhoto>

    @Query("DELETE FROM progress_photos WHERE id IN (:ids)")
    suspend fun deletePhotosByIds(ids: Set<Int>)

    @Update
    suspend fun updatePhoto(photo: ProgressPhoto)
}

