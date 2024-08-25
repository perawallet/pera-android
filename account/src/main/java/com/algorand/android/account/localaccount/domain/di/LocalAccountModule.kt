package com.algorand.android.account.localaccount.domain.di

import com.algorand.android.account.localaccount.domain.repository.*
import com.algorand.android.account.localaccount.domain.repository.Algo25AccountRepository
import com.algorand.android.account.localaccount.domain.repository.LedgerBleAccountRepository
import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.account.localaccount.domain.usecase.implementation.*
import dagger.*
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
