@file:Suppress("TooManyFunctions")
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

package com.algorand.android.module.algosdk.transaction.di

import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.android.module.algosdk.network.GetAlgodInterceptorNodeDetails
import com.algorand.android.module.algosdk.network.GetAlgodInterceptorNodeDetailsImpl
import com.algorand.android.module.algosdk.transaction.AlgoSdkTransaction
import com.algorand.android.module.algosdk.transaction.AlgoSdkTransactionImpl
import com.algorand.android.module.algosdk.transaction.AlgoTransactionSigner
import com.algorand.android.module.algosdk.transaction.AlgoTransactionSignerImpl
import com.algorand.android.module.algosdk.transaction.mapper.AddAssetTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.AlgoTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.AssetTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.RekeyTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.RemoveAssetTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.SendAndRemoveAssetTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.SuggestedParamsMapper
import com.algorand.android.module.algosdk.transaction.mapper.TransactionMappers
import com.algorand.android.module.algosdk.transaction.mapper.implementation.AddAssetTransactionMapperImpl
import com.algorand.android.module.algosdk.transaction.mapper.implementation.AlgoTransactionMapperImpl
import com.algorand.android.module.algosdk.transaction.mapper.implementation.AssetTransactionMapperImpl
import com.algorand.android.module.algosdk.transaction.mapper.implementation.RekeyTransactionMapperImpl
import com.algorand.android.module.algosdk.transaction.mapper.implementation.RemoveAssetTransactionMapperImpl
import com.algorand.android.module.algosdk.transaction.mapper.implementation.SendAndRemoveAssetTransactionMapperImpl
import com.algorand.android.module.algosdk.transaction.mapper.implementation.SuggestedParamsMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@Module
@InstallIn(SingletonComponent::class)
internal object AlgoSdkTransactionModule {

    @Provides
    @Singleton
    fun provideSuggestedParamsMapper(mapper: SuggestedParamsMapperImpl): SuggestedParamsMapper = mapper

    @Provides
    @Singleton
    fun provideAddAssetTransactionMapper(
        mapper: AddAssetTransactionMapperImpl
    ): AddAssetTransactionMapper = mapper

    @Provides
    @Singleton
    fun provideAlgoTransactionMapper(
        mapper: AlgoTransactionMapperImpl
    ): AlgoTransactionMapper = mapper

    @Provides
    @Singleton
    fun provideAssetTransactionMapper(
        mapper: AssetTransactionMapperImpl
    ): AssetTransactionMapper = mapper

    @Provides
    @Singleton
    fun provideRekeyTransactionMapper(
        mapper: RekeyTransactionMapperImpl
    ): RekeyTransactionMapper = mapper

    @Provides
    @Singleton
    fun provideRemoveAssetTransactionMapper(
        mapper: RemoveAssetTransactionMapperImpl
    ): RemoveAssetTransactionMapper = mapper

    @Provides
    @Singleton
    fun provideSendAndRemoveAssetTransactionMapper(
        mapper: SendAndRemoveAssetTransactionMapperImpl
    ): SendAndRemoveAssetTransactionMapper = mapper

    @Provides
    fun provideAlgodClient(
        getAlgodInterceptorNodeDetails: GetAlgodInterceptorNodeDetails
    ): AlgodClient {
        val algodInterceptorNodeDetails = getAlgodInterceptorNodeDetails()
        val currentNodeAlgodAddress = algodInterceptorNodeDetails.baseUrl.toHttpUrlOrNull()!!
        return AlgodClient(
            currentNodeAlgodAddress.toString(),
            currentNodeAlgodAddress.port,
            algodInterceptorNodeDetails.apiKey
        )
    }

    @Provides
    @Singleton
    fun provideGetAlgodInterceptorNodeDetails(
        impl: GetAlgodInterceptorNodeDetailsImpl
    ): GetAlgodInterceptorNodeDetails = impl

    @Provides
    @Singleton
    fun provideAlgoSdkTransaction(
        addAssetTransactionMapper: AddAssetTransactionMapper,
        algoTransactionMapper: AlgoTransactionMapper,
        assetTransactionMapper: AssetTransactionMapper,
        rekeyTransactionMapper: RekeyTransactionMapper,
        removeAssetTransactionMapper: RemoveAssetTransactionMapper,
        sendAndRemoveAssetTransactionMapper: SendAndRemoveAssetTransactionMapper,
        algodClient: AlgodClient
    ): AlgoSdkTransaction {
        val mappers = TransactionMappers(
            addAssetTransactionMapper,
            rekeyTransactionMapper,
            algoTransactionMapper,
            assetTransactionMapper,
            removeAssetTransactionMapper,
            sendAndRemoveAssetTransactionMapper
        )
        return AlgoSdkTransactionImpl(mappers, algodClient)
    }

    @Provides
    @Singleton
    fun provideAlgoTransactionSigner(
        impl: AlgoTransactionSignerImpl
    ): AlgoTransactionSigner = impl
}
