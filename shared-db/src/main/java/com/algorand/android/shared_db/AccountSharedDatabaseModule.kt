package com.algorand.android.shared_db

import android.content.Context
import androidx.room.Room
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountSharedDatabaseModule {

    @Provides
    @Singleton
    fun provideAccountSharedDatabase(
        @ApplicationContext appContext: Context
    ): AccountSharedDatabase {
        return Room
            .databaseBuilder(appContext, AccountSharedDatabase::class.java, AccountSharedDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}
