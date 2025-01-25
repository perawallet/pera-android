/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.block.di

import com.algorand.wallet.block.data.repository.BlockPollingRepositoryImpl
import com.algorand.wallet.block.data.service.BlockPollingApiService
import com.algorand.wallet.block.domain.repository.BlockPollingRepository
import com.algorand.wallet.block.domain.usecase.ClearLastKnownBlockNumber
import com.algorand.wallet.block.domain.usecase.GetLastKnownBlockNumber
import com.algorand.wallet.block.domain.usecase.ShouldUpdateAccountCache
import com.algorand.wallet.block.domain.usecase.ShouldUpdateAccountCacheUseCase
import com.algorand.wallet.block.domain.usecase.UpdateLastKnownBlockNumber
import com.algorand.wallet.block.domain.usecase.UpdateLastKnownBlockNumberUseCase
import com.algorand.wallet.foundation.cache.SingleInMemoryLocalCache
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
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
        blockPollingApiService: BlockPollingApiService,
        retrofitErrorHandler: PeraRetrofitErrorHandler
    ): BlockPollingRepository {
        return BlockPollingRepositoryImpl(
            blockPollingApiService,
            SingleInMemoryLocalCache(),
            retrofitErrorHandler
        )
    }

    @Provides
    fun provideClearLastKnownBlockNumber(repository: BlockPollingRepository): ClearLastKnownBlockNumber {
        return ClearLastKnownBlockNumber(repository::clearLastKnownBlockNumber)
    }

    @Provides
    fun provideGetLastKnownBlockNumber(repository: BlockPollingRepositoryImpl): GetLastKnownBlockNumber {
        return GetLastKnownBlockNumber(repository::getLastKnownAccountBlockNumber)
    }

    @Provides
    fun provideShouldUpdateAccountCache(useCase: ShouldUpdateAccountCacheUseCase): ShouldUpdateAccountCache = useCase

    @Provides
    fun provideUpdateLastKnownBlockNumber(
        useCase: UpdateLastKnownBlockNumberUseCase
    ): UpdateLastKnownBlockNumber = useCase
}
