package com.algorand.android.shared_db.custominfo.di

import com.algorand.android.shared_db.AccountSharedDatabase
import com.algorand.android.shared_db.custominfo.dao.CustomInfoDao
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
internal object CustomInfoDatabaseModule {

    @Provides
    @Singleton
    fun provideCustomInfoDao(database: AccountSharedDatabase): CustomInfoDao {
        return database.customInfoDao()
    }
}
