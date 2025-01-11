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

package com.algorand.common.nameservice.di

import com.algorand.android.nameservice.data.mapper.NameServiceMapper
import com.algorand.android.nameservice.data.mapper.NameServiceMapperImpl
import com.algorand.android.nameservice.data.mapper.NameServiceSearchResultMapper
import com.algorand.android.nameservice.data.mapper.NameServiceSearchResultMapperImpl
import com.algorand.android.nameservice.data.mapper.NameServiceSourceMapper
import com.algorand.android.nameservice.data.mapper.NameServiceSourceMapperImpl
import com.algorand.common.foundation.cache.InMemoryLocalCache
import com.algorand.common.foundation.network.pera.getPeraMobileHttpClient
import com.algorand.common.nameservice.data.repository.NameServiceRepositoryImpl
import com.algorand.common.nameservice.data.service.NameServiceApiService
import com.algorand.common.nameservice.data.service.NameServiceApiServiceImpl
import com.algorand.common.nameservice.domain.repository.NameServiceRepository
import com.algorand.common.nameservice.domain.usecase.GetAccountNameService
import com.algorand.common.nameservice.domain.usecase.GetNameServiceSearchResults
import com.algorand.common.nameservice.domain.usecase.GetNameServiceSearchResultsUseCase
import com.algorand.common.nameservice.domain.usecase.InitializeAccountNameService
import org.koin.dsl.module

internal val nameServiceKoinModule = module {
    factory<NameServiceSearchResultMapper> { NameServiceSearchResultMapperImpl() }
    factory<GetNameServiceSearchResults> { GetNameServiceSearchResultsUseCase(get()) }
    factory<NameServiceMapper> { NameServiceMapperImpl(get()) }
    factory<NameServiceSourceMapper> { NameServiceSourceMapperImpl() }
    factory<InitializeAccountNameService> {
        InitializeAccountNameService { addresses ->
            get<NameServiceRepository>().initializeNameServiceCache(addresses)
        }
    }
    factory<GetAccountNameService> {
        GetAccountNameService {
            get<NameServiceRepository>().getNameService(it)
        }
    }
    single<NameServiceRepository> {
        NameServiceRepositoryImpl(
            get(),
            get(),
            InMemoryLocalCache(),
            get()
        )
    }
    factory<NameServiceApiService> {
        NameServiceApiServiceImpl(getPeraMobileHttpClient(get()))
    }
}
