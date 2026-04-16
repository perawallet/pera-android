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

package com.algorand.wallet.transaction.di

import com.algorand.wallet.transaction.data.repository.DefaultTransactionRepository
import com.algorand.wallet.transaction.data.service.TransactionsAlgodApiService

import com.algorand.wallet.transaction.domain.repository.TransactionRepository
import com.algorand.wallet.transaction.domain.usecase.SendSignedTransaction
import com.algorand.wallet.transaction.domain.usecase.SendSignedTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionsDiModule {

    @Provides
    @Singleton
    fun provideTransactionAlgodApiService(
        @Named("algodRetrofitInterface") retrofit: Retrofit
    ): TransactionsAlgodApiService {
        return retrofit.create(TransactionsAlgodApiService::class.java)
    }

    @Provides
    fun provideTransactionRepository(repository: DefaultTransactionRepository): TransactionRepository = repository

    @Provides
    fun provideSendSignedTransaction(useCase: SendSignedTransactionUseCase): SendSignedTransaction = useCase
}
