package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.conquest.data.entity.CosplayTask
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayTaskDao {
    @Insert
    suspend fun insertTask(task: CosplayTask)

    @Query("DELETE FROM cosplay_tasks WHERE id IN (:ids)")
    suspend fun deleteTasksByIds(ids: Set<Int>)

    @Query("SELECT * FROM cosplay_tasks WHERE cosplay_id = :cosplayId")
    fun getTasksForCosplay(cosplayId: Int): Flow<List<CosplayTask>>
}