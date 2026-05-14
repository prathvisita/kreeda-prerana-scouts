package com.example.kreedapreranascouts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: String,
    val gender: String,
    val district: String,
    val mobile: String,
    val email: String,
    val password: String
)