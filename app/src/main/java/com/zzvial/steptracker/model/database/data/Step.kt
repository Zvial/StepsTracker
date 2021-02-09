package com.zzvial.steptracker.model.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = stepTableContract.NAME)
data class Step(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = stepTableContract.FIELD_NAME_ID)
    val id: Long? = null,
    @ColumnInfo(name = stepTableContract.FIELD_NAME_STEPS)
    val steps: Int,
    @ColumnInfo(name = stepTableContract.FIELD_NAME_TIMESTAMP)
    val timestamp: Long
)