package com.zzvial.steptracker.model.database.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zzvial.steptracker.model.database.StepsDAO
import javax.inject.Singleton

internal val stepTableContract = StepsDatabase.Companion.DatabaseContract.stepTable

@Singleton
@Database(
    entities = arrayOf(Step::class),
    version = StepsDatabase.Companion.DatabaseContract.DATABASE_VERSION,
    exportSchema = false
)
abstract class StepsDatabase: RoomDatabase() {

    abstract fun stepsDAO(): StepsDAO

    companion object {
        @Volatile
        private var INSTANCE: StepsDatabase? = null

        fun getInstance(appContext: Context): StepsDatabase {
            synchronized(this) {
                var tmpInstance = INSTANCE

                if (tmpInstance == null) {
                    tmpInstance = Room.databaseBuilder(
                        appContext,
                        StepsDatabase::class.java,
                        DatabaseContract.DATABASE_NAME
                    )
                        .build()

                    INSTANCE = tmpInstance
                }
                return tmpInstance
            }
        }

        object DatabaseContract {

            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "steps_database"

            object stepTable {
                const val NAME = "steps"
                const val FIELD_NAME_ID = "_id"
                const val FIELD_NAME_STEPS = "steps_count"
                const val FIELD_NAME_TIMESTAMP = "timestamp"
            }
        }
    }
}