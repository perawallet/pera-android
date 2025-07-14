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

package com.algorand.wallet.asset.pricehistory.di

import com.algorand.wallet.asset.pricehistory.data.mapper.AssetPriceHistoryMapper
import com.algorand.wallet.asset.pricehistory.data.mapper.DefaultAssetPriceHistoryMapper
import com.algorand.wallet.asset.pricehistory.data.repository.DefaultAssetPriceHistoryRepository
import com.algorand.wallet.asset.pricehistory.data.service.AssetPriceHistoryApiService
import com.algorand.wallet.asset.pricehistory.domain.repository.AssetPriceHistoryRepository
import com.algorand.wallet.asset.pricehistory.domain.usecase.GetAssetPriceHistory
import com.algorand.wallet.utils.date.parser.ISO8601DateTimeParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AssetPriceModule {

    @Provides
    @Singleton
    fun provideAssetPriceHistoryApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetPriceHistoryApiService {
        return retrofit.create(AssetPriceHistoryApiService::class.java)
    }

    @Provides
    fun provideAssetPriceHistoryMapper(dateTimeParser: ISO8601DateTimeParser): AssetPriceHistoryMapper {
        return DefaultAssetPriceHistoryMapper(dateTimeParser)
    }

    @Provides
    fun provideAssetPriceHistoryRepository(
        repository: DefaultAssetPriceHistoryRepository
    ): AssetPriceHistoryRepository = repository

    @Provides
    fun provideGetAssetPriceHistory(repository: AssetPriceHistoryRepository): GetAssetPriceHistory {
        return GetAssetPriceHistory(repository::getAssetPriceHistory)
    }
}
