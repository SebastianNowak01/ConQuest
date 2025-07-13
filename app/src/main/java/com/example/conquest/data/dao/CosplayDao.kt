package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.conquest.data.entity.Cosplay
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayDao {
    @Insert
    suspend fun insertCosplay(cosplay: Cosplay): Long

    @Delete
    suspend fun delete(cosplay: Cosplay)

    @Query("SELECT * FROM cosplays")
    fun getAllCosplays(): Flow<List<Cosplay>>
}