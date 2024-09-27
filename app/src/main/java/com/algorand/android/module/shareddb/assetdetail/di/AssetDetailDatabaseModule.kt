package com.algorand.android.module.shareddb.assetdetail.di

import com.algorand.android.module.shareddb.AccountSharedDatabase
import com.algorand.android.module.shareddb.assetdetail.dao.AssetDetailDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleMediaDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleTraitDao
import dagger.Module
import dagger.Provides
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
