/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.block.di

import com.algorand.android.block.data.repository.BlockPollingRepositoryImpl
import com.algorand.android.block.data.service.BlockPollingApiService
import com.algorand.android.block.domain.repository.BlockPollingRepository
import com.algorand.android.block.domain.usecase.ClearLastKnownBlockNumber
import com.algorand.android.block.domain.usecase.GetLastKnownBlockNumber
import com.algorand.android.block.domain.usecase.ShouldUpdateAccountCache
import com.algorand.android.block.domain.usecase.ShouldUpdateAccountCacheUseCase
import com.algorand.android.block.domain.usecase.UpdateLastKnownBlockNumber
import com.algorand.android.block.domain.usecase.UpdateLastKnownBlockNumberUseCase
import com.algorand.android.caching.SingleInMemoryLocalCache
import com.hipo.hipoexceptionsandroid.RetrofitErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object BlockPollingModule {

    @Provides
    @Singleton
    fun provideBlockPollingApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): BlockPollingApiService {
        return retrofit.create(BlockPollingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBlockPollingRepository(
        apiService: BlockPollingApiService,
        hipoErrorHandler: RetrofitErrorHandler
    ): BlockPollingRepository {
        return BlockPollingRepositoryImpl(
            blockPollingApiService = apiService,
            blockPollingLocalCache = SingleInMemoryLocalCache(),
            hipoErrorHandler = hipoErrorHandler
        )
    }

    @Provides
    @Singleton
    fun provideClearLastKnownBlockNumber(
        repository: BlockPollingRepository
    ): ClearLastKnownBlockNumber = ClearLastKnownBlockNumber(repository::clearLastKnownBlockNumber)

    @Provides
    @Singleton
    fun provideGetLastKnownAccountBlockNumber(
        repository: BlockPollingRepository
    ): GetLastKnownBlockNumber = GetLastKnownBlockNumber(repository::getLastKnownAccountBlockNumber)

    @Provides
    @Singleton
    fun provideShouldUpdateAccountCache(useCase: ShouldUpdateAccountCacheUseCase): ShouldUpdateAccountCache = useCase

    @Provides
    @Singleton
    fun provideUpdateLastKnownBlockNumber(
        useCase: UpdateLastKnownBlockNumberUseCase
    ): UpdateLastKnownBlockNumber = useCase
}
