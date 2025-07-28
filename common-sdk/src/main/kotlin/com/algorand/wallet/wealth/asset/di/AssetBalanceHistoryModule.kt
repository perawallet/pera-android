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

package com.algorand.wallet.wealth.asset.di

import com.algorand.wallet.utils.date.parser.ISO8601DateTimeParser
import com.algorand.wallet.wealth.asset.data.api.AssetBalanceHistoryApiService
import com.algorand.wallet.wealth.asset.data.mapper.AssetBalanceHistoryMapper
import com.algorand.wallet.wealth.asset.data.mapper.DefaultAssetBalanceHistoryMapper
import com.algorand.wallet.wealth.asset.data.repository.DefaultAssetBalanceHistoryRepository
import com.algorand.wallet.wealth.asset.domain.repository.AssetBalanceHistoryRepository
import com.algorand.wallet.wealth.asset.domain.usecase.GetAssetBalanceHistory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetBalanceHistoryModule {

    @Provides
    fun provideAssetBalanceHistoryMapper(dateTimeParser: ISO8601DateTimeParser): AssetBalanceHistoryMapper {
        return DefaultAssetBalanceHistoryMapper(dateTimeParser)
    }

    @Provides
    fun provideAssetBalanceHistoryRepository(repository: DefaultAssetBalanceHistoryRepository): AssetBalanceHistoryRepository =
        repository

    @Provides
    @Singleton
    fun provideAssetBalanceHistoryApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetBalanceHistoryApiService {
        return retrofit.create(AssetBalanceHistoryApiService::class.java)
    }

    @Provides
    fun provideGetAssetBalanceHistory(repository: AssetBalanceHistoryRepository): GetAssetBalanceHistory {
        return GetAssetBalanceHistory(repository::getAssetBalanceHistory)
    }
}
