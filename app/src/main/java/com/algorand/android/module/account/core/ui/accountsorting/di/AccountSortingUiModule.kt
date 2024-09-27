package com.algorand.android.module.account.core.ui.accountsorting.di

import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.AccountAndAssetAccountListItemMapper
import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.AccountAndAssetAccountListItemMapperImpl
import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.BaseAccountAndAssetListItemMapper
import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.BaseAccountAndAssetListItemMapperImpl
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetDefaultAccountSortingType
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetFilteredSortedAccountListWhichNotBackedUp
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.AccountListItemSorterByAlphabeticallyAscending
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.AccountListItemSorterByAlphabeticallyDescending
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.AccountListItemSorterByManually
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.AccountListItemSorterByNumericalAscending
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.AccountListItemSorterByNumericalDescending
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation.GetDefaultAccountSortingTypeUseCase
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransactionUseCase
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation.GetFilteredSortedAccountListWhichNotBackedUpUseCase
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation.GetSortedAccountsByPreferenceUseCase
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation.SortAccountsBySortingPreferenceUseCase
import com.algorand.android.module.account.sorting.domain.usecase.GetAccountSortingTypeIdentifier
import dagger.Module
import dagger.Provides
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
