package com.algorand.android.asb.component.backupprotocol.di

import com.algorand.android.asb.component.backupprotocol.mapper.*
import com.algorand.android.asb.component.backupprotocol.mapper.BackUpProtocolContentMapper
import com.algorand.android.asb.component.backupprotocol.usecase.*
import com.algorand.android.asb.component.backupprotocol.usecase.implementation.*
import com.algorand.android.asb.component.backupprotocol.usecase.implementation.CreateAsbBackUpFilePayloadUseCase
import com.algorand.android.asb.component.backupprotocol.usecase.implementation.CreateBackUpProtocolPayloadUseCase
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object BackUpProtocolModule {

    @Provides
    @Singleton
    fun provideBackUpProtocolContentMapper(
        mapper: BackUpProtocolContentMapperImpl
    ): BackUpProtocolContentMapper = mapper

    @Provides
    @Singleton
    fun provideCreateAsbBackUpFilePayload(
        useCase: CreateAsbBackUpFilePayloadUseCase
    ): CreateAsbBackUpFilePayload = useCase

    @Provides
    @Singleton
    fun provideCreateBackUpProtocolPayload(
        useCase: CreateBackUpProtocolPayloadUseCase
    ): CreateBackUpProtocolPayload = useCase
}
