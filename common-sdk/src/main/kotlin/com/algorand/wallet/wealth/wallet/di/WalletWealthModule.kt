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

package com.algorand.wallet.wealth.wallet.di

import com.algorand.wallet.utils.date.parser.ISO8601DateTimeParser
import com.algorand.wallet.wealth.wallet.data.api.WalletWealthApiService
import com.algorand.wallet.wealth.wallet.data.mapper.DefaultWalletWealthMapper
import com.algorand.wallet.wealth.wallet.data.mapper.DefaultWalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthMapper
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.data.repository.DefaultWalletWealthRepository
import com.algorand.wallet.wealth.wallet.domain.repository.WalletWealthRepository
import com.algorand.wallet.wealth.wallet.domain.usecase.GetWalletWealth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object WalletWealthModule {

    @Provides
    fun provideWalletWealthMapper(dateTimeParser: ISO8601DateTimeParser): WalletWealthMapper {
        return DefaultWalletWealthMapper(dateTimeParser)
    }

    @Provides
    fun provideWalletWealthPeriodRequestMapper(
        mapper: DefaultWalletWealthPeriodRequestMapper
    ): WalletWealthPeriodRequestMapper = mapper

    @Provides
    @Singleton
    fun provideWalletWealthApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): WalletWealthApiService {
        return retrofit.create(WalletWealthApiService::class.java)
    }

    @Provides
    fun provideWalletWealthRepository(repository: DefaultWalletWealthRepository): WalletWealthRepository = repository

    @Provides
    fun provideGetWalletWealth(repository: WalletWealthRepository): GetWalletWealth {
        return GetWalletWealth(repository::getWalletWealth)
    }
}
