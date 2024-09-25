/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.transaction.component.pendingtxn.di

import com.algorand.android.module.transaction.component.pendingtxn.data.mapper.PendingTransactionDetailMapper
import com.algorand.android.module.transaction.component.pendingtxn.data.mapper.PendingTransactionDetailMapperImpl
import com.algorand.android.module.transaction.component.pendingtxn.data.mapper.PendingTransactionMapper
import com.algorand.android.module.transaction.component.pendingtxn.data.mapper.PendingTransactionMapperImpl
import com.algorand.android.module.transaction.component.pendingtxn.data.mapper.TransactionTypeMapper
import com.algorand.android.module.transaction.component.pendingtxn.data.mapper.TransactionTypeMapperImpl
import com.algorand.android.module.transaction.component.pendingtxn.data.repository.PendingTransactionRepositoryImpl
import com.algorand.android.module.transaction.component.pendingtxn.data.service.PendingTransactionsApiService
import com.algorand.android.module.transaction.component.pendingtxn.domain.repository.PendingTransactionRepository
import com.algorand.android.module.transaction.component.pendingtxn.domain.usecase.GetPendingTransactions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object PendingTransactionModule {

    @Provides
    @Singleton
    fun providePendingTransactionDetailMapper(
        impl: PendingTransactionDetailMapperImpl
    ): PendingTransactionDetailMapper = impl

    @Provides
    @Singleton
    fun providePendingTransactionMapper(impl: PendingTransactionMapperImpl): PendingTransactionMapper = impl

    @Provides
    @Singleton
    fun provideTransactionTypeMapper(impl: TransactionTypeMapperImpl): TransactionTypeMapper = impl

    @Provides
    @Singleton
    fun providePendingTransactionRepository(impl: PendingTransactionRepositoryImpl): PendingTransactionRepository = impl

    @Provides
    @Singleton
    fun providePendingTransactionsApiService(
        @Named("algodRetrofitInterface") algodRetrofitInterface: Retrofit
    ): PendingTransactionsApiService {
        return algodRetrofitInterface.create(PendingTransactionsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetPendingTransactions(
        repository: PendingTransactionRepository
    ): GetPendingTransactions = GetPendingTransactions(repository::getPendingTransactions)
}
