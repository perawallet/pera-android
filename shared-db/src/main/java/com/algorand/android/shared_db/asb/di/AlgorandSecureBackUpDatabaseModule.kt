package com.algorand.android.shared_db.asb.di

import com.algorand.android.shared_db.AccountSharedDatabase
import com.algorand.android.shared_db.asb.dao.AlgorandSecureBackUpDao
import dagger.*
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
