package com.algorand.android.module.shareddb.custominfo.di

import com.algorand.android.module.shareddb.AccountSharedDatabase
import com.algorand.android.module.shareddb.custominfo.dao.CustomInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CustomInfoDatabaseModule {

    @Provides
    @Singleton
    fun provideCustomInfoDao(database: AccountSharedDatabase): CustomInfoDao {
        return database.customInfoDao()
    }
}
