package com.example.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.conquest.data.entity.Cosplay
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayDao {
    @Insert
    suspend fun insertCosplay(cosplay: Cosplay): Long

    @Query("DELETE FROM cosplays WHERE uid IN (:ids)")
    suspend fun deleteCosplaysByIds(ids: Set<Int>)

    @Query("SELECT * FROM cosplays")
    fun getAllCosplays(): Flow<List<Cosplay>>

    @Query("SELECT * FROM cosplays WHERE uid = :cosplayId LIMIT 1")
    fun getCosplayById(cosplayId: Int): Flow<Cosplay?>

    @Query(
        """
        UPDATE cosplays
        SET
            tasks_count = (
                SELECT COUNT(*)
                FROM cosplay_tasks
                WHERE cosplay_id = uid
            ),
            overall_percentage = (
                CASE
                    WHEN (
                        SELECT COUNT(*)
                        FROM cosplay_tasks
                        WHERE cosplay_id = uid
                    ) = 0 THEN 0
                    ELSE CAST(ROUND((
                        (
                            SELECT COUNT(*)
                            FROM cosplay_tasks
                            WHERE cosplay_id = uid
                              AND done = 1
                        ) * 100.0
                    ) / (
                        SELECT COUNT(*)
                        FROM cosplay_tasks
                        WHERE cosplay_id = uid
                    )) AS INTEGER)
                END
            ),
            total_spend = (
                SELECT COALESCE(
                    SUM(
                        CASE
                            WHEN bought = 1 THEN COALESCE(cost, 0.0)
                            ELSE 0.0
                        END
                    ),
                    0.0
                )
                FROM cosplay_elements
                WHERE cosplay_id = uid
            ),
            events_count = (
                SELECT COUNT(DISTINCT event_id)
                FROM event_cosplay_cross_ref
                WHERE cosplay_id = uid
            ),
            total_time_days = (
                CASE
                    WHEN COALESCE(
                        (
                            SELECT MAX(date)
                            FROM cosplay_tasks
                            WHERE cosplay_id = uid
                              AND date IS NOT NULL
                        ),
                        due_date,
                        initial_date
                    ) < initial_date THEN 0
                    ELSE (
                        COALESCE(
                            (
                                SELECT MAX(date)
                                FROM cosplay_tasks
                                WHERE cosplay_id = uid
                                  AND date IS NOT NULL
                            ),
                            due_date,
                            initial_date
                        ) - initial_date
                    ) / 86400000
                END
            )
        WHERE uid = :cosplayId
        """
    )
    suspend fun recomputeStatsForCosplay(cosplayId: Int)

    @Query(
        """
        UPDATE cosplays
        SET
            tasks_count = (
                SELECT COUNT(*)
                FROM cosplay_tasks
                WHERE cosplay_id = uid
            ),
            overall_percentage = (
                CASE
                    WHEN (
                        SELECT COUNT(*)
                        FROM cosplay_tasks
                        WHERE cosplay_id = uid
                    ) = 0 THEN 0
                    ELSE CAST(ROUND((
                        (
                            SELECT COUNT(*)
                            FROM cosplay_tasks
                            WHERE cosplay_id = uid
                              AND done = 1
                        ) * 100.0
                    ) / (
                        SELECT COUNT(*)
                        FROM cosplay_tasks
                        WHERE cosplay_id = uid
                    )) AS INTEGER)
                END
            ),
            total_spend = (
                SELECT COALESCE(
                    SUM(
                        CASE
                            WHEN bought = 1 THEN COALESCE(cost, 0.0)
                            ELSE 0.0
                        END
                    ),
                    0.0
                )
                FROM cosplay_elements
                WHERE cosplay_id = uid
            ),
            events_count = (
                SELECT COUNT(DISTINCT event_id)
                FROM event_cosplay_cross_ref
                WHERE cosplay_id = uid
            ),
            total_time_days = (
                CASE
                    WHEN COALESCE(
                        (
                            SELECT MAX(date)
                            FROM cosplay_tasks
                            WHERE cosplay_id = uid
                              AND date IS NOT NULL
                        ),
                        due_date,
                        initial_date
                    ) < initial_date THEN 0
                    ELSE (
                        COALESCE(
                            (
                                SELECT MAX(date)
                                FROM cosplay_tasks
                                WHERE cosplay_id = uid
                                  AND date IS NOT NULL
                            ),
                            due_date,
                            initial_date
                        ) - initial_date
                    ) / 86400000
                END
            )
        WHERE uid IN (:cosplayIds)
        """
    )
    suspend fun recomputeStatsForCosplays(cosplayIds: Set<Int>)

    @Update
    suspend fun updateCosplay(cosplay: Cosplay)
}
