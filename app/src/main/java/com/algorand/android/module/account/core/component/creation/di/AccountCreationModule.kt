package com.algorand.android.module.account.core.component.creation.di

import com.algorand.android.module.account.core.component.creation.domain.usecase.CreateAlgo25AccountUseCase
import com.algorand.android.module.account.core.component.creation.domain.usecase.CreateLedgerBleAccountUseCase
import com.algorand.android.module.account.core.component.creation.domain.usecase.CreateLedgerUsbAccountUseCase
import com.algorand.android.module.account.core.component.creation.domain.usecase.CreateNoAuthAccountUseCase
import com.algorand.android.module.account.local.domain.usecase.CreateAlgo25Account
import com.algorand.android.module.account.local.domain.usecase.CreateLedgerBleAccount
import com.algorand.android.module.account.local.domain.usecase.CreateLedgerUsbAccount
import com.algorand.android.module.account.local.domain.usecase.CreateNoAuthAccount
import dagger.Module
import dagger.Provides
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
