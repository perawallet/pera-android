/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.algorand.android.modules.accounts.lite.domain.manager

import com.algorand.android.models.Node
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.CurrencyCachingError
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.EmptyLocalAccounts
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteInitializationStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLitesFlow
import com.algorand.android.modules.accountsorting.ui.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.modules.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.usecase.NodeSettingsUseCase
import com.algorand.android.utils.CacheResult
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsFlow
import com.algorand.wallet.cache.domain.model.AppCacheStatus
import com.algorand.wallet.cache.domain.usecase.GetAppCacheStatusFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

@Singleton
internal class AccountLiteManagerImpl @Inject constructor(
    private val getAccountLitesFlow: GetAccountLitesFlow,
    private val getLocalAccountsFlow: GetLocalAccountsFlow,
    private val getAppCacheStatusFlow: GetAppCacheStatusFlow,
    private val parityUseCase: ParityUseCase,
    private val nodeSettingsUseCase: NodeSettingsUseCase,
    private val sortAccountsBySortingPreference: SortAccountsBySortingPreference
) : AccountLiteManager {

    private val _localAccountLitesFlow = MutableStateFlow<AccountLiteCacheStatus>(AccountLiteCacheStatus.Idle)
    override val localAccountLitesFlow: StateFlow<AccountLiteCacheStatus>
        get() = _localAccountLitesFlow.asStateFlow()

    override fun initialize(scope: CoroutineScope) {
        combine(
            getLocalAccountsFlow().distinctUntilChanged(),
            getAppCacheStatusFlow(),
            parityUseCase.getSelectedCurrencyDetailCacheFlow(),
            nodeSettingsUseCase.getAllNodeAsFlow(),
            ::getAccountLiteInitializationStatus
        )
            .distinctUntilChanged()
            .flatMapLatest(::getAccountLiteCacheStatus).distinctUntilChanged()
            .onEach {
                _localAccountLitesFlow.value = it
            }.launchIn(scope)
    }

    private fun getAccountLiteCacheStatus(
        initializationStatus: AccountLiteInitializationStatus
    ): Flow<AccountLiteCacheStatus> {
        return when (initializationStatus) {
            is AccountLiteInitializationStatus.CurrencyDetailError -> {
                flowOf(CurrencyCachingError(initializationStatus.error))
            }
            AccountLiteInitializationStatus.EmptyAccounts -> flowOf(EmptyLocalAccounts)
            AccountLiteInitializationStatus.Loading -> flowOf(AccountLiteCacheStatus.Loading)
            is AccountLiteInitializationStatus.ReadyForInitialization -> {
                val localAccounts = initializationStatus.accounts
                val addresses = localAccounts.map { it.algoAddress }
                getAccountLitesFlow(localAccounts, addresses).mapLatest {
                    val data = sortAccountsBySortingPreference.sortAccountLites(it)
                    AccountLiteCacheStatus.Data(localAccounts, data)
                }
            }
        }
    }

    private fun getAccountLiteInitializationStatus(
        localAccounts: List<LocalAccount>,
        appCacheStatus: AppCacheStatus,
        currencyStatus: CacheResult<SelectedCurrencyDetail>?,
        nodes: List<Node>
    ): AccountLiteInitializationStatus {
        return when {
            localAccounts.isEmpty() -> AccountLiteInitializationStatus.EmptyAccounts
            currencyStatus is CacheResult.Error -> {
                if (currencyStatus.data != null) {
                    getStatusWithAppCacheStatus(localAccounts, appCacheStatus)
                } else {
                    AccountLiteInitializationStatus.CurrencyDetailError(currencyStatus)
                }
            }
            currencyStatus is CacheResult.Success -> getStatusWithAppCacheStatus(localAccounts, appCacheStatus)
            currencyStatus == null -> AccountLiteInitializationStatus.Loading
            else -> AccountLiteInitializationStatus.Loading
        }
    }

    private fun getStatusWithAppCacheStatus(
        localAccounts: List<LocalAccount>,
        appCacheStatus: AppCacheStatus
    ): AccountLiteInitializationStatus {
        val isCacheNotAvailable = appCacheStatus != AppCacheStatus.INITIALIZED
        return if (isCacheNotAvailable) {
            AccountLiteInitializationStatus.Loading
        } else {
            AccountLiteInitializationStatus.ReadyForInitialization(localAccounts)
        }
    }
}
