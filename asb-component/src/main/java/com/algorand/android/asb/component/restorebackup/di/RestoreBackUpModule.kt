package com.algorand.android.asb.component.restorebackup.di

import com.algorand.android.asb.component.restorebackup.domain.usecase.*
import com.algorand.android.asb.component.restorebackup.domain.usecase.implementation.*
import com.algorand.android.asb.component.restorebackup.domain.usecase.implementation.RestoreAsbCipherTextUseCase
import com.algorand.android.asb.component.restorebackup.domain.usecase.implementation.RestoreBackUpPayloadUseCase
import com.algorand.android.asb.component.restorebackup.validation.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RestoreBackUpModule {

    @Provides
    @Singleton
    fun provideRestoreAsbCipherText(useCase: RestoreAsbCipherTextUseCase): RestoreAsbCipherText = useCase

    @Provides
    @Singleton
    fun provideAsbBackUpProtocolContentValidator(
        useCase: AsbBackUpProtocolContentValidatorUseCase
    ): AsbBackUpProtocolContentValidator = useCase

    @Provides
    @Singleton
    fun provideAsbFileContentValidator(
        useCase: AsbFileContentValidatorUseCase
    ): AsbFileContentValidator = useCase

    @Provides
    @Singleton
    fun provideRestoreBackUpPayload(
        useCase: RestoreBackUpPayloadUseCase
    ): RestoreBackUpPayload = useCase
}
