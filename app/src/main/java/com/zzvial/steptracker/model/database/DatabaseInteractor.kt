package com.zzvial.steptracker.model.database

import com.zzvial.steptracker.model.database.data.Step
import com.zzvial.steptracker.model.database.data.StepsDatabase
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class DatabaseInteractor @Inject constructor() {

    @Inject lateinit var database: StepsDatabase

    private suspend fun saveSensorData(steps: List<Step>) {
        database.stepsDAO().insertAll(steps)
    }

    suspend fun getStepsByToday(): Flow<List<Int>> {
        val today = Date()
        val begDay = GregorianCalendar()
        begDay.time = today
        begDay.set(Calendar.HOUR_OF_DAY, 0)
        begDay.set(Calendar.MINUTE, 0)
        begDay.set(Calendar.SECOND, 0)
        begDay.set(Calendar.MILLISECOND, 0)

        val endDay = GregorianCalendar()
        endDay.time = begDay.time
        endDay.add(Calendar.DAY_OF_MONTH, 1)

        return getStepsSummByPeriod(begDay.time, endDay.time)
    }

    suspend fun getStepsSummByPeriod(dateBegin: Date, dateEnd: Date): Flow<List<Int>> {
        val tsBegin = dateBegin.getTime()
        val tsEnd = dateEnd.getTime()
        return database.stepsDAO().getStepsSumm(tsBegin, tsEnd)
    }

    suspend fun insertSteps(steps: List<Step>) {
        database.stepsDAO().insertAll(steps)
    }

    suspend fun clearAllSteps() {
        database.stepsDAO().deleteAll()
    }
}