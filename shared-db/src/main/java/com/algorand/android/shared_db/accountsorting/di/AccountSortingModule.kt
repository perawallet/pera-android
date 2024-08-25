package com.algorand.android.shared_db.accountsorting.di

import com.algorand.android.shared_db.AccountSharedDatabase
import com.algorand.android.shared_db.accountsorting.dao.AccountIndexDao
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountSortingModule {

    @Provides
    @Singleton
    fun provideAccountIndexDao(
        database: AccountSharedDatabase
    ): AccountIndexDao {
        return database.accountIndexDao()
    }
}
