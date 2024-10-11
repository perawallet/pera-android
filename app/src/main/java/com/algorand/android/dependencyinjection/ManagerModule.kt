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

package com.algorand.android.dependencyinjection

import com.algorand.android.core.AccountManager
import com.algorand.android.modules.accountblockpolling.domain.usecase.ClearLastKnownBlockForAccountsUseCase
import com.algorand.android.modules.accountblockpolling.domain.usecase.GetResultWhetherAccountsUpdateIsRequiredUseCase
import com.algorand.android.modules.accountblockpolling.domain.usecase.UpdateLastKnownBlockUseCase
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.usecase.AssetInboxAllAccountsUseCase
import com.algorand.android.modules.currency.domain.usecase.CurrencyUseCase
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.usecase.AccountCacheStatusUseCase
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.usecase.AssetFetchAndCacheUseCase
import com.algorand.android.usecase.SimpleAssetDetailUseCase
import com.algorand.android.utils.AccountDetailUpdateHelper
import com.algorand.android.utils.coremanager.AccountDetailCacheManager
import com.algorand.android.utils.coremanager.AssetCacheManager
import com.algorand.android.utils.coremanager.ParityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Singleton
    @Provides
    fun provideAlgoPriceManager(
        parityUseCase: ParityUseCase,
        currencyUseCase: CurrencyUseCase
    ): ParityManager {
        return ParityManager(parityUseCase, currencyUseCase)
    }

    @Singleton
    @Provides
    fun provideAccountDetailCacheManager(
        getResultWhetherAccountsUpdateIsRequiredUseCase: GetResultWhetherAccountsUpdateIsRequiredUseCase,
        updateLastKnownBlockUseCase: UpdateLastKnownBlockUseCase,
        clearLastKnownBlockForAccountsUseCase: ClearLastKnownBlockForAccountsUseCase,
        accountDetailUseCase: AccountDetailUseCase,
        accountManager: AccountManager,
        accountDetailUpdateHelper: AccountDetailUpdateHelper,
        assetInboxAllAccountsUseCase: AssetInboxAllAccountsUseCase
    ): AccountDetailCacheManager {
        return AccountDetailCacheManager(
            getResultWhetherAccountsUpdateIsRequiredUseCase,
            updateLastKnownBlockUseCase,
            clearLastKnownBlockForAccountsUseCase,
            accountDetailUseCase,
            accountManager,
            accountDetailUpdateHelper,
            assetInboxAllAccountsUseCase
        )
    }

    @Singleton
    @Provides
    fun provideAssetCacheManager(
        accountCacheStatusUseCase: AccountCacheStatusUseCase,
        simpleAssetDetailUseCase: SimpleAssetDetailUseCase,
        accountDetailUseCase: AccountDetailUseCase,
        assetFetchAndCacheUseCase: AssetFetchAndCacheUseCase
    ): AssetCacheManager {
        return AssetCacheManager(
            accountCacheStatusUseCase,
            simpleAssetDetailUseCase,
            accountDetailUseCase,
            assetFetchAndCacheUseCase
        )
    }
}
