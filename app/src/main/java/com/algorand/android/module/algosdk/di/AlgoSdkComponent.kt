package com.algorand.android.module.algosdk.di

import com.algorand.android.module.algosdk.AlgoSdkAddress
import com.algorand.android.module.algosdk.AlgoSdkAddressImpl
import com.algorand.android.module.algosdk.AlgoSdkBackUp
import com.algorand.android.module.algosdk.AlgoSdkBackUpImpl
import com.algorand.android.module.algosdk.AlgoSdkEncryption
import com.algorand.android.module.algosdk.AlgoSdkEncryptionImpl
import com.algorand.android.module.algosdk.transaction.ParseTransactionMessagePack
import com.algorand.android.module.algosdk.transaction.ParseTransactionMessagePackUseCase
import com.algorand.android.module.algosdk.transaction.mapper.RawTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.RawTransactionMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AlgoSdkComponent {

    @Provides
    @Singleton
    fun provideAlgoSdkBackUp(impl: AlgoSdkBackUpImpl): AlgoSdkBackUp = impl

    @Provides
    @Singleton
    fun provideAlgoSdkEncryption(impl: AlgoSdkEncryptionImpl): AlgoSdkEncryption = impl

    @Provides
    @Singleton
    fun provideAlgoSdkAddress(impl: AlgoSdkAddressImpl): AlgoSdkAddress = impl

    @Provides
    @Singleton
    fun provideRawTransactionMapper(mapper: RawTransactionMapperImpl): RawTransactionMapper = mapper

    @Provides
    @Singleton
    fun provideParseTransactionByteArray(useCase: ParseTransactionMessagePackUseCase): ParseTransactionMessagePack =
        useCase
}
