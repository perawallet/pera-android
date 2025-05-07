/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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
