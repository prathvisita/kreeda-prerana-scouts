package com.example.kreedapreranascouts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TeacherDao {

    @Insert
    suspend fun insertTeacher(teacher: Teacher)

    @Query(
        "SELECT * FROM teachers WHERE (email = :login OR mobile = :login) AND password = :password LIMIT 1"
    )
    suspend fun loginTeacher(
        login: String,
        password: String
    ): Teacher?
}