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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class IsThereAnyCachedErrorAccountUseCase @Inject constructor(
    private val accountInformationRepository: AccountInformationRepository,
    private val getLocalAccounts: GetLocalAccounts
) : IsThereAnyCachedErrorAccount {

    override suspend fun invoke(excludeNoAuthAccounts: Boolean): Boolean {
        return isThereAnyCachedErrorAccount(getLocalAccounts(), excludeNoAuthAccounts)
    }

    override suspend fun invoke(localAccounts: List<LocalAccount>, excludeNoAuthAccounts: Boolean): Boolean {
        return isThereAnyCachedErrorAccount(localAccounts, excludeNoAuthAccounts)
    }

    private suspend fun isThereAnyCachedErrorAccount(
        localAccounts: List<LocalAccount>,
        excludeNoAuthAccounts: Boolean
    ): Boolean {
        val failedAccounts = accountInformationRepository.getFailedAccountInformation()
        val filteredLocalAccounts = if (excludeNoAuthAccounts) {
            localAccounts.filter { it !is LocalAccount.NoAuth }
        } else {
            localAccounts
        }

        return filteredLocalAccounts.any { localAccount ->
            failedAccounts.any { address ->
                localAccount.algoAddress == address
            }
        }
    }
}
