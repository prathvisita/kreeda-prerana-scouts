package com.example.kreedapreranascouts

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoutDao {

    @Insert
    suspend fun insertScout(scout: Scout)

    @Delete
    suspend fun deleteScout(scout: Scout)

    @Update
    suspend fun updateScout(scout: Scout)

    @Query("SELECT * FROM scouts")
    fun getAllScouts(): Flow<List<Scout>>

    @Insert
    suspend fun insertPerformance(performance: Performance)

    @Query("SELECT * FROM performances WHERE scoutId = :scoutId")
    fun getPerformancesForScout(scoutId: Int): kotlinx.coroutines.flow.Flow<List<Performance>>

    @Query("""
SELECT * FROM performances
ORDER BY timeSeconds ASC
""")
    fun getLeaderboard():
            kotlinx.coroutines.flow.Flow<List<Performance>>
}