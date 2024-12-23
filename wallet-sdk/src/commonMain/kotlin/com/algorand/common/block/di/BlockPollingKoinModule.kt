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

package com.algorand.common.block.di

import com.algorand.common.block.data.repository.BlockPollingRepositoryImpl
import com.algorand.common.block.data.service.BlockPollingApiService
import com.algorand.common.block.data.service.BlockPollingApiServiceImpl
import com.algorand.common.block.domain.repository.BlockPollingRepository
import com.algorand.common.block.domain.usecase.ClearLastKnownBlockNumber
import com.algorand.common.block.domain.usecase.GetLastKnownBlockNumber
import com.algorand.common.block.domain.usecase.ShouldUpdateAccountCache
import com.algorand.common.block.domain.usecase.ShouldUpdateAccountCacheUseCase
import com.algorand.common.block.domain.usecase.UpdateLastKnownBlockNumber
import com.algorand.common.block.domain.usecase.UpdateLastKnownBlockNumberUseCase
import com.algorand.common.foundation.cache.SingleInMemoryLocalCache
import com.algorand.common.foundation.network.pera.getPeraMobileHttpClient
import org.koin.dsl.module

internal val blockPollingKoinModule = module {
    single<BlockPollingApiService> { BlockPollingApiServiceImpl(getPeraMobileHttpClient(get())) }
    single<BlockPollingRepository> { BlockPollingRepositoryImpl(get(), SingleInMemoryLocalCache(), get()) }

    factory<ClearLastKnownBlockNumber> {
        ClearLastKnownBlockNumber {
            get<BlockPollingRepository>().clearLastKnownBlockNumber()
        }
    }

    factory<GetLastKnownBlockNumber> {
        GetLastKnownBlockNumber {
            get<BlockPollingRepository>().getLastKnownAccountBlockNumber()
        }
    }

    factory<ShouldUpdateAccountCache> {
        ShouldUpdateAccountCacheUseCase(get(), get(), get())
    }

    factory<UpdateLastKnownBlockNumber> {
        UpdateLastKnownBlockNumberUseCase(get(), get())
    }
}
