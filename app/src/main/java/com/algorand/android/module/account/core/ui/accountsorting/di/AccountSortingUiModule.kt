package com.algorand.android.module.account.core.ui.accountsorting.di

import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.*
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.*
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.*
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation.*
import com.algorand.android.module.account.sorting.domain.usecase.GetAccountSortingTypeIdentifier
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountSortingUiModule {

    @Provides
    @Singleton
    fun provideAccountAndAssetAccountListItemMapper(
        mapper: AccountAndAssetAccountListItemMapperImpl
    ): AccountAndAssetAccountListItemMapper = mapper

    @Provides
    @Singleton
    fun provideSortAccountsBySortingPreference(
        getAccountSortingTypeIdentifier: GetAccountSortingTypeIdentifier
    ): SortAccountsBySortingPreference {
        return SortAccountsBySortingPreferenceUseCase(
            getAccountSortingTypeIdentifier,
            AccountListItemSorterByAlphabeticallyAscending(),
            AccountListItemSorterByAlphabeticallyDescending(),
            AccountListItemSorterByNumericalAscending(),
            AccountListItemSorterByNumericalDescending(),
            AccountListItemSorterByManually()
        )
    }

    @Provides
    @Singleton
    fun provideGetSortedAccountsByPreference(
        useCase: GetSortedAccountsByPreferenceUseCase
    ): GetSortedAccountsByPreference = useCase

    @Provides
    @Singleton
    fun provideGetDefaultSortingType(
        useCase: GetDefaultAccountSortingTypeUseCase
    ): GetDefaultAccountSortingType = useCase

    @Provides
    @Singleton
    fun provideBaseAccountAndAssetListItemMapper(
        impl: BaseAccountAndAssetListItemMapperImpl
    ): BaseAccountAndAssetListItemMapper = impl

    @Provides
    @Singleton
    fun provideGetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction(
        useCase: GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransactionUseCase
    ): GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction = useCase

    @Provides
    @Singleton
    fun provideGetFilteredSortedAccountListWhichNotBackedUp(
        useCase: GetFilteredSortedAccountListWhichNotBackedUpUseCase
    ): GetFilteredSortedAccountListWhichNotBackedUp = useCase
}
