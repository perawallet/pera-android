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

package com.algorand.wallet.wealth.address.di

import com.algorand.wallet.utils.date.parser.ISO8601DateTimeParser
import com.algorand.wallet.wealth.address.data.api.AddressWealthApiService
import com.algorand.wallet.wealth.address.data.mapper.AddressWealthMapper
import com.algorand.wallet.wealth.address.data.mapper.DefaultAddressWealthMapper
import com.algorand.wallet.wealth.address.data.repository.DefaultAddressWealthRepository
import com.algorand.wallet.wealth.address.domain.repository.AddressWealthRepository
import com.algorand.wallet.wealth.address.domain.usecase.GetAddressWealth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AddressWealthModule {

    @Provides
    fun provideAddressWealthMapper(dateTimeParser: ISO8601DateTimeParser): AddressWealthMapper {
        return DefaultAddressWealthMapper(dateTimeParser)
    }

    @Provides
    fun provideAddressWealthRepository(repository: DefaultAddressWealthRepository): AddressWealthRepository = repository

    @Provides
    @Singleton
    fun provideAddressWealthApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AddressWealthApiService {
        return retrofit.create(AddressWealthApiService::class.java)
    }

    @Provides
    fun provideGetAddressWealth(repository: AddressWealthRepository): GetAddressWealth {
        return GetAddressWealth(repository::getAddressWealth)
    }
}
