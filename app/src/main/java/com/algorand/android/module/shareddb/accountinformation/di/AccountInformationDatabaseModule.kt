package com.algorand.android.module.shareddb.accountinformation.di

import com.algorand.android.module.shareddb.AccountSharedDatabase
import com.algorand.android.module.shareddb.accountinformation.dao.AccountInformationDao
import com.algorand.android.module.shareddb.accountinformation.dao.AssetHoldingDao
import dagger.Module
import dagger.Provides
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
