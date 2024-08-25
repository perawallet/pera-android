package com.algorand.android.core.component.di

import com.algorand.android.core.component.detail.domain.usecase.GetAccountsDetailFlow
import com.algorand.android.core.component.detail.domain.usecase.implementation.GetAccountsDetailFlowUseCase
import com.algorand.android.core.component.domain.mapper.AlgoAssetDataMapper
import com.algorand.android.core.component.domain.mapper.AlgoAssetDataMapperImpl
import com.algorand.android.core.component.domain.mapper.OwnedAssetDataMapper
import com.algorand.android.core.component.domain.mapper.OwnedAssetDataMapperImpl
import com.algorand.android.core.component.domain.usecase.AddAccount
import com.algorand.android.core.component.domain.usecase.AddAccountUseCase
import com.algorand.android.core.component.domain.usecase.DeleteAccount
import com.algorand.android.core.component.domain.usecase.DeleteAccountUseCase
import com.algorand.android.core.component.domain.usecase.GetAccountCollectiblesData
import com.algorand.android.core.component.domain.usecase.GetAccountCollectiblesDataUseCase
import com.algorand.android.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.core.component.domain.usecase.GetAccountMinBalanceUseCase
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValue
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValueFlow
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValueFlowUseCase
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValueUseCase
import com.algorand.android.core.component.domain.usecase.GetNotBackedUpAccounts
import com.algorand.android.core.component.domain.usecase.GetNotBackedUpAccountsUseCase
import com.algorand.android.core.component.domain.usecase.GetRekeyedAccountAddresses
import com.algorand.android.core.component.domain.usecase.GetRekeyedAccountAddressesUseCase
import com.algorand.android.core.component.domain.usecase.HasAccountAnyRekeyedAccount
import com.algorand.android.core.component.domain.usecase.HasAccountAnyRekeyedAccountUseCase
import com.algorand.android.core.component.domain.usecase.UpdateAccountName
import com.algorand.android.core.component.domain.usecase.UpdateAccountNameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountCoreModule {

    @Provides
    @Singleton
    fun provideSetAccountName(useCase: UpdateAccountNameUseCase): UpdateAccountName = useCase

    @Provides
    @Singleton
    fun provideGetNotBackedUpAccounts(useCase: GetNotBackedUpAccountsUseCase): GetNotBackedUpAccounts = useCase

    @Provides
    @Singleton
    fun provideGetAccountTotalValue(useCase: GetAccountTotalValueUseCase): GetAccountTotalValue = useCase

    @Provides
    @Singleton
    fun provideAddAccount(useCase: AddAccountUseCase): AddAccount = useCase

    @Provides
    @Singleton
    fun provideAlgoAssetDataMapper(mapper: AlgoAssetDataMapperImpl): AlgoAssetDataMapper = mapper

    @Provides
    @Singleton
    fun provideOwnedAssetDataMapper(mapper: OwnedAssetDataMapperImpl): OwnedAssetDataMapper = mapper

    @Provides
    @Singleton
    fun provideGetAccountMinBalance(useCase: GetAccountMinBalanceUseCase): GetAccountMinBalance = useCase

    @Provides
    @Singleton
    fun provideDeleteAccount(useCase: DeleteAccountUseCase): DeleteAccount = useCase

    @Provides
    @Singleton
    fun provideGetAccountCollectiblesData(
        useCase: GetAccountCollectiblesDataUseCase
    ): GetAccountCollectiblesData = useCase

    @Provides
    @Singleton
    fun provideGetAccountTotalValueFlow(useCase: GetAccountTotalValueFlowUseCase): GetAccountTotalValueFlow = useCase

    @Provides
    @Singleton
    fun provideHasAccountAnyRekeyedAccount(
        useCase: HasAccountAnyRekeyedAccountUseCase
    ): HasAccountAnyRekeyedAccount = useCase

    @Provides
    @Singleton
    fun provideGetRekeyedAccountAddresses(
        useCase: GetRekeyedAccountAddressesUseCase
    ): GetRekeyedAccountAddresses = useCase

    @Provides
    @Singleton
    fun provideGetAccountsDetailFlow(useCase: GetAccountsDetailFlowUseCase): GetAccountsDetailFlow = useCase
}
