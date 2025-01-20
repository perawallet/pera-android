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

package com.algorand.wallet.account.detail.di

import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetailUseCase
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationTypeUseCase
import com.algorand.wallet.account.detail.domain.usecase.GetAccountState
import com.algorand.wallet.account.detail.domain.usecase.GetAccountStateUseCase
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountTypeUseCase
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetailsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AccountDetailModule {

    @Provides
    fun provideGetAccountType(useCase: GetAccountTypeUseCase): GetAccountType = useCase

    @Provides
    fun provideGetAccountState(useCase: GetAccountStateUseCase): GetAccountState = useCase

    @Provides
    fun provideGetAccountRegistrationType(
        useCase: GetAccountRegistrationTypeUseCase
    ): GetAccountRegistrationType = useCase

    @Provides
    fun provideGetAccountsDetails(useCase: GetAccountsDetailsUseCase): GetAccountsDetails = useCase

    @Provides
    fun provideGetAccountDetail(useCase: GetAccountDetailUseCase): GetAccountDetail = useCase
}