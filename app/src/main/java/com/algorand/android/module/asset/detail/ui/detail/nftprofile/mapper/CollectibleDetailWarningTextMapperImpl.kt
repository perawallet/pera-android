/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.asset.detail.ui.detail.nftprofile.mapper

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.R
import javax.inject.Inject

internal class CollectibleDetailWarningTextMapperImpl @Inject constructor() : CollectibleDetailWarningTextMapper {

    override fun mapToWarningTextResId(prismUrl: String?): Int? {
        return if (prismUrl.isNullOrBlank()) {
            R.string.we_can_t_display
        } else {
            null
        }
    }

    override fun mapToOptedInWarningTextResId(isOwnedByTheUser: Boolean, accountType: AccountType?): Int? {
        if (isOwnedByTheUser) return null
        return when (accountType) {
            AccountType.Algo25, AccountType.LedgerBle, AccountType.Rekeyed, AccountType.RekeyedAuth -> {
                R.string.you_are_not_the_owner
            }
            AccountType.NoAuth -> {
                R.string.this_watch_account_has_opted
            }
            else -> null
        }
    }
}
