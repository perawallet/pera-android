package com.algorand.android.core.component.creation.di

import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.core.component.creation.domain.usecase.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountCreationModule {

    @Provides
    @Singleton
    fun provideCreateNoAuthAccount(useCase: CreateNoAuthAccountUseCase): CreateNoAuthAccount = useCase

    @Provides
    @Singleton
    fun provideCreateAlgo25Account(useCase: CreateAlgo25AccountUseCase): CreateAlgo25Account = useCase

    @Provides
    @Singleton
    fun provideCreateLedgerBleAccount(useCase: CreateLedgerBleAccountUseCase): CreateLedgerBleAccount = useCase

    @Provides
    @Singleton
    fun provideCreateLedgerUsbAccount(useCase: CreateLedgerUsbAccountUseCase): CreateLedgerUsbAccount = useCase
}
