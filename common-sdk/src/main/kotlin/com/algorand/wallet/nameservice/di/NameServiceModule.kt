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

package com.algorand.wallet.nameservice.di

import com.algorand.wallet.foundation.cache.InMemoryLocalCache
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import com.algorand.wallet.nameservice.data.mapper.NameServiceMapper
import com.algorand.wallet.nameservice.data.mapper.NameServiceMapperImpl
import com.algorand.wallet.nameservice.data.mapper.NameServiceSearchResultMapper
import com.algorand.wallet.nameservice.data.mapper.NameServiceSearchResultMapperImpl
import com.algorand.wallet.nameservice.data.mapper.NameServiceSourceMapper
import com.algorand.wallet.nameservice.data.mapper.NameServiceSourceMapperImpl
import com.algorand.wallet.nameservice.data.repository.NameServiceRepositoryImpl
import com.algorand.wallet.nameservice.data.service.NameServiceApiService
import com.algorand.wallet.nameservice.domain.repository.NameServiceRepository
import com.algorand.wallet.nameservice.domain.usecase.GetAccountNameService
import com.algorand.wallet.nameservice.domain.usecase.GetNameServiceSearchResults
import com.algorand.wallet.nameservice.domain.usecase.GetNameServiceSearchResultsUseCase
import com.algorand.wallet.nameservice.domain.usecase.InitializeAccountNameService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object NameServiceModule {

    @Provides
    fun provideNameServiceSearchResultMapper(
        impl: NameServiceSearchResultMapperImpl
    ): NameServiceSearchResultMapper = impl

    @Provides
    fun provideNameServiceMapper(impl: NameServiceMapperImpl): NameServiceMapper = impl

    @Provides
    fun provideNameServiceSourceMapper(impl: NameServiceSourceMapperImpl): NameServiceSourceMapper = impl

    @Provides
    fun provideInitializeAccountNameService(
        repository: NameServiceRepository
    ): InitializeAccountNameService = InitializeAccountNameService(repository::initializeNameServiceCache)

    @Provides
    fun provideGetAccountNameService(
        repository: NameServiceRepository
    ): GetAccountNameService = GetAccountNameService(repository::getNameService)

    @Provides
    @Singleton
    fun provideNameServiceRepository(
        apiService: NameServiceApiService,
        peraApiErrorHandler: PeraRetrofitErrorHandler,
        nameServiceMapper: NameServiceMapper,
        searchResultMapper: NameServiceSearchResultMapper
    ): NameServiceRepository = NameServiceRepositoryImpl(
        apiService,
        nameServiceMapper,
        InMemoryLocalCache(),
        searchResultMapper,
        peraApiErrorHandler
    )

    @Provides
    @Singleton
    fun provideNameServiceApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): NameServiceApiService = retrofit.create(NameServiceApiService::class.java)

    @Provides
    fun provideGetNameServiceSearchResults(
        useCase: GetNameServiceSearchResultsUseCase
    ): GetNameServiceSearchResults = useCase
}
