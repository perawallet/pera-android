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

package com.algorand.android.ui.swap.di

import com.algorand.android.ui.swap.data.network.SwapQuoteTransactionsApiService
import com.algorand.android.ui.swap.data.repository.DefaultSwapQuoteTransactionsRepository
import com.algorand.android.ui.swap.domain.repository.SwapQuoteTransactionsRepository
import com.algorand.android.ui.swap.domain.usecase.CreateSwapV2QuoteTransactions
import com.algorand.android.ui.swap.domain.usecase.CreateSwapV2QuoteTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
internal object SwapQuoteTransactionsModule {

    @Provides
    fun provideSwapQuoteTransactionsRepository(
        repository: DefaultSwapQuoteTransactionsRepository
    ): SwapQuoteTransactionsRepository = repository

    @Provides
    fun provideSwapQuoteTransactionsApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): SwapQuoteTransactionsApiService {
        return retrofit.create(SwapQuoteTransactionsApiService::class.java)
    }

    @Provides
    fun provideCreateSwapV2QuoteTransactions(
        useCase: CreateSwapV2QuoteTransactionsUseCase
    ): CreateSwapV2QuoteTransactions = useCase
}
