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

package com.algorand.wallet.cache.domain.usecase

import com.algorand.wallet.account.info.domain.manager.AccountCacheManager
import com.algorand.wallet.account.info.domain.model.AccountCacheManagerStatus
import com.algorand.wallet.account.info.domain.model.AccountCacheStatus
import com.algorand.wallet.account.info.domain.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.wallet.cache.domain.model.AppCacheStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class GetAppCacheStatusFlowUseCase @Inject constructor(
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow,
    private val accountCacheManager: AccountCacheManager
) : GetAppCacheStatusFlow {

    override fun invoke(): Flow<AppCacheStatus> {
        return combine(
            getAccountDetailCacheStatusFlow(),
            accountCacheManager.cacheStatusFlow
        ) { accountCacheStatus, accountCacheManagerStatus ->
            when {
                !isInitializationStarted(accountCacheStatus) -> AppCacheStatus.IDLE
                isCacheInitialized(accountCacheStatus, accountCacheManagerStatus) -> AppCacheStatus.INITIALIZED
                else -> AppCacheStatus.LOADING
            }
        }
    }

    private fun isCacheInitialized(
        accountStatus: AccountCacheStatus,
        accountCacheManagerStatus: AccountCacheManagerStatus
    ): Boolean {
        val isAccountCacheInitialized = accountStatus == AccountCacheStatus.INITIALIZED
        val isAccountCacheManagerInitialized = accountCacheManagerStatus == AccountCacheManagerStatus.INITIALIZED
        return isAccountCacheInitialized && isAccountCacheManagerInitialized
    }

    private fun isInitializationStarted(accountStatus: AccountCacheStatus): Boolean {
        return accountStatus != AccountCacheStatus.IDLE
    }
}
