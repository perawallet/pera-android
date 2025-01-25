/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.algosdk.di

import com.algorand.android.modules.algosdk.domain.mapper.TransactionParametersResponseMapper
import com.algorand.android.modules.algosdk.domain.mapper.TransactionParametersResponseMapperImpl
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOfflineTransaction
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOfflineTransactionImpl
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOnlineTransaction
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOnlineTransactionImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AlgoSdkModule {

    @Provides
    fun provideBuildKeyRegOnlineTransaction(
        impl: BuildKeyRegOnlineTransactionImpl
    ): BuildKeyRegOnlineTransaction = impl

    @Provides
    fun provideBuildKeyRegOfflineTransaction(
        impl: BuildKeyRegOfflineTransactionImpl
    ): BuildKeyRegOfflineTransaction = impl

    @Provides
    fun provideTransactionParametersResponseMapper(
        impl: TransactionParametersResponseMapperImpl
    ): TransactionParametersResponseMapper = impl
}
