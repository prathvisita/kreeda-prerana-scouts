package com.example.kreedapreranascouts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scouts")
data class Scout(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val age: Int,
    val district: String,
    val level: String,

    val attendance: Boolean = false
)