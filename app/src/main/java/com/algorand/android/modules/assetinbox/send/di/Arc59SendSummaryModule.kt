/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.di

import android.content.Context
import com.algorand.android.modules.assetinbox.send.data.mapper.Arc59SendSummaryMapper
import com.algorand.android.modules.assetinbox.send.data.mapper.Arc59SendSummaryMapperImpl
import com.algorand.android.modules.assetinbox.send.data.repository.Arc59SendSummaryRepositoryImpl
import com.algorand.android.modules.assetinbox.send.data.service.Arc59SendSummaryApiService
import com.algorand.android.modules.assetinbox.send.domain.mapper.Arc59SignedTransactionDetailMapper
import com.algorand.android.modules.assetinbox.send.domain.mapper.Arc59SignedTransactionDetailMapperImpl
import com.algorand.android.modules.assetinbox.send.domain.mapper.Arc59TransactionPayloadMapper
import com.algorand.android.modules.assetinbox.send.domain.mapper.Arc59TransactionPayloadMapperImpl
import com.algorand.android.modules.assetinbox.send.domain.repository.Arc59SendSummaryRepository
import com.algorand.android.modules.assetinbox.send.domain.usecase.CreateArc59SendTransaction
import com.algorand.android.modules.assetinbox.send.domain.usecase.CreateArc59SendTransactionUseCase
import com.algorand.android.modules.assetinbox.send.domain.usecase.CreateArc59Transactions
import com.algorand.android.modules.assetinbox.send.domain.usecase.CreateArc59TransactionsUseCase
import com.algorand.android.modules.assetinbox.send.domain.usecase.GetArc59SendSummary
import com.algorand.android.modules.assetinbox.send.ui.mapper.Arc59SendSummaryPreviewMapper
import com.algorand.android.modules.assetinbox.send.ui.mapper.Arc59SendSummaryPreviewMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object Arc59SendSummaryModule {

    @Provides
    @Singleton
    fun provideArc59SendSummaryRepository(
        repository: Arc59SendSummaryRepositoryImpl
    ): Arc59SendSummaryRepository = repository

    @Provides
    @Singleton
    fun provideArc59SendSummaryApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): Arc59SendSummaryApiService {
        return retrofit.create(Arc59SendSummaryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideArc59SendSummaryMapper(
        mapper: Arc59SendSummaryMapperImpl
    ): Arc59SendSummaryMapper = mapper

    @Provides
    @Singleton
    fun provideGetArc59SendSummary(
        repository: Arc59SendSummaryRepository
    ): GetArc59SendSummary = GetArc59SendSummary(repository::getArc59SendSummary)

    @Provides
    @Singleton
    fun provideArc59SendSummaryPreviewMapper(
        @ApplicationContext context: Context
    ): Arc59SendSummaryPreviewMapper {
        return Arc59SendSummaryPreviewMapperImpl(context.resources)
    }

    @Provides
    @Singleton
    fun provideCreateArc59SendTransaction(
        useCase: CreateArc59SendTransactionUseCase
    ): CreateArc59SendTransaction = useCase

    @Provides
    @Singleton
    fun provideCreateArc59Transactions(
        useCase: CreateArc59TransactionsUseCase
    ): CreateArc59Transactions = useCase

    @Provides
    @Singleton
    fun provideArc59TransactionPayloadMapper(
        impl: Arc59TransactionPayloadMapperImpl
    ): Arc59TransactionPayloadMapper = impl

    @Provides
    @Singleton
    fun provideArc59SignedTransactionDetailMapper(
        impl: Arc59SignedTransactionDetailMapperImpl
    ): Arc59SignedTransactionDetailMapper = impl
}
