package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.conquest.data.entity.CosplayElement

@Dao
interface CosplayElementDao {
    @Insert
    suspend fun insertElement(element: CosplayElement)
}