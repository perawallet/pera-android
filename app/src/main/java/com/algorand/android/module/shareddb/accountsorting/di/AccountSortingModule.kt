package com.algorand.android.module.shareddb.accountsorting.di

import com.algorand.android.module.shareddb.AccountSharedDatabase
import com.algorand.android.module.shareddb.accountsorting.dao.AccountIndexDao
import dagger.Module
import dagger.Provides
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
