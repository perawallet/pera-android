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

package com.algorand.android.modules.accounts.di

import com.algorand.android.modules.accounts.domain.usecase.GetAuthAddressOfAnAccount
import com.algorand.android.modules.accounts.domain.usecase.IsSenderRekeyedToAnotherAccount
import com.algorand.android.usecase.AccountDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountsModule {

    @Provides
    @Singleton
    fun provideIsSenderRekeyedToAnotherAccount(
        useCase: AccountDetailUseCase
    ): IsSenderRekeyedToAnotherAccount = IsSenderRekeyedToAnotherAccount(useCase::isAccountRekeyed)

    @Provides
    @Singleton
    fun provideGetAuthAddressOfAnAccount(
        useCase: AccountDetailUseCase
    ): GetAuthAddressOfAnAccount = GetAuthAddressOfAnAccount(useCase::getAuthAddress)
}
