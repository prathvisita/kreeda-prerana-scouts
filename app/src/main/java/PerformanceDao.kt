package com.example.kreedapreranascouts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformanceDao {

    @Insert
    suspend fun insertPerformance(performance: Performance)

    @Query(
        "SELECT * FROM performances WHERE scoutId = :scoutId ORDER BY timeSeconds ASC"
    )
    fun getPerformancesForScout(
        scoutId: Int
    ): Flow<List<Performance>>

    @Query(
        "SELECT * FROM performances ORDER BY timeSeconds ASC"
    )
    fun getLeaderboard(): Flow<List<Performance>>
}