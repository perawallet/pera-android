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

package com.algorand.android.transaction.domain.di

import com.algorand.android.transaction.domain.sign.SignTransactionManager
import com.algorand.android.transaction.domain.sign.mapper.LedgerBleResultSignTransactionResultMapper
import com.algorand.android.transaction.domain.sign.mapper.LedgerBleResultSignTransactionResultMapperImpl
import com.algorand.android.transaction.domain.sign.mapper.SignTransactionNotReadyToSignToSignTransactionResultMapper
import com.algorand.android.transaction.domain.sign.mapper.SignTransactionNotReadyToSignToSignTransactionResultMapperImpl
import com.algorand.android.transaction.domain.sign.usecase.GetSignTransactionPreparedness
import com.algorand.android.transaction.domain.sign.usecase.SignTransactionManagerImpl
import com.algorand.android.transaction.domain.sign.usecase.SignTransactionWithLedgerManager
import com.algorand.android.transaction.domain.sign.usecase.implementation.GetSignTransactionPreparednessUseCase
import com.algorand.android.transaction.domain.sign.usecase.implementation.SignTransactionWithLedgerManagerImpl
import com.algorand.android.transaction.domain.usecase.CalculateRekeyFee
import com.algorand.android.transaction.domain.usecase.CalculateTransactionFee
import com.algorand.android.transaction.domain.usecase.SendSignedAddAssetTransaction
import com.algorand.android.transaction.domain.usecase.SendSignedRemoveAssetTransaction
import com.algorand.android.transaction.domain.usecase.implementation.CalculateRekeyFeeUseCase
import com.algorand.android.transaction.domain.usecase.implementation.CalculateTransactionFeeUseCase
import com.algorand.android.transaction.domain.usecase.implementation.SendSignedAddAssetTransactionUseCase
import com.algorand.android.transaction.domain.usecase.implementation.SendSignedRemoveAssetTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionComponentModule {

    @Provides
    @Singleton
    fun provideCalculateTransactionFee(
        useCase: CalculateTransactionFeeUseCase
    ): CalculateTransactionFee = useCase

    @Provides
    @Singleton
    fun provideCalculateRekeyFee(useCase: CalculateRekeyFeeUseCase): CalculateRekeyFee = useCase

    @Provides
    fun provideSignTransactionWithLedgerManager(
        impl: SignTransactionWithLedgerManagerImpl
    ): SignTransactionWithLedgerManager = impl

    @Provides
    @Singleton
    fun provideLedgerBleResultSignTransactionResultMapper(
        impl: LedgerBleResultSignTransactionResultMapperImpl
    ): LedgerBleResultSignTransactionResultMapper = impl

    @Provides
    @Singleton
    fun provideSignTransactionNotReadyToSignToSignTransactionResultMapper(
        impl: SignTransactionNotReadyToSignToSignTransactionResultMapperImpl
    ): SignTransactionNotReadyToSignToSignTransactionResultMapper = impl

    @Provides
    fun provideSignTransactionManager(impl: SignTransactionManagerImpl): SignTransactionManager = impl

    @Provides
    @Singleton
    fun provideGetSignTransactionPreparedness(
        useCase: GetSignTransactionPreparednessUseCase
    ): GetSignTransactionPreparedness = useCase

    @Provides
    @Singleton
    fun provideSendSignedRemoveAssetTransaction(
        useCase: SendSignedRemoveAssetTransactionUseCase
    ): SendSignedRemoveAssetTransaction = useCase

    @Provides
    @Singleton
    fun provideSendSignedAddAssetTransaction(
        useCase: SendSignedAddAssetTransactionUseCase
    ): SendSignedAddAssetTransaction = useCase
}
