/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.accountcore.ui.accountselection.di

import com.algorand.android.accountcore.ui.accountselection.mapper.AccountSelectionListItemMapper
import com.algorand.android.accountcore.ui.accountselection.mapper.AccountSelectionListItemMapperImpl
import com.algorand.android.accountcore.ui.accountselection.usecase.CreateLoadedAccountConfiguration
import com.algorand.android.accountcore.ui.accountselection.usecase.CreateLoadedAccountConfigurationUseCase
import com.algorand.android.accountcore.ui.accountselection.usecase.CreateNotLoadedAccountConfiguration
import com.algorand.android.accountcore.ui.accountselection.usecase.CreateNotLoadedAccountConfigurationUseCase
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountItems
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountItemsUseCase
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountsWhichCanSignTransaction
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountsWhichCanSignTransactionUseCase
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionContactItems
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionContactItemsUseCase
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionItemsFromAccountAddress
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionItemsFromAccountAddressUseCase
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionNameServiceItems
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionNameServiceItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountSelectionUiModule {

    @Provides
    @Singleton
    fun provideCreateLoadedAccountConfiguration(
        useCase: CreateLoadedAccountConfigurationUseCase
    ): CreateLoadedAccountConfiguration = useCase

    @Provides
    @Singleton
    fun provideGetAccountSelectionAccountsWithCanSignTransaction(
        useCase: GetAccountSelectionAccountsWhichCanSignTransactionUseCase
    ): GetAccountSelectionAccountsWhichCanSignTransaction = useCase

    @Provides
    @Singleton
    fun provideCreateNotLoadedAccountConfiguration(
        useCase: CreateNotLoadedAccountConfigurationUseCase
    ): CreateNotLoadedAccountConfiguration = useCase

    @Provides
    @Singleton
    fun provideAccountSelectionListItemMapper(
        mapper: AccountSelectionListItemMapperImpl
    ): AccountSelectionListItemMapper = mapper

    @Provides
    @Singleton
    fun provideGetAccountSelectionAccountItems(
        useCase: GetAccountSelectionAccountItemsUseCase
    ): GetAccountSelectionAccountItems = useCase

    @Provides
    @Singleton
    fun provideGetAccountSelectionContactItems(
        useCase: GetAccountSelectionContactItemsUseCase
    ): GetAccountSelectionContactItems = useCase

    @Provides
    @Singleton
    fun provideGetAccountSelectionItemsFromAccountAddress(
        useCase: GetAccountSelectionItemsFromAccountAddressUseCase
    ): GetAccountSelectionItemsFromAccountAddress = useCase

    @Provides
    @Singleton
    fun provideGetAccountSelectionNameServiceItems(
        useCase: GetAccountSelectionNameServiceItemsUseCase
    ): GetAccountSelectionNameServiceItems = useCase
}
