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

package com.algorand.android.transaction.di

import com.algorand.android.algosdk.component.AlgoSdkAddress
import com.algorand.android.transaction.data.mapper.TransactionParamsMapper
import com.algorand.android.transaction.data.mapper.TransactionParamsMapperImpl
import com.algorand.android.transaction.data.repository.TransactionRepositoryImpl
import com.algorand.android.transaction.data.service.AlgodApiService
import com.algorand.android.transaction.domain.mapper.SuggestedTransactionParamsMapper
import com.algorand.android.transaction.domain.mapper.SuggestedTransactionParamsMapperImpl
import com.algorand.android.transaction.domain.repository.TransactionRepository
import com.algorand.android.transaction.domain.usecase.GetTransactionParams
import com.algorand.android.transaction.domain.usecase.IsValidAlgorandAddress
import com.algorand.android.transaction.domain.usecase.SendSignedTransaction
import com.algorand.android.transaction.domain.usecase.implementation.SendSignedTransactionUseCase
import com.algorand.android.transaction.domain.sign.usecase.GetTransactionSigner
import com.algorand.android.transaction.domain.sign.usecase.SignTransactionWithSecretKey
import com.algorand.android.transaction.domain.sign.usecase.implementation.GetTransactionSignerUseCase
import com.algorand.android.transaction.domain.sign.usecase.implementation.SignTransactionWithSecretKeyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionsModule {

    @Provides
    @Singleton
    fun provideTransactionParamsMapper(
        mapper: TransactionParamsMapperImpl
    ): TransactionParamsMapper = mapper

    @Provides
    @Singleton
    fun provideTransactionRepository(
        repository: TransactionRepositoryImpl
    ): TransactionRepository = repository

    @Provides
    @Singleton
    fun provideGetTransactionParams(
        repository: TransactionRepository
    ): GetTransactionParams = GetTransactionParams(repository::getTransactionParams)

    @Provides
    @Singleton
    fun provideAlgodApiService(
        @Named("algodRetrofitInterface") retrofit: Retrofit
    ): AlgodApiService {
        return retrofit.create(AlgodApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSendSignedTransaction(
        useCase: SendSignedTransactionUseCase
    ): SendSignedTransaction = useCase

    @Provides
    @Singleton
    fun provideSuggestedTransactionParamsMapper(
        mapper: SuggestedTransactionParamsMapperImpl
    ): SuggestedTransactionParamsMapper = mapper

    @Provides
    @Singleton
    fun provideIsValidAlgorandAddress(
        algoSdkAddress: AlgoSdkAddress
    ): IsValidAlgorandAddress = IsValidAlgorandAddress(algoSdkAddress::isValid)

    @Provides
    @Singleton
    fun provideGetTransactionSigner(
        useCase: GetTransactionSignerUseCase
    ): GetTransactionSigner = useCase

    @Provides
    @Singleton
    fun provideSignTransactionWithSecretKey(
        useCase: SignTransactionWithSecretKeyUseCase
    ): SignTransactionWithSecretKey = useCase
}
