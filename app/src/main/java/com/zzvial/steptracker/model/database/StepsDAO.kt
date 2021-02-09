package com.zzvial.steptracker.model.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zzvial.steptracker.model.database.data.Step
import com.zzvial.steptracker.model.database.data.stepTableContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface StepsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepsData: Step): Long

    @Transaction
    suspend fun insertAll(steps: List<Step>) {
        steps.forEach {
            insert(it)
        }
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(stepsData: Step): Int

    @Query("DELETE FROM ${stepTableContract.NAME} WHERE ${stepTableContract.FIELD_NAME_ID} = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM ${stepTableContract.NAME}")
    suspend fun deleteAll()

    @Query(
        "SELECT " +
                "   * " +
                "FROM " +
                "   ${stepTableContract.NAME} " +
                "WHERE " +
                "   ${stepTableContract.FIELD_NAME_ID} = :id"
    )
    fun getStepData(id: Long): Flow<Step>

    @Query(
        "SELECT " +
                "       SUM(${stepTableContract.FIELD_NAME_STEPS}) " +
                "FROM " +
                "   ${stepTableContract.NAME} " +
                "WHERE " +
                "   ${stepTableContract.FIELD_NAME_TIMESTAMP} >= :timestampBegin " +
                "       AND ${stepTableContract.FIELD_NAME_TIMESTAMP} <= :timestampEnd"
    )
    fun getStepsSumm(timestampBegin: Long, timestampEnd: Long): Flow<List<Int>>
}