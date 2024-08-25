package com.algorand.android.shared_db.assetdetail.di

import com.algorand.android.shared_db.AccountSharedDatabase
import com.algorand.android.shared_db.assetdetail.dao.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetDetailDatabaseModule {

    @Provides
    @Singleton
    fun provideAssetDetailDao(database: AccountSharedDatabase): AssetDetailDao {
        return database.assetDetailDao()
    }

    @Provides
    @Singleton
    fun provideCollectibleDao(database: AccountSharedDatabase): CollectibleDao {
        return database.collectibleDao()
    }

    @Provides
    @Singleton
    fun providesCollectibleMediaDao(database: AccountSharedDatabase): CollectibleMediaDao {
        return database.collectibleMediaDao()
    }

    @Provides
    @Singleton
    fun providesCollectibleTraitDao(database: AccountSharedDatabase): CollectibleTraitDao {
        return database.collectibleTraitDao()
    }
}
