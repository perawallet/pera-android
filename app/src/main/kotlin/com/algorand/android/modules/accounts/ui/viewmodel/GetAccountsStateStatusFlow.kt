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

package com.algorand.android.modules.accounts.ui.viewmodel

import com.algorand.android.modules.accounts.ui.model.AccountsInitializationStatus
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.usecase.NodeSettingsUseCase
import com.algorand.android.utils.CacheResult
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsFlow
import com.algorand.wallet.cache.domain.model.AppCacheStatus
import com.algorand.wallet.cache.domain.usecase.GetAppCacheStatusFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart

class GetAccountsStateStatusFlow @Inject constructor(
    private val parityUseCase: ParityUseCase,
    private val getAppCacheStatusFlow: GetAppCacheStatusFlow,
    private val getLocalAccountsFlow: GetLocalAccountsFlow,
    private val nodeSettingsUseCase: NodeSettingsUseCase,
) {

    operator fun invoke(): Flow<AccountsInitializationStatus> {
        return combine(
            getLocalAccountsFlow().distinctUntilChanged(),
            getAppCacheStatusFlow().distinctUntilChanged(),
            parityUseCase.getSelectedCurrencyDetailCacheFlow(),
            nodeSettingsUseCase.getAllNodeAsFlow()
        ) { localAccounts, appCacheStatus, currencyStatus, _ ->
            when {
                localAccounts.isEmpty() -> AccountsInitializationStatus.EmptyAccounts
                currencyStatus is CacheResult.Error -> {
                    if (currencyStatus.data != null) {
                        getStatusWithAppCacheStatus(localAccounts, appCacheStatus)
                    } else {
                        AccountsInitializationStatus.CurrencyDetailError(currencyStatus)
                    }
                }
                currencyStatus is CacheResult.Success -> getStatusWithAppCacheStatus(localAccounts, appCacheStatus)
                currencyStatus == null -> AccountsInitializationStatus.Loading
                else -> AccountsInitializationStatus.Loading
            }
        }.onStart {
            emit(AccountsInitializationStatus.Loading)
        }
    }

    private fun getStatusWithAppCacheStatus(
        localAccounts: List<LocalAccount>,
        appCacheStatus: AppCacheStatus
    ): AccountsInitializationStatus {
        val isCacheNotAvailable = appCacheStatus != AppCacheStatus.INITIALIZED
        return if (isCacheNotAvailable) {
            AccountsInitializationStatus.Loading
        } else {
            AccountsInitializationStatus.ReadyForInitialization(localAccounts)
        }
    }
}
