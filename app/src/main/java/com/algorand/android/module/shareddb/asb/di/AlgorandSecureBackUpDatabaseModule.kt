package com.algorand.android.module.shareddb.asb.di

import com.algorand.android.module.shareddb.AccountSharedDatabase
import com.algorand.android.module.shareddb.asb.dao.AlgorandSecureBackUpDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AlgorandSecureBackUpDatabaseModule {

    @Provides
    @Singleton
    fun provideAlgorandSecureBackUpDao(accountSharedDatabase: AccountSharedDatabase): AlgorandSecureBackUpDao {
        return accountSharedDatabase.algorandSecureBackUpDao()
    }
}
