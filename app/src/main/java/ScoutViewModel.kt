package com.example.kreedapreranascouts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ScoutViewModel(application: Application) :
    AndroidViewModel(application) {

    private val db =
        ScoutDatabase.getDatabase(application)

    private val dao =
        db.scoutDao()

    private val teacherDao =
        db.teacherDao()
    val scouts = dao.getAllScouts()

    fun addScout(
        name: String,
        age: Int,
        district: String,
        level: String
    ) {
        viewModelScope.launch {
            dao.insertScout(
                Scout(
                    name = name,
                    age = age,
                    district = district,
                    level = level
                )
            )
        }
    }
    fun toggleAttendance(scout: Scout) {

        viewModelScope.launch {

            dao.updateScout(
                scout.copy(
                    attendance = !scout.attendance
                )
            )
        }
    }
    fun addPerformance(
        scoutId: Int,
        eventName: String,
        timeSeconds: Double,
        date: String
    ) {
        viewModelScope.launch {
            dao.insertPerformance(
                Performance(
                    scoutId = scoutId,
                    eventName = eventName,
                    timeSeconds = timeSeconds,
                    date = date
                )
            )
        }
    }

    fun getPerformancesForScout(scoutId: Int) =
        dao.getPerformancesForScout(scoutId)

    fun getLeaderboard() =
        dao.getLeaderboard()

    fun deleteScout(scout: Scout) {
        viewModelScope.launch {
            dao.deleteScout(scout)
        }
    }

    fun updateScout(scout: Scout) {
        viewModelScope.launch {
            dao.updateScout(scout)
        }
    }
    fun registerTeacher(teacher: Teacher) {
        viewModelScope.launch {
            teacherDao.insertTeacher(teacher)
        }
    }

    suspend fun loginTeacher(
        login: String,
        password: String
    ): Teacher? {
        return teacherDao.loginTeacher(login, password)
    }
}
