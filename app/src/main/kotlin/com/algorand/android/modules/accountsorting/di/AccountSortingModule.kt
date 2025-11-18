/*
 * Copyright 2022-2025 Pera Wallet, LDA
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

import android.content.SharedPreferences
import com.algorand.android.modules.accountsorting.data.repository.AccountSortingRepositoryImpl
import com.algorand.android.modules.accountsorting.data.storage.AccountSortPreferencesLocalSource
import com.algorand.android.modules.accountsorting.domain.repository.AccountSortingRepository
import com.algorand.android.modules.accountsorting.domain.usecase.GetAccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.modules.accountsorting.domain.usecase.SaveAccountSortPreference
import com.algorand.android.modules.accountsorting.domain.usecase.implementation.GetSortedLocalAccountsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountSortingModule {

    @Provides
    @Singleton
    fun provideAccountSortingRepository(sharedPreferences: SharedPreferences): AccountSortingRepository {
        return AccountSortingRepositoryImpl(AccountSortPreferencesLocalSource(sharedPreferences))
    }

    @Provides
    @Singleton
    fun provideGetSortedLocalAccounts(useCase: GetSortedLocalAccountsUseCase): GetSortedLocalAccounts = useCase

    @Provides
    @Singleton
    fun provideGetAccountSortingTypeIdentifier(repository: AccountSortingRepository): GetAccountSortingTypeIdentifier {
        return GetAccountSortingTypeIdentifier(repository::getAccountSortPreference)
    }

    @Provides
    @Singleton
    fun provideSaveAccountSortPreference(repository: AccountSortingRepository): SaveAccountSortPreference {
        return SaveAccountSortPreference(repository::saveAccountSortPreference)
    }
}
