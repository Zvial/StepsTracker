package com.zzvial.steptracker.DI

import android.content.Context
import com.zzvial.steptracker.model.database.data.StepsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun getDatabaseInstance(@ApplicationContext appContext: Context) = StepsDatabase.getInstance(appContext)
}