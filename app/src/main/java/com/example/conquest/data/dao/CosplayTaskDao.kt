package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.conquest.data.entity.CosplayTask

@Dao
interface CosplayTaskDao {
    @Insert
    suspend fun insertElement(task: CosplayTask)
}