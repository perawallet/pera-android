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

package com.algorand.android.module.transaction.component.di

import com.algorand.android.module.algosdk.AlgoSdkAddress
import com.algorand.android.module.transaction.component.data.mapper.TransactionParamsMapper
import com.algorand.android.module.transaction.component.data.mapper.TransactionParamsMapperImpl
import com.algorand.android.module.transaction.component.data.repository.TransactionRepositoryImpl
import com.algorand.android.module.transaction.component.data.service.AlgodApiService
import com.algorand.android.module.transaction.component.domain.mapper.SuggestedTransactionParamsMapper
import com.algorand.android.module.transaction.component.domain.mapper.SuggestedTransactionParamsMapperImpl
import com.algorand.android.module.transaction.component.domain.repository.TransactionRepository
import com.algorand.android.module.transaction.component.domain.sign.usecase.GetTransactionSigner
import com.algorand.android.module.transaction.component.domain.sign.usecase.SignTransactionWithSecretKey
import com.algorand.android.module.transaction.component.domain.sign.usecase.implementation.GetTransactionSignerUseCase
import com.algorand.android.module.transaction.component.domain.sign.usecase.implementation.SignTransactionWithSecretKeyUseCase
import com.algorand.android.module.transaction.component.domain.usecase.GetTransactionParams
import com.algorand.android.module.transaction.component.domain.usecase.IsValidAlgorandAddress
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedTransaction
import com.algorand.android.module.transaction.component.domain.usecase.implementation.SendSignedTransactionUseCase
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
