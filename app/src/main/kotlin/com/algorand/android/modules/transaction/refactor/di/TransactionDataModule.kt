/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.transaction.refactor.di

import com.algorand.android.modules.transaction.refactor.usecase.CreateAddAssetTransactionData
import com.algorand.android.modules.transaction.refactor.usecase.CreateAddAssetTransactionDataUseCase
import com.algorand.android.modules.transaction.refactor.usecase.CreateRekeyTransactionData
import com.algorand.android.modules.transaction.refactor.usecase.CreateRekeyTransactionDataUseCase
import com.algorand.android.modules.transaction.refactor.usecase.CreateRemoveAssetTransactionData
import com.algorand.android.modules.transaction.refactor.usecase.CreateRemoveAssetTransactionDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionDataModule {

    @Provides
    fun provideCreateRekeyTransactionData(
        useCase: CreateRekeyTransactionDataUseCase
    ): CreateRekeyTransactionData = useCase

    @Provides
    fun provideCreateRemoveAssetTransactionData(
        useCase: CreateRemoveAssetTransactionDataUseCase
    ): CreateRemoveAssetTransactionData = useCase

    @Provides
    fun provideCreateAddAssetTransactionData(
        useCase: CreateAddAssetTransactionDataUseCase
    ): CreateAddAssetTransactionData = useCase
}
