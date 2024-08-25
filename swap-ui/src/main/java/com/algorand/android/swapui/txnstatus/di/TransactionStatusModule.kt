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

package com.algorand.android.swapui.txnstatus.di

import com.algorand.android.swapui.txnstatus.mapper.CompletedSwapTransactionStatusPreviewMapper
import com.algorand.android.swapui.txnstatus.mapper.CompletedSwapTransactionStatusPreviewMapperImpl
import com.algorand.android.swapui.txnstatus.mapper.FailedSwapTransactionStatusPreviewMapper
import com.algorand.android.swapui.txnstatus.mapper.FailedSwapTransactionStatusPreviewMapperImpl
import com.algorand.android.swapui.txnstatus.mapper.SendingSwapTransactionStatusPreviewMapper
import com.algorand.android.swapui.txnstatus.mapper.SendingSwapTransactionStatusPreviewMapperImpl
import com.algorand.android.swapui.txnstatus.usecase.GetSwapTransactionStatusPreviewFlow
import com.algorand.android.swapui.txnstatus.usecase.GetSwapTransactionStatusPreviewFlowUseCase
import com.algorand.android.swapui.txnstatus.usecase.SendSignedSwapTransactions
import com.algorand.android.swapui.txnstatus.usecase.SendSignedSwapTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionStatusModule {

    @Provides
    @Singleton
    fun provideSendingSwapTransactionStatusPreviewMapper(
        impl: SendingSwapTransactionStatusPreviewMapperImpl
    ): SendingSwapTransactionStatusPreviewMapper = impl

    @Provides
    @Singleton
    fun provideSendSignedSwapTransactions(
        useCase: SendSignedSwapTransactionsUseCase
    ): SendSignedSwapTransactions = useCase

    @Provides
    @Singleton
    fun provideCompletedSwapTransactionStatusPreviewMapper(
        impl: CompletedSwapTransactionStatusPreviewMapperImpl
    ): CompletedSwapTransactionStatusPreviewMapper = impl

    @Provides
    @Singleton
    fun provideFailedSwapTransactionStatusPreviewMapper(
        impl: FailedSwapTransactionStatusPreviewMapperImpl
    ): FailedSwapTransactionStatusPreviewMapper = impl

    @Provides
    @Singleton
    fun provideGetSwapTransactionStatusPreviewFlow(
        useCase: GetSwapTransactionStatusPreviewFlowUseCase
    ): GetSwapTransactionStatusPreviewFlow = useCase
}
