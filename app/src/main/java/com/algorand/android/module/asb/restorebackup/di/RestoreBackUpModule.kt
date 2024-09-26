package com.algorand.android.module.asb.restorebackup.di

import com.algorand.android.module.asb.restorebackup.domain.usecase.RestoreAsbCipherText
import com.algorand.android.module.asb.restorebackup.domain.usecase.RestoreBackUpPayload
import com.algorand.android.module.asb.restorebackup.domain.usecase.implementation.RestoreAsbCipherTextUseCase
import com.algorand.android.module.asb.restorebackup.domain.usecase.implementation.RestoreBackUpPayloadUseCase
import com.algorand.android.module.asb.restorebackup.validation.AsbBackUpProtocolContentValidator
import com.algorand.android.module.asb.restorebackup.validation.AsbBackUpProtocolContentValidatorUseCase
import com.algorand.android.module.asb.restorebackup.validation.AsbFileContentValidator
import com.algorand.android.module.asb.restorebackup.validation.AsbFileContentValidatorUseCase
import dagger.Module
import dagger.Provides
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
