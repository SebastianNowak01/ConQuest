package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.conquest.data.entity.CosplayPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayPhotoDao {
    @Insert
    suspend fun insertPhoto(photo: CosplayPhoto)

    @Query("SELECT * FROM cosplay_photos WHERE cosplay_id = :cosplayId")
    fun getPhotosForCosplay(cosplayId: Int): Flow<List<CosplayPhoto>>
}