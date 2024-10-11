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

package com.algorand.android.modules.assetinbox.detail.di

import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.mapper.Arc59ClaimTransactionPayloadMapper
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.mapper.Arc59ClaimTransactionPayloadMapperImpl
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.mapper.Arc59RejectTransactionPayloadMapper
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.mapper.Arc59RejectTransactionPayloadMapperImpl
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase.CreateArc59ClaimTransaction
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase.CreateArc59ClaimTransactionUseCase
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase.CreateArc59RejectTransaction
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase.CreateArc59RejectTransactionUseCase
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.mapper.Arc59ReceiveDetailPreviewMapper
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.mapper.Arc59ReceiveDetailPreviewMapperImpl
import com.algorand.android.modules.assetinbox.detail.transactiondetail.mapper.Arc59TransactionDetailPreviewMapper
import com.algorand.android.modules.assetinbox.detail.transactiondetail.mapper.Arc59TransactionDetailPreviewMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Arc59ReceiveModule {

    @Provides
    @Singleton
    fun provideArc59TransactionDetailPreviewMapper(
        impl: Arc59TransactionDetailPreviewMapperImpl
    ): Arc59TransactionDetailPreviewMapper = impl

    @Provides
    @Singleton
    fun provideCreateArc59ClaimTransaction(
        useCase: CreateArc59ClaimTransactionUseCase
    ): CreateArc59ClaimTransaction = useCase

    @Provides
    @Singleton
    fun provideCreateArc59RejectTransaction(
        useCase: CreateArc59RejectTransactionUseCase
    ): CreateArc59RejectTransaction = useCase

    @Provides
    @Singleton
    fun provideArc59ReceiveDetailPreviewMapper(
        impl: Arc59ReceiveDetailPreviewMapperImpl
    ): Arc59ReceiveDetailPreviewMapper = impl

    @Provides
    @Singleton
    fun provideArc59ClaimTransactionPayloadMapper(
        impl: Arc59ClaimTransactionPayloadMapperImpl
    ): Arc59ClaimTransactionPayloadMapper = impl

    @Provides
    @Singleton
    fun provideArc59RejectTransactionPayloadMapper(
        impl: Arc59RejectTransactionPayloadMapperImpl
    ): Arc59RejectTransactionPayloadMapper = impl
}
