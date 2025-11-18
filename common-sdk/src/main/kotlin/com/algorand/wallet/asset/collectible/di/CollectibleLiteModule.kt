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

package com.algorand.wallet.asset.collectible.di

import com.algorand.wallet.asset.collectible.data.database.dao.PaginatedCollectibleDao
import com.algorand.wallet.asset.collectible.data.mapper.model.CollectibleLiteSortTypeQueryMapper
import com.algorand.wallet.asset.collectible.data.mapper.model.CollectibleLiteSortTypeQueryMapperImpl
import com.algorand.wallet.asset.collectible.data.repository.CollectiblePagedRepositoryImpl
import com.algorand.wallet.asset.collectible.domain.repository.CollectiblePagedRepository
import com.algorand.wallet.asset.collectible.domain.usecase.GetCollectibleLiteCountFlow
import com.algorand.wallet.asset.collectible.domain.usecase.GetCollectibleLitesFlow
import com.algorand.wallet.foundation.database.PeraDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CollectibleLiteModule {

    @Provides
    fun provideCollectibleLiteSortTypeQueryMapper(
        impl: CollectibleLiteSortTypeQueryMapperImpl
    ): CollectibleLiteSortTypeQueryMapper = impl

    @Provides
    fun provideCollectiblePagedRepository(impl: CollectiblePagedRepositoryImpl): CollectiblePagedRepository = impl

    @Provides
    @Singleton
    fun providePaginatedCollectibleDao(database: PeraDatabase): PaginatedCollectibleDao {
        return database.paginatedCollectibleDao()
    }

    @Provides
    fun provideGetCollectibleLitesFlow(
        repository: CollectiblePagedRepository
    ): GetCollectibleLitesFlow = GetCollectibleLitesFlow(repository::getPaginatedAssetCollectibleLiteItems)

    @Provides
    fun provideGetCollectibleLiteCount(
        repository: CollectiblePagedRepository
    ): GetCollectibleLiteCountFlow = GetCollectibleLiteCountFlow(repository::getCollectibleLiteCountFlow)
}
