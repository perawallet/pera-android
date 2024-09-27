@file:Suppress("TooManyFunctions")
package com.algorand.android.module.account.local.domain.di

import com.algorand.android.module.account.local.domain.repository.Algo25AccountRepository
import com.algorand.android.module.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.android.module.account.local.domain.repository.LedgerUsbAccountRepository
import com.algorand.android.module.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.android.module.account.local.domain.usecase.AddAlgo25Account
import com.algorand.android.module.account.local.domain.usecase.AddLedgerBleAccount
import com.algorand.android.module.account.local.domain.usecase.AddLedgerUsbAccount
import com.algorand.android.module.account.local.domain.usecase.AddNoAuthAccount
import com.algorand.android.module.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.android.module.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.local.domain.usecase.GetSecretKey
import com.algorand.android.module.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.module.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.android.module.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25
import com.algorand.android.module.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBle
import com.algorand.android.module.account.local.domain.usecase.implementation.DeleteLocalAccountUseCase
import com.algorand.android.module.account.local.domain.usecase.implementation.GetAllLocalAccountAddressesAsFlowUseCase
import com.algorand.android.module.account.local.domain.usecase.implementation.GetLocalAccountsUseCase
import com.algorand.android.module.account.local.domain.usecase.implementation.GetSecretKeyUseCase
import com.algorand.android.module.account.local.domain.usecase.implementation.IsThereAnyAccountWithAddressUseCase
import com.algorand.android.module.account.local.domain.usecase.implementation.IsThereAnyLocalAccountUseCase
import com.algorand.android.module.account.local.domain.usecase.implementation.UpdateNoAuthAccountToAlgo25UseCase
import com.algorand.android.module.account.local.domain.usecase.implementation.UpdateNoAuthAccountToLedgerBleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalAccountModule {

    @Provides
    @Singleton
    fun provideGetLocalAccounts(useCase: GetLocalAccountsUseCase): GetLocalAccounts = useCase

    @Provides
    @Singleton
    fun provideIsThereAnyLocalAccount(useCase: IsThereAnyLocalAccountUseCase): IsThereAnyLocalAccount = useCase

    @Provides
    @Singleton
    fun provideGetAllLocalAccountAddressesAsFlow(
        useCase: GetAllLocalAccountAddressesAsFlowUseCase
    ): GetAllLocalAccountAddressesAsFlow = useCase

    @Provides
    @Singleton
    fun provideDeleteLocalAccount(useCase: DeleteLocalAccountUseCase): DeleteLocalAccount = useCase

    @Provides
    @Singleton
    fun provideGetSecretKey(useCase: GetSecretKeyUseCase): GetSecretKey = useCase

    @Provides
    @Singleton
    fun provideIsThereAnyAccountWithAddress(
        useCase: IsThereAnyAccountWithAddressUseCase
    ): IsThereAnyAccountWithAddress = useCase

    @Provides
    @Singleton
    fun provideUpdateNoAuthAccountToAlgo25(
        useCase: UpdateNoAuthAccountToAlgo25UseCase
    ): UpdateNoAuthAccountToAlgo25 = useCase

    @Provides
    @Singleton
    fun provideUpdateNoAuthAccountToLedgerBle(
        useCase: UpdateNoAuthAccountToLedgerBleUseCase
    ): UpdateNoAuthAccountToLedgerBle = useCase

    @Provides
    @Singleton
    fun provideAddAlgo25Account(
        algo25AccountRepository: Algo25AccountRepository
    ): AddAlgo25Account = AddAlgo25Account(algo25AccountRepository::addAccount)

    @Provides
    @Singleton
    fun provideAddLedgerBleAccount(
        ledgerBleAccountRepository: LedgerBleAccountRepository
    ): AddLedgerBleAccount = AddLedgerBleAccount(ledgerBleAccountRepository::addAccount)

    @Provides
    @Singleton
    fun provideAddNoAuthAccount(
        noAuthAccountRepository: NoAuthAccountRepository
    ): AddNoAuthAccount = AddNoAuthAccount(noAuthAccountRepository::addAccount)

    @Provides
    @Singleton
    fun provideAddLedgerUsbAccount(
        ledgerUsbAccountRepository: LedgerUsbAccountRepository
    ): AddLedgerUsbAccount = AddLedgerUsbAccount(ledgerUsbAccountRepository::addAccount)
}
