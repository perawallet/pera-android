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

package com.algorand.android.module.transaction.history.ui.di

import com.algorand.android.module.transaction.history.ui.TransactionHistoryPreviewManager
import com.algorand.android.module.transaction.history.ui.TransactionHistoryPreviewManagerImpl
import com.algorand.android.module.transaction.history.ui.TransactionHistoryProcessor
import com.algorand.android.module.transaction.history.ui.TransactionHistoryProcessorImpl
import com.algorand.android.module.transaction.history.ui.mapper.BaseTransactionItemMapper
import com.algorand.android.module.transaction.history.ui.mapper.BaseTransactionItemMapperImpl
import com.algorand.android.module.transaction.history.ui.mapper.TransactionLoadStatePreviewMapper
import com.algorand.android.module.transaction.history.ui.mapper.TransactionLoadStatePreviewMapperImpl
import com.algorand.android.module.transaction.history.ui.pendingtxn.domain.mapper.PendingTransactionItemMapper
import com.algorand.android.module.transaction.history.ui.pendingtxn.domain.mapper.PendingTransactionItemMapperImpl
import com.algorand.android.module.transaction.history.ui.pendingtxn.domain.usecase.GetPendingTransactionItems
import com.algorand.android.module.transaction.history.ui.pendingtxn.domain.usecase.GetPendingTransactionItemsUseCase
import com.algorand.android.module.transaction.history.ui.usecase.GetTransactionTargetUserDisplayName
import com.algorand.android.module.transaction.history.ui.usecase.GetTransactionTargetUserDisplayNameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionHistoryUiModule {

    @Provides
    @Singleton
    fun provideBaseTransactionItemMapper(impl: BaseTransactionItemMapperImpl): BaseTransactionItemMapper = impl

    @Provides
    @Singleton
    fun provideTransactionHistoryProcessor(impl: TransactionHistoryProcessorImpl): TransactionHistoryProcessor = impl

    @Provides
    fun provideTransactionHistoryPreviewManager(
        impl: TransactionHistoryPreviewManagerImpl
    ): TransactionHistoryPreviewManager = impl

    @Provides
    @Singleton
    fun provideTransactionLoadStatePreviewMapper(
        impl: TransactionLoadStatePreviewMapperImpl
    ): TransactionLoadStatePreviewMapper = impl

    @Provides
    @Singleton
    fun provideGetPendingTransactionItems(
        useCase: GetPendingTransactionItemsUseCase
    ): GetPendingTransactionItems = useCase

    @Provides
    @Singleton
    fun providePendingTransactionItemMapper(impl: PendingTransactionItemMapperImpl): PendingTransactionItemMapper = impl

    @Provides
    @Singleton
    fun provideGetTransactionTargetUserDisplayName(
        useCase: GetTransactionTargetUserDisplayNameUseCase
    ): GetTransactionTargetUserDisplayName = useCase
}
