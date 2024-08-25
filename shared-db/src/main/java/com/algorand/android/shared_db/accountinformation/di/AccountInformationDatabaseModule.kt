package com.algorand.android.shared_db.accountinformation.di

import com.algorand.android.shared_db.AccountSharedDatabase
import com.algorand.android.shared_db.accountinformation.dao.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountInformationDatabaseModule {

    @Provides
    @Singleton
    fun provideAccountInformationDao(
        database: AccountSharedDatabase
    ): AccountInformationDao {
        return database.accountInformationDao()
    }

    @Provides
    @Singleton
    fun provideAssetHoldingDao(
        database: AccountSharedDatabase
    ): AssetHoldingDao {
        return database.assetHoldingDao()
    }
}
