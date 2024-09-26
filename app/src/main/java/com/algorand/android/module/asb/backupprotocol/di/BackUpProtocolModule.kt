package com.algorand.android.module.asb.backupprotocol.di

import com.algorand.android.module.asb.backupprotocol.mapper.BackUpProtocolContentMapper
import com.algorand.android.module.asb.backupprotocol.mapper.BackUpProtocolContentMapperImpl
import com.algorand.android.module.asb.backupprotocol.usecase.CreateAsbBackUpFilePayload
import com.algorand.android.module.asb.backupprotocol.usecase.CreateBackUpProtocolPayload
import com.algorand.android.module.asb.backupprotocol.usecase.implementation.CreateAsbBackUpFilePayloadUseCase
import com.algorand.android.module.asb.backupprotocol.usecase.implementation.CreateBackUpProtocolPayloadUseCase
import dagger.Module
import dagger.Provides
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
