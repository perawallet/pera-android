/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.collectibles.detail.base.domain.decider

import com.algorand.android.R
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Algo25
import com.algorand.wallet.account.detail.domain.model.AccountType.HdKey
import com.algorand.wallet.account.detail.domain.model.AccountType.LedgerBle
import com.algorand.wallet.account.detail.domain.model.AccountType.NoAuth
import com.algorand.wallet.account.detail.domain.model.AccountType.Rekeyed
import com.algorand.wallet.account.detail.domain.model.AccountType.RekeyedAuth
import javax.inject.Inject

class CollectibleDetailDecider @Inject constructor() {

    // TODO: 4.03.2022 Handle other error cases 
    fun decideWarningTextRes(prismUrl: String?): Int? {
        return if (prismUrl.isNullOrBlank()) {
            R.string.we_can_t_display
        } else {
            null
        }
    }

    fun decideOptedInWarningTextRes(isOwnedByTheUser: Boolean, accountType: AccountType?): Int? {
        if (isOwnedByTheUser) return null
        return when (accountType) {
            Algo25, LedgerBle, Rekeyed, RekeyedAuth, HdKey -> {
                R.string.you_are_not_the_owner
            }
            NoAuth -> {
                R.string.this_watch_account_has_opted
            }
            else -> null
        }
    }
}
