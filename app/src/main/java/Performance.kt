package com.example.kreedapreranascouts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "performances")
data class Performance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val scoutId: Int,
    val eventName: String,
    val timeSeconds: Double,
    val date: String
)

