package com.example.kreedapreranascouts

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Scout::class, Performance::class,Teacher::class],
    version = 3
)
abstract class ScoutDatabase : RoomDatabase() {

    abstract fun scoutDao(): ScoutDao

    abstract fun performanceDao(): PerformanceDao

    abstract fun teacherDao(): TeacherDao

    companion object {

        @Volatile
        private var INSTANCE: ScoutDatabase? = null

        fun getDatabase(context: Context): ScoutDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScoutDatabase::class.java,
                    "scout_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}