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

package com.algorand.wallet.transaction.history.di

import androidx.paging.PagingSource
import com.algorand.wallet.transaction.history.data.mapper.DefaultTransactionHistoryAssetTransferTypeMapper
import com.algorand.wallet.transaction.history.data.mapper.DefaultTransactionHistoryMapper
import com.algorand.wallet.transaction.history.data.mapper.DefaultTransactionHistoryPaymentTypeMapper
import com.algorand.wallet.transaction.history.data.mapper.DefaultTransactionHistorySwapGroupDetailMapper
import com.algorand.wallet.transaction.history.data.mapper.TransactionHistoryAssetTransferTypeMapper
import com.algorand.wallet.transaction.history.data.mapper.TransactionHistoryMapper
import com.algorand.wallet.transaction.history.data.mapper.TransactionHistoryPaymentTypeMapper
import com.algorand.wallet.transaction.history.data.mapper.TransactionHistorySwapGroupDetailMapper
import com.algorand.wallet.transaction.history.data.repository.DefaultTransactionHistoryPagingSource
import com.algorand.wallet.transaction.history.data.repository.DefaultTransactionHistoryRepository
import com.algorand.wallet.transaction.history.data.service.TransactionHistoryApiService
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistoryPagingData
import com.algorand.wallet.transaction.history.domain.repository.TransactionHistoryRepository
import com.algorand.wallet.transaction.history.domain.usecase.GetTransactionHistory
import com.algorand.wallet.transaction.history.domain.usecase.GetTransactionHistorySwapGroupDetail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionHistoryDiModule {

    @Provides
    fun provideTransactionHistoryMapper(mapper: DefaultTransactionHistoryMapper): TransactionHistoryMapper = mapper

    @Provides
    fun provideTransactionHistoryPagingSource(
        source: DefaultTransactionHistoryPagingSource
    ): PagingSource<TransactionHistoryPagingData, TransactionHistory> = source

    @Provides
    @Singleton
    fun provideTransactionHistoryApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): TransactionHistoryApiService {
        return retrofit.create(TransactionHistoryApiService::class.java)
    }

    @Provides
    fun provideTransactionHistoryRepository(
        repository: DefaultTransactionHistoryRepository
    ): TransactionHistoryRepository = repository

    @Provides
    fun provideGetTransactionHistory(
        repository: TransactionHistoryRepository
    ): GetTransactionHistory = GetTransactionHistory(repository::getTransactionHistory)

    @Provides
    fun provideTransactionHistorySwapGroupDetailMapper(
        mapper: DefaultTransactionHistorySwapGroupDetailMapper
    ): TransactionHistorySwapGroupDetailMapper = mapper

    @Provides
    fun provideTransactionHistoryAssetTransferTypeMapper(
        mapper: DefaultTransactionHistoryAssetTransferTypeMapper
    ): TransactionHistoryAssetTransferTypeMapper = mapper

    @Provides
    fun provideTransactionHistoryPaymentTypeMapper(
        mapper: DefaultTransactionHistoryPaymentTypeMapper
    ): TransactionHistoryPaymentTypeMapper = mapper

    @Provides
    fun provideGetTransactionHistorySwapGroupDetail(
        repository: TransactionHistoryRepository
    ): GetTransactionHistorySwapGroupDetail = GetTransactionHistorySwapGroupDetail(repository::getSwapGroupTransactions)
}
