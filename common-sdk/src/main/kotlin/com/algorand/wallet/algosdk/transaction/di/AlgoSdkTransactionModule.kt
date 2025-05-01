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

package com.algorand.wallet.algosdk.transaction.di

import com.algorand.wallet.algosdk.transaction.builders.AddAssetTransactionBuilder
import com.algorand.wallet.algosdk.transaction.builders.AddAssetTransactionBuilderBuilderImpl
import com.algorand.wallet.algosdk.transaction.builders.AlgoTransactionBuilder
import com.algorand.wallet.algosdk.transaction.builders.AlgoTransactionBuilderImpl
import com.algorand.wallet.algosdk.transaction.builders.RekeyTransactionBuilder
import com.algorand.wallet.algosdk.transaction.builders.RekeyTransactionBuilderImpl
import com.algorand.wallet.algosdk.transaction.builders.RemoveAssetTransactionBuilder
import com.algorand.wallet.algosdk.transaction.builders.RemoveAssetTransactionBuilderImpl
import com.algorand.wallet.algosdk.transaction.builders.SendAndRemoveAssetTransactionBuilder
import com.algorand.wallet.algosdk.transaction.builders.SendAndRemoveAssetTransactionBuilderImpl
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddress
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddressImpl
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkImpl
import com.algorand.wallet.algosdk.transaction.sdk.AlgoTransactionSigner
import com.algorand.wallet.algosdk.transaction.sdk.AlgoTransactionSignerImpl
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39SdkImpl
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransactionImpl
import com.algorand.wallet.algosdk.transaction.sdk.mapper.SuggestedParamsMapper
import com.algorand.wallet.algosdk.transaction.sdk.mapper.SuggestedParamsMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AlgoSdkTransactionModule {

    @Provides
    fun provideSuggestedParamsMapper(mapper: SuggestedParamsMapperImpl): SuggestedParamsMapper = mapper

    @Provides
    fun provideAddAssetTransactionBuilder(
        impl: AddAssetTransactionBuilderBuilderImpl
    ): AddAssetTransactionBuilder = impl

    @Provides
    fun provideAlgoTransactionBuilder(impl: AlgoTransactionBuilderImpl): AlgoTransactionBuilder = impl

    @Provides
    fun provideAssetTransactionBuilder(
        impl: AddAssetTransactionBuilderBuilderImpl
    ): AddAssetTransactionBuilder = impl

    @Provides
    fun provideRekeyTransactionBuilder(impl: RekeyTransactionBuilderImpl): RekeyTransactionBuilder = impl

    @Provides
    fun provideRemoveAssetTransactionBuilder(
        impl: RemoveAssetTransactionBuilderImpl
    ): RemoveAssetTransactionBuilder = impl

    @Provides
    fun provideSendAndRemoveAssetTransactionMapper(
        impl: SendAndRemoveAssetTransactionBuilderImpl
    ): SendAndRemoveAssetTransactionBuilder = impl

    @Provides
    fun provideAlgoSdk(impl: AlgoSdkImpl): AlgoSdk = impl

    @Provides
    fun provideAlgoTransactionSigner(impl: AlgoTransactionSignerImpl): AlgoTransactionSigner = impl

    @Provides
    fun provideAlgoSdkAddress(impl: AlgoSdkAddressImpl): AlgoSdkAddress = impl

    @Provides
    fun providePeraBip39Sdk(impl: PeraBip39SdkImpl): PeraBip39Sdk = impl

    @Provides
    fun provideSignHdKeyTransaction(impl: SignHdKeyTransactionImpl): SignHdKeyTransaction = impl
}
