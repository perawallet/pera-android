/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountsorting.di

import com.algorand.android.modules.accountsorting.domain.usecase.GetAccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.ui.domain.mapper.AccountAndAssetAccountListItemMapper
import com.algorand.android.modules.accountsorting.ui.domain.mapper.AccountAndAssetAccountListItemMapperImpl
import com.algorand.android.modules.accountsorting.ui.domain.mapper.BaseAccountAndAssetListItemMapper
import com.algorand.android.modules.accountsorting.ui.domain.mapper.BaseAccountAndAssetListItemMapperImpl
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetDefaultAccountSortingType
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetFilteredSortedAccountListWhichNotBackedUp
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.modules.accountsorting.ui.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter.AccountListItemSorterByAlphabeticallyAscending
import com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter.AccountListItemSorterByAlphabeticallyDescending
import com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter.AccountListItemSorterByManually
import com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter.AccountListItemSorterByNumericalAscending
import com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter.AccountListItemSorterByNumericalDescending
import com.algorand.android.modules.accountsorting.ui.domain.usecase.implementation.GetDefaultAccountSortingTypeUseCase
import com.algorand.android.modules.accountsorting.ui.domain.usecase.implementation.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransactionUseCase
import com.algorand.android.modules.accountsorting.ui.domain.usecase.implementation.GetFilteredSortedAccountListWhichNotBackedUpUseCase
import com.algorand.android.modules.accountsorting.ui.domain.usecase.implementation.GetSortedAccountsByPreferenceUseCase
import com.algorand.android.modules.accountsorting.ui.domain.usecase.implementation.SortAccountsBySortingPreferenceUseCase
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
