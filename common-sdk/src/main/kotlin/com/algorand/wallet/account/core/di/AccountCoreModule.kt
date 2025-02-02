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

package com.algorand.wallet.account.core.di

import com.algorand.wallet.account.core.domain.usecase.AddAlgo25Account
import com.algorand.wallet.account.core.domain.usecase.AddAlgo25AccountUseCase
import com.algorand.wallet.account.core.domain.usecase.AddHdKeyAccount
import com.algorand.wallet.account.core.domain.usecase.AddHdKeyAccountUseCase
import com.algorand.wallet.account.core.domain.usecase.AddLedgerBleAccount
import com.algorand.wallet.account.core.domain.usecase.AddLedgerBleAccountUseCase
import com.algorand.wallet.account.core.domain.usecase.AddNoAuthAccount
import com.algorand.wallet.account.core.domain.usecase.AddNoAuthAccountUseCase
import com.algorand.wallet.account.core.domain.usecase.CacheAccountDetail
import com.algorand.wallet.account.core.domain.usecase.CacheAccountDetailUseCase
import com.algorand.wallet.account.core.domain.usecase.DeleteAccount
import com.algorand.wallet.account.core.domain.usecase.DeleteAccountUseCase
import com.algorand.wallet.account.core.domain.usecase.FetchAccountInformationAndCacheAssets
import com.algorand.wallet.account.core.domain.usecase.FetchAccountInformationAndCacheAssetsUseCase
import com.algorand.wallet.account.core.domain.usecase.GetAccountDetailFlow
import com.algorand.wallet.account.core.domain.usecase.GetAccountDetailFlowUseCase
import com.algorand.wallet.account.core.domain.usecase.GetAccountMinBalance
import com.algorand.wallet.account.core.domain.usecase.GetAccountMinBalanceUseCase
import com.algorand.wallet.account.core.domain.usecase.GetAccountsDetailsFlow
import com.algorand.wallet.account.core.domain.usecase.GetAccountsDetailsFlowUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AccountCoreModule {

    @Provides
    fun provideAddAlgo25Account(useCase: AddAlgo25AccountUseCase): AddAlgo25Account = useCase

    @Provides
    fun provideAddLedgerBleAccount(useCase: AddLedgerBleAccountUseCase): AddLedgerBleAccount = useCase

    @Provides
    fun provideAddNoAuthAccount(useCase: AddNoAuthAccountUseCase): AddNoAuthAccount = useCase

    @Provides
    fun provideAddHdKeyAccount(useCase: AddHdKeyAccountUseCase): AddHdKeyAccount = useCase

    @Provides
    fun provideDeleteAccount(useCase: DeleteAccountUseCase): DeleteAccount = useCase

    @Provides
    fun provideGetAccountDetailFlow(useCase: GetAccountDetailFlowUseCase): GetAccountDetailFlow = useCase

    @Provides
    fun provideGetAccountsDetailsFlow(useCase: GetAccountsDetailsFlowUseCase): GetAccountsDetailsFlow = useCase

    @Provides
    fun provideCacheAccountDetail(useCase: CacheAccountDetailUseCase): CacheAccountDetail = useCase

    @Provides
    fun provideFetchAccountInformationAndCacheAssets(
        useCase: FetchAccountInformationAndCacheAssetsUseCase
    ): FetchAccountInformationAndCacheAssets = useCase

    @Provides
    fun provideGetAccountMinBalance(useCase: GetAccountMinBalanceUseCase): GetAccountMinBalance = useCase
}
