package com.algorand.android.algosdk.component.di

import com.algorand.android.algosdk.component.*
import com.algorand.android.algosdk.component.transaction.ParseTransactionMessagePack
import com.algorand.android.algosdk.component.transaction.ParseTransactionMessagePackUseCase
import com.algorand.android.algosdk.component.transaction.mapper.RawTransactionMapper
import com.algorand.android.algosdk.component.transaction.mapper.RawTransactionMapperImpl
import dagger.*
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
