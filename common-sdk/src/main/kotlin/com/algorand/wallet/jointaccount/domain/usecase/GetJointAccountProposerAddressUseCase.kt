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

package com.algorand.wallet.jointaccount.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class GetJointAccountProposerAddressUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val getAccountType: GetAccountType
) : GetJointAccountProposerAddress {

    override suspend fun invoke(jointAccount: LocalAccount.Joint): String? {
        val localAccounts = getLocalAccounts()
        val localAccountAddresses = localAccounts.map { it.algoAddress }.toSet()

        var ledgerFallbackAddress: String? = null
        for (participantAddress in jointAccount.participantAddresses) {
            if (participantAddress !in localAccountAddresses) continue

            val accountType = getAccountType(participantAddress) ?: continue
            if (!accountType.canDirectlySign()) continue

            if (accountType is AccountType.LedgerBle) {
                if (ledgerFallbackAddress == null) ledgerFallbackAddress = participantAddress
            } else {
                return participantAddress
            }
        }
        return ledgerFallbackAddress
    }

    private fun AccountType.canDirectlySign(): Boolean {
        return this is AccountType.Algo25 ||
            this is AccountType.HdKey ||
            this is AccountType.LedgerBle ||
            this is AccountType.RekeyedAuth
    }
}
