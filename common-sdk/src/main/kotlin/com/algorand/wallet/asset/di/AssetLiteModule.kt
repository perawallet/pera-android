package com.algorand.wallet.asset.di

import com.algorand.wallet.asset.data.database.dao.PaginatedAssetCollectibleDao
import com.algorand.wallet.asset.data.mapper.model.AssetCollectibleLiteSortTypeQueryMapper
import com.algorand.wallet.asset.data.mapper.model.AssetCollectibleLiteSortTypeQueryMapperImpl
import com.algorand.wallet.asset.data.mapper.model.AssetLiteMapper
import com.algorand.wallet.asset.data.mapper.model.AssetLiteMapperImpl
import com.algorand.wallet.asset.data.repository.AssetCollectibleLiteRepositoryImpl
import com.algorand.wallet.asset.domain.repository.AssetCollectibleLiteRepository
import com.algorand.wallet.asset.domain.usecase.GetAssetCollectibleLitesFlow
import com.algorand.wallet.foundation.database.PeraDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetLiteModule {

    @Provides
    @Singleton
    fun providePaginatedAssetCollectibleDao(database: PeraDatabase): PaginatedAssetCollectibleDao {
        return database.paginatedAssetCollectibleDao()
    }

    @Provides
    fun provideGetAssetCollectibleLitesFlow(
        repository: AssetCollectibleLiteRepository
    ): GetAssetCollectibleLitesFlow = GetAssetCollectibleLitesFlow(repository::getPaginatedAssetCollectibleLiteItems)

    @Provides
    fun provideAssetLiteMapper(impl: AssetLiteMapperImpl): AssetLiteMapper = impl

    @Provides
    fun provideAssetCollectibleLiteSortTypeQueryMapper(
        impl: AssetCollectibleLiteSortTypeQueryMapperImpl
    ): AssetCollectibleLiteSortTypeQueryMapper = impl

    @Provides
    fun provideAssetCollectibleLiteRepository(
        impl: AssetCollectibleLiteRepositoryImpl
    ): AssetCollectibleLiteRepository = impl
}
